package ge.mmo.world.presence;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of who is present in the world and where. The era is the area-of-interest
 * unit for v1 — a Watcher sees and is seen by others in the same era. Ephemeral (rebuilt on
 * reconnect); durable world/social state lives in the DB. Thread-safe for concurrent sessions.
 */
@Service
public class PresenceService {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Presence> presenceBySession = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> sessionIdsByEra = new ConcurrentHashMap<>();

    /** Register presence; returns the other Watchers already present in the same era (snapshot). */
    public List<Presence> join(WebSocketSession session, UUID characterId, String name, String eraCode) {
        String sid = session.getId();
        Presence presence = new Presence(characterId, name, eraCode);
        sessions.put(sid, session);
        presenceBySession.put(sid, presence);
        Set<String> era = sessionIdsByEra.computeIfAbsent(eraCode, k -> ConcurrentHashMap.newKeySet());
        List<Presence> others = era.stream()
                .map(presenceBySession::get)
                .filter(p -> p != null)
                .toList();
        era.add(sid);
        return others;
    }

    /** Update a session's position; empty if the session has not entered the world. */
    public Optional<Presence> move(String sessionId, double x, double y, double z) {
        Presence presence = presenceBySession.get(sessionId);
        if (presence == null) {
            return Optional.empty();
        }
        presence.moveTo(x, y, z);
        return Optional.of(presence);
    }

    /** Deregister on disconnect; returns the removed presence (for an ENTITY_LEFT broadcast). */
    public Optional<Presence> leave(WebSocketSession session) {
        String sid = session.getId();
        sessions.remove(sid);
        Presence presence = presenceBySession.remove(sid);
        if (presence != null) {
            Set<String> era = sessionIdsByEra.get(presence.eraCode());
            if (era != null) {
                era.remove(sid);
            }
        }
        return Optional.ofNullable(presence);
    }

    /** Sessions of other Watchers in an era (for broadcasting), excluding the given session. */
    public List<WebSocketSession> othersInEra(String eraCode, String exceptSessionId) {
        Set<String> era = sessionIdsByEra.get(eraCode);
        if (era == null) {
            return List.of();
        }
        return era.stream()
                .filter(sid -> !sid.equals(exceptSessionId))
                .map(sessions::get)
                .filter(s -> s != null && s.isOpen())
                .toList();
    }

    public Optional<Presence> presenceOf(String sessionId) {
        return Optional.ofNullable(presenceBySession.get(sessionId));
    }
}
