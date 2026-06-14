namespace Watcher.Net
{
    /// <summary>
    /// Mirror of the WebSocket contract (contracts/ws/protocol.md). Every message is a JSON
    /// envelope { "type": ..., "data": { ... } }.
    /// </summary>
    public static class Protocol
    {
        // Commands (client -> server)
        public const string CmdSessionHello = "SESSION_HELLO"; // { token }
        public const string CmdEnterWorld = "ENTER_WORLD";     // { characterId }
        public const string CmdMove = "MOVE";                  // { x, y, z }
        public const string CmdPing = "PING";

        // Events (server -> client)
        public const string EvtSessionWelcome = "SESSION_WELCOME"; // { accountId, username }
        public const string EvtWorldEntered = "WORLD_ENTERED";     // { characterId, name, era }
        public const string EvtWorldSnapshot = "WORLD_SNAPSHOT";   // { entities: [...] }
        public const string EvtEntityJoined = "ENTITY_JOINED";     // { characterId, name, x, y, z }
        public const string EvtEntityMoved = "ENTITY_MOVED";       // { characterId, x, y, z }
        public const string EvtEntityLeft = "ENTITY_LEFT";         // { characterId }
        public const string EvtPong = "PONG";
        public const string EvtError = "ERROR";                    // { code, message }
    }
}
