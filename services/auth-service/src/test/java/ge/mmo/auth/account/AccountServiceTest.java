package ge.mmo.auth.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    private AccountRepository accounts;
    private AccountService service;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        accounts = mock(AccountRepository.class);
        service = new AccountService(accounts, encoder);
    }

    @Test
    void registerHashesPasswordAndSaves() {
        when(accounts.existsByUsernameIgnoreCase("watcher")).thenReturn(false);
        when(accounts.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        Account created = service.register("watcher", "w@example.com", "supersecret");

        assertThat(created.getUsername()).isEqualTo("watcher");
        assertThat(created.getPasswordHash()).isNotEqualTo("supersecret");
        assertThat(encoder.matches("supersecret", created.getPasswordHash())).isTrue();
        assertThat(created.roleList()).containsExactly("PLAYER");
    }

    @Test
    void registerRejectsDuplicateUsername() {
        when(accounts.existsByUsernameIgnoreCase("watcher")).thenReturn(true);

        assertThatThrownBy(() -> service.register("watcher", null, "supersecret"))
                .isInstanceOf(UsernameTakenException.class);
    }

    @Test
    void authenticateAcceptsCorrectPassword() {
        Account stored = new Account(UUID.randomUUID(), "watcher", null, encoder.encode("supersecret"));
        when(accounts.findByUsernameIgnoreCase("watcher")).thenReturn(Optional.of(stored));

        assertThat(service.authenticate("watcher", "supersecret")).isSameAs(stored);
    }

    @Test
    void authenticateRejectsWrongPassword() {
        Account stored = new Account(UUID.randomUUID(), "watcher", null, encoder.encode("supersecret"));
        when(accounts.findByUsernameIgnoreCase("watcher")).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> service.authenticate("watcher", "wrong-password"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticateRejectsUnknownUser() {
        when(accounts.findByUsernameIgnoreCase("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.authenticate("ghost", "whatever123"))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
