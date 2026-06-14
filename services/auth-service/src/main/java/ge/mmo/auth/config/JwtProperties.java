package ge.mmo.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Binds {@code mmo.jwt.*} configuration. */
@ConfigurationProperties(prefix = "mmo.jwt")
public record JwtProperties(String secret, String issuer, Duration accessTtl) {
}
