package ge.mmo.auth.web.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        AccountResponse account) {

    public static TokenResponse bearer(String accessToken, long expiresInSeconds, AccountResponse account) {
        return new TokenResponse(accessToken, "Bearer", expiresInSeconds, account);
    }
}
