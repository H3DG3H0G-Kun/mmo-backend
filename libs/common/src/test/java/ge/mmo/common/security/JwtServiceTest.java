package ge.mmo.common.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-that-is-at-least-32-bytes-long!!";
    private static final String ISSUER = "mmo-test";

    @Test
    void issuesAndVerifiesRoundTrip() {
        JwtService jwt = new JwtService(SECRET, ISSUER, Duration.ofMinutes(15));
        UUID id = UUID.randomUUID();

        String token = jwt.issueAccessToken(id, "watcher", List.of("PLAYER"));
        AuthPrincipal principal = jwt.verify(token);

        assertThat(principal.accountId()).isEqualTo(id);
        assertThat(principal.username()).isEqualTo("watcher");
        assertThat(principal.roles()).containsExactly("PLAYER");
    }

    @Test
    void rejectsTamperedSignature() {
        JwtService issuer = new JwtService(SECRET, ISSUER, Duration.ofMinutes(15));
        JwtService attacker = new JwtService("a-totally-different-secret-key-32bytes!!", ISSUER, Duration.ofMinutes(15));
        String token = attacker.issueAccessToken(UUID.randomUUID(), "evil", List.of("PLAYER"));

        assertThatThrownBy(() -> issuer.verify(token)).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsExpiredToken() {
        Instant t0 = Instant.parse("2026-01-01T00:00:00Z");
        Clock issued = Clock.fixed(t0, ZoneOffset.UTC);
        Clock later = Clock.fixed(t0.plusSeconds(3600), ZoneOffset.UTC);

        String token = new JwtService(SECRET, ISSUER, Duration.ofMinutes(15), issued)
                .issueAccessToken(UUID.randomUUID(), "watcher", List.of("PLAYER"));

        JwtService verifier = new JwtService(SECRET, ISSUER, Duration.ofMinutes(15), later);
        assertThatThrownBy(() -> verifier.verify(token)).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsShortSecret() {
        assertThatThrownBy(() -> new JwtService("too-short", ISSUER, Duration.ofMinutes(15)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
