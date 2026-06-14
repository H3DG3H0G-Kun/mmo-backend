package ge.mmo.world.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Binds {@code mmo.jwt.*}. world-service only verifies tokens, so TTL is unused here. */
@ConfigurationProperties(prefix = "mmo.jwt")
public record JwtProperties(String secret, String issuer, Duration accessTtl) {
}
