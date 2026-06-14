package ge.mmo.world.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.common.security.InvalidTokenException;
import ge.mmo.common.security.JwtService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.session.WorldSessionService;
import ge.mmo.world.world.Era;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameWebSocketHandlerTest {

    private final ObjectMapper json = new ObjectMapper();
    private JwtService jwt;
    private WorldSessionService sessions;
    private GameWebSocketHandler handler;

    private WebSocketSession session;
    private Map<String, Object> attrs;
    private List<String> sent;

    @BeforeEach
    void setUp() throws Exception {
        jwt = mock(JwtService.class);
        sessions = mock(WorldSessionService.class);
        handler = new GameWebSocketHandler(jwt, sessions);

        session = mock(WebSocketSession.class);
        attrs = new HashMap<>();
        sent = new ArrayList<>();
        when(session.getAttributes()).thenReturn(attrs);
        doAnswer(inv -> {
            sent.add(((TextMessage) inv.getArgument(0)).getPayload());
            return null;
        }).when(session).sendMessage(any());
    }

    private void receive(String payload) throws Exception {
        handler.handleTextMessage(session, new TextMessage(payload));
    }

    private String lastType() throws Exception {
        JsonNode node = json.readTree(sent.get(sent.size() - 1));
        return node.path("type").asText();
    }

    @Test
    void helloWithValidTokenWelcomes() throws Exception {
        UUID acc = UUID.randomUUID();
        when(jwt.verify("good-token")).thenReturn(new AuthPrincipal(acc, "watcher", List.of("PLAYER")));

        receive("""
                {"type":"SESSION_HELLO","data":{"token":"good-token"}}""");

        assertThat(lastType()).isEqualTo(WsProtocol.E_SESSION_WELCOME);
        assertThat(attrs).containsKey("principal");
    }

    @Test
    void helloWithBadTokenErrors() throws Exception {
        when(jwt.verify("bad")).thenThrow(new InvalidTokenException("nope", null));

        receive("""
                {"type":"SESSION_HELLO","data":{"token":"bad"}}""");

        assertThat(lastType()).isEqualTo(WsProtocol.E_ERROR);
        assertThat(attrs).doesNotContainKey("principal");
    }

    @Test
    void enterWorldBeforeHelloIsRejected() throws Exception {
        receive("""
                {"type":"ENTER_WORLD","data":{"characterId":"%s"}}""".formatted(UUID.randomUUID()));

        assertThat(lastType()).isEqualTo(WsProtocol.E_ERROR);
        JsonNode node = json.readTree(sent.getLast());
        assertThat(node.path("data").path("code").asText()).isEqualTo(WsProtocol.ERR_UNAUTHENTICATED);
    }

    @Test
    void enterWorldWithOwnedCharacterSucceeds() throws Exception {
        UUID acc = UUID.randomUUID();
        UUID charId = UUID.randomUUID();
        attrs.put("principal", new AuthPrincipal(acc, "watcher", List.of("PLAYER")));

        Era era = mock(Era.class);
        when(era.getId()).thenReturn(1);
        when(era.getCode()).thenReturn("PARNAVAZ");
        when(era.getName()).thenReturn("The Age of Parnavaz");
        PlayerCharacter character = mock(PlayerCharacter.class);
        when(character.getId()).thenReturn(charId);
        when(character.getName()).thenReturn("Mtsveli");
        when(character.getCurrentEra()).thenReturn(era);
        when(sessions.enterWorld(eq(acc), eq(charId))).thenReturn(Optional.of(character));

        receive("""
                {"type":"ENTER_WORLD","data":{"characterId":"%s"}}""".formatted(charId));

        assertThat(lastType()).isEqualTo(WsProtocol.E_WORLD_ENTERED);
    }

    @Test
    void enterWorldWithForeignCharacterIsNotFound() throws Exception {
        UUID acc = UUID.randomUUID();
        UUID charId = UUID.randomUUID();
        attrs.put("principal", new AuthPrincipal(acc, "watcher", List.of("PLAYER")));
        when(sessions.enterWorld(eq(acc), eq(charId))).thenReturn(Optional.empty());

        receive("""
                {"type":"ENTER_WORLD","data":{"characterId":"%s"}}""".formatted(charId));

        JsonNode node = json.readTree(sent.getLast());
        assertThat(node.path("type").asText()).isEqualTo(WsProtocol.E_ERROR);
        assertThat(node.path("data").path("code").asText()).isEqualTo(WsProtocol.ERR_NOT_FOUND);
    }

    @Test
    void pingPongs() throws Exception {
        receive("""
                {"type":"PING"}""");
        assertThat(lastType()).isEqualTo(WsProtocol.E_PONG);
    }

    @Test
    void unknownTypeErrors() throws Exception {
        receive("""
                {"type":"FLY_TO_MARS"}""");
        assertThat(lastType()).isEqualTo(WsProtocol.E_ERROR);
    }
}
