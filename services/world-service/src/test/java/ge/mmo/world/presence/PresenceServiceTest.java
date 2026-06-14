package ge.mmo.world.presence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PresenceServiceTest {

    private PresenceService presence;
    private WebSocketSession s1;
    private WebSocketSession s2;
    private final UUID a = UUID.randomUUID();
    private final UUID b = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        presence = new PresenceService();
        s1 = mock(WebSocketSession.class);
        s2 = mock(WebSocketSession.class);
        when(s1.getId()).thenReturn("s1");
        when(s2.getId()).thenReturn("s2");
        when(s1.isOpen()).thenReturn(true);
        when(s2.isOpen()).thenReturn(true);
    }

    @Test
    void joinSeesExistingWatchersInSameEra() {
        List<Presence> firstOthers = presence.join(s1, a, "Alice", "PARNAVAZ");
        assertThat(firstOthers).isEmpty();

        List<Presence> secondOthers = presence.join(s2, b, "Bob", "PARNAVAZ");
        assertThat(secondOthers).hasSize(1);
        assertThat(secondOthers.getFirst().name()).isEqualTo("Alice");
    }

    @Test
    void othersInEraExcludesSelfAndOtherEras() {
        presence.join(s1, a, "Alice", "PARNAVAZ");
        presence.join(s2, b, "Bob", "PARNAVAZ");

        assertThat(presence.othersInEra("PARNAVAZ", "s1")).containsExactly(s2);
        assertThat(presence.othersInEra("GOLDEN_AGE", "s1")).isEmpty();
    }

    @Test
    void moveUpdatesPosition() {
        presence.join(s1, a, "Alice", "PARNAVAZ");
        presence.move("s1", 5, 6, 7);
        Presence p = presence.presenceOf("s1").orElseThrow();
        assertThat(p.x()).isEqualTo(5);
        assertThat(p.y()).isEqualTo(6);
        assertThat(p.z()).isEqualTo(7);
    }

    @Test
    void moveBeforeJoinIsEmpty() {
        assertThat(presence.move("ghost", 1, 1, 1)).isEmpty();
    }

    @Test
    void leaveRemovesFromEra() {
        presence.join(s1, a, "Alice", "PARNAVAZ");
        presence.join(s2, b, "Bob", "PARNAVAZ");

        assertThat(presence.leave(s1)).isPresent();
        assertThat(presence.othersInEra("PARNAVAZ", "s2")).isEmpty();
        assertThat(presence.presenceOf("s1")).isEmpty();
    }
}
