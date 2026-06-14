package ge.mmo.auth.web.dto;

import ge.mmo.auth.account.Account;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String username,
        String email,
        List<String> roles,
        Instant createdAt) {

    public static AccountResponse of(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.roleList(),
                account.getCreatedAt());
    }
}
