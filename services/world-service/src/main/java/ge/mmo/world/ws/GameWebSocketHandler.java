package ge.mmo.world.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.common.security.InvalidTokenException;
import ge.mmo.common.security.JwtService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.session.WorldSessionService;
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
 * JWT as the first message (the HTTP handshake itself is unauthenticated). After that the backend
 * validates every intention and decides the resulting state — never the client.
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final String ATTR_PRINCIPAL = "principal";

    // Own a Jackson 2 mapper for the simple JSON envelopes. (Spring Boot 4's managed bean is
    // Jackson 3 / tools.jackson; our envelope handling is trivial and self-contained here.)
    private final ObjectMapper json = new ObjectMapper();
    private final JwtService jwt;
    private final WorldSessionService sessions;

    public GameWebSocketHandler(JwtService jwt, WorldSessionService sessions) {
        this.jwt = jwt;
        this.sessions = sessions;
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
            case WsProtocol.C_PING -> send(session, WsProtocol.E_PONG, Map.of());
            default -> send(session, WsProtocol.E_ERROR,
                    error(WsProtocol.ERR_UNKNOWN_TYPE, "Unknown message type: " + type));
        }
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
            send(session, WsProtocol.E_ERROR,
                    error(WsProtocol.ERR_UNAUTHENTICATED, "Send SESSION_HELLO first"));
            return;
        }
        String raw = data.path("characterId").asText(null);
        UUID characterId;
        try {
            characterId = UUID.fromString(raw);
        } catch (Exception e) {
            send(session, WsProtocol.E_ERROR, error(WsProtocol.ERR_BAD_MESSAGE, "Invalid characterId"));
            return;
        }
        sessions.enterWorld(principal.accountId(), characterId).ifPresentOrElse(
                character -> trySend(session, WsProtocol.E_WORLD_ENTERED, worldEntered(character)),
                () -> trySend(session, WsProtocol.E_ERROR,
                        error(WsProtocol.ERR_NOT_FOUND, "Character not found or not yours")));
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
        session.sendMessage(new TextMessage(payload));
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
