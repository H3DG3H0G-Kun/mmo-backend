package ge.mmo.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Issues and verifies HMAC-SHA256 access tokens. Framework-light on purpose so both the
 * auth-service (issuer) and every other service (verifier) can share one implementation
 * via {@code libs/common}. The token subject is the account id; the username and roles
 * ride as claims.
 */
public class JwtService {

    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLES = "roles";

    private final SecretKey key;
    private final String issuer;
    private final Duration accessTtl;
    private final Clock clock;

    public JwtService(String secret, String issuer, Duration accessTtl) {
        this(secret, issuer, accessTtl, Clock.systemUTC());
    }

    public JwtService(String secret, String issuer, Duration accessTtl, Clock clock) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 bytes (256 bits) for HS256; got " + bytes.length);
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.issuer = issuer;
        this.accessTtl = accessTtl;
        this.clock = clock;
    }

    /** Issue a signed access token for the given account. */
    public String issueAccessToken(UUID accountId, String username, List<String> roles) {
        Instant now = clock.instant();
        return Jwts.builder()
                .issuer(issuer)
                .subject(accountId.toString())
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_ROLES, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTtl)))
                .signWith(key)
                .compact();
    }

    /**
     * Verify a token and return its principal.
     *
     * @throws InvalidTokenException if the token is malformed, expired, or has a bad signature
     */
    @SuppressWarnings("unchecked")
    public AuthPrincipal verify(String token) {
        try {
            Claims claims = Jwts.parser()
                    .requireIssuer(issuer)
                    .verifyWith(key)
                    .clock(() -> Date.from(clock.instant()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID accountId = UUID.fromString(claims.getSubject());
            String username = claims.get(CLAIM_USERNAME, String.class);
            List<String> roles = claims.get(CLAIM_ROLES, List.class);
            return new AuthPrincipal(accountId, username, roles == null ? List.of() : List.copyOf(roles));
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException(e.getMessage(), e);
        }
    }

    public Duration accessTtl() {
        return accessTtl;
    }
}
