package ge.mmo.common.security;

/** Thrown when a JWT cannot be trusted (bad signature, expired, malformed, wrong issuer). */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
