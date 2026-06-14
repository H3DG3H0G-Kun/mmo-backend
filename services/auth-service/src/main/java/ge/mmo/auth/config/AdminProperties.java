package ge.mmo.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Binds {@code mmo.admin.*} — the bootstrap admin account. */
@ConfigurationProperties(prefix = "mmo.admin")
public record AdminProperties(String username, String password) {
}
