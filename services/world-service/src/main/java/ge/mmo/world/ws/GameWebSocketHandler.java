package ge.mmo.world.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.common.security.InvalidTokenException;
import ge.mmo.common.security.JwtService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.presence.Presence;
import ge.mmo.world.presence.PresenceService;
import ge.mmo.world.session.WorldSessionService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Drives a realtime session. The client authenticates by sending {@code SESSION_HELLO} with its
 * JWT first (the HTTP handshake itself is unauthenticated). After {@code ENTER_WORLD}, the
 * session has a live presence and exchanges movement with others in the same era (the
 * area-of-interest unit for v1). The backend validates every intention and decides state.
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final String ATTR_PRINCIPAL = "principal";

    // Own a Jackson 2 mapper for the simple JSON envelopes. (Spring Boot 4's managed bean is
    // Jackson 3 / tools.jackson; our envelope handling is trivial and self-contained here.)
    private final ObjectMapper json = new ObjectMapper();
    private final JwtService jwt;
    private final WorldSessionService sessions;
    private final PresenceService presence;

    public GameWebSocketHandler(JwtService jwt, WorldSessionService sessions, PresenceService presence) {
        this.jwt = jwt;
        this.sessions = sessions;
        this.presence = presence;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JsonNode root;
        try {
            root = json.readTree(message.getPayload());
        } catch (Exception e) {
            send(session, WsProtocol.E_ERROR, error(WsProtocol.ERR_BAD_MESSAGE, "Malformed JSON"));
            return;
        }
        String type = root.path("type").asText(null);
        JsonNode data = root.path("data");
        if (type == null) {
            send(session, WsProtocol.E_ERROR, error(WsProtocol.ERR_BAD_MESSAGE, "Missing 'type'"));
            return;
        }

        switch (type) {
            case WsProtocol.C_SESSION_HELLO -> onHello(session, data);
            case WsProtocol.C_ENTER_WORLD -> onEnterWorld(session, data);
            case WsProtocol.C_MOVE -> onMove(session, data);
            case WsProtocol.C_PING -> send(session, WsProtocol.E_PONG, Map.of());
            default -> send(session, WsProtocol.E_ERROR,
                    error(WsProtocol.ERR_UNKNOWN_TYPE, "Unknown message type: " + type));
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        presence.leave(session).ifPresent(p ->
                broadcast(p.eraCode(), session.getId(), WsProtocol.E_ENTITY_LEFT,
                        Map.of("characterId", p.characterId().toString())));
    }

    private void onHello(WebSocketSession session, JsonNode data) throws IOException {
        String token = data.path("token").asText(null);
        if (token == null || token.isBlank()) {
            send(session, WsProtocol.E_ERROR, error(WsProtocol.ERR_INVALID_TOKEN, "Missing token"));
            return;
        }
        try {
            AuthPrincipal principal = jwt.verify(token);
            session.getAttributes().put(ATTR_PRINCIPAL, principal);
            send(session, WsProtocol.E_SESSION_WELCOME,
                    Map.of("accountId", principal.accountId().toString(), "username", principal.username()));
        } catch (InvalidTokenException e) {
            send(session, WsProtocol.E_ERROR, error(WsProtocol.ERR_INVALID_TOKEN, "Token rejected"));
        }
    }

    private void onEnterWorld(WebSocketSession session, JsonNode data) throws IOException {
        AuthPrincipal principal = (AuthPrincipal) session.getAttributes().get(ATTR_PRINCIPAL);
        if (principal == null) {
            send(session, WsProtocol.E_ERROR, error(WsProtocol.ERR_UNAUTHENTICATED, "Send SESSION_HELLO first"));
            return;
        }
        UUID characterId;
        try {
            characterId = UUID.fromString(data.path("characterId").asText(null));
        } catch (Exception e) {
            send(session, WsProtocol.E_ERROR, error(WsProtocol.ERR_BAD_MESSAGE, "Invalid characterId"));
            return;
        }
        sessions.enterWorld(principal.accountId(), characterId).ifPresentOrElse(
                character -> spawnIntoWorld(session, character),
                () -> trySend(session, WsProtocol.E_ERROR,
                        error(WsProtocol.ERR_NOT_FOUND, "Character not found or not yours")));
    }

    private void spawnIntoWorld(WebSocketSession session, PlayerCharacter character) {
        trySend(session, WsProtocol.E_WORLD_ENTERED, worldEntered(character));
        String era = character.getCurrentEra().getCode();
        // Register presence; snapshot the others already here, then announce ourselves to them.
        var others = presence.join(session, character.getId(), character.getName(), era);
        trySend(session, WsProtocol.E_WORLD_SNAPSHOT,
                Map.of("entities", others.stream().map(this::entity).toList()));
        presence.presenceOf(session.getId()).ifPresent(self ->
                broadcast(era, session.getId(), WsProtocol.E_ENTITY_JOINED, entity(self)));
    }

    private void onMove(WebSocketSession session, JsonNode data) throws IOException {
        double x = data.path("x").asDouble(0);
        double y = data.path("y").asDouble(0);
        double z = data.path("z").asDouble(0);
        Presence moved = presence.move(session.getId(), x, y, z).orElse(null);
        if (moved == null) {
            send(session, WsProtocol.E_ERROR, error(WsProtocol.ERR_NOT_IN_WORLD, "Enter the world first"));
            return;
        }
        broadcast(moved.eraCode(), session.getId(), WsProtocol.E_ENTITY_MOVED, Map.of(
                "characterId", moved.characterId().toString(), "x", x, "y", y, "z", z));
    }

    /** Send an event to every other open session in the era. */
    private void broadcast(String eraCode, String exceptSessionId, String type, Object data) {
        String payload;
        try {
            payload = json.writeValueAsString(Map.of("type", type, "data", data));
        } catch (IOException e) {
            return;
        }
        TextMessage message = new TextMessage(payload);
        for (WebSocketSession s : presence.othersInEra(eraCode, exceptSessionId)) {
            try {
                synchronized (s) {
                    if (s.isOpen()) {
                        s.sendMessage(message);
                    }
                }
            } catch (IOException ignored) {
                // best effort; a failing peer will be cleaned up on its own close
            }
        }
    }

    private Map<String, Object> entity(Presence p) {
        return Map.of("characterId", p.characterId().toString(), "name", p.name(),
                "x", p.x(), "y", p.y(), "z", p.z());
    }

    private Map<String, Object> worldEntered(PlayerCharacter c) {
        return Map.of(
                "characterId", c.getId().toString(),
                "name", c.getName(),
                "era", Map.of(
                        "id", c.getCurrentEra().getId(),
                        "code", c.getCurrentEra().getCode(),
                        "name", c.getCurrentEra().getName()));
    }

    private Map<String, Object> error(String code, String message) {
        return Map.of("code", code, "message", message);
    }

    private void send(WebSocketSession session, String type, Object data) throws IOException {
        String payload = json.writeValueAsString(Map.of("type", type, "data", data));
        synchronized (session) {
            session.sendMessage(new TextMessage(payload));
        }
    }

    private void trySend(WebSocketSession session, String type, Object data) {
        try {
            send(session, type, data);
        } catch (IOException e) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException ignored) {
                // best effort
            }
        }
    }
}
