package ge.mmo.auth.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accounts;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accounts, PasswordEncoder passwordEncoder) {
        this.accounts = accounts;
        this.passwordEncoder = passwordEncoder;
    }

    /** Create a new account. The username is unique case-insensitively. */
    @Transactional
    public Account register(String username, String email, String rawPassword) {
        if (accounts.existsByUsernameIgnoreCase(username)) {
            throw new UsernameTakenException(username);
        }
        Account account = new Account(
                UUID.randomUUID(),
                username,
                (email == null || email.isBlank()) ? null : email,
                passwordEncoder.encode(rawPassword));
        return accounts.save(account);
    }

    /** Verify credentials and return the account, or throw on any mismatch. */
    @Transactional(readOnly = true)
    public Account authenticate(String username, String rawPassword) {
        Account account = accounts.findByUsernameIgnoreCase(username)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(rawPassword, account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return account;
    }

    @Transactional(readOnly = true)
    public Account require(UUID id) {
        return accounts.findById(id).orElseThrow(InvalidCredentialsException::new);
    }
}
