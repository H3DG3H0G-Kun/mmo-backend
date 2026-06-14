package ge.mmo.common.security;

import java.util.List;
import java.util.UUID;

/**
 * The verified identity carried by a JWT. Transport-independent: the same principal is
 * reconstructed whether the token arrived over REST (Authorization header) or the
 * WebSocket handshake. Services treat this as the authenticated caller.
 */
public record AuthPrincipal(UUID accountId, String username, List<String> roles) {
}
