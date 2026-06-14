package ge.mmo.world.ws;

/**
 * The realtime message contract — transport-independent on purpose. Every message is a JSON
 * envelope {@code {"type": <name>, "data": {...}}}. Today it rides WebSocket/JSON; tomorrow it
 * can ride UDP/ENet/protobuf without the game logic changing (see GAME_DESIGN / BUILD_PROMPT).
 */
public final class WsProtocol {
    private WsProtocol() {
    }

    // --- Commands: client -> server (intentions) ---
    public static final String C_SESSION_HELLO = "SESSION_HELLO"; // {token}
    public static final String C_ENTER_WORLD = "ENTER_WORLD";     // {characterId}
    public static final String C_PING = "PING";

    // --- Events: server -> client (decisions/state) ---
    public static final String E_SESSION_WELCOME = "SESSION_WELCOME"; // {accountId, username}
    public static final String E_WORLD_ENTERED = "WORLD_ENTERED";     // {characterId, name, era}
    public static final String E_PONG = "PONG";
    public static final String E_ERROR = "ERROR";                     // {code, message}

    // --- Error codes ---
    public static final String ERR_BAD_MESSAGE = "BAD_MESSAGE";
    public static final String ERR_UNAUTHENTICATED = "UNAUTHENTICATED";
    public static final String ERR_INVALID_TOKEN = "INVALID_TOKEN";
    public static final String ERR_NOT_FOUND = "NOT_FOUND";
    public static final String ERR_UNKNOWN_TYPE = "UNKNOWN_TYPE";
}
