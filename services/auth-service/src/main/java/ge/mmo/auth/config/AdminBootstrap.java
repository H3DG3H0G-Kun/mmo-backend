package ge.mmo.auth.config;

import ge.mmo.auth.account.AccountService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Ensures the bootstrap admin account exists on startup (idempotent). */
@Component
public class AdminBootstrap implements ApplicationRunner {

    private final AccountService accounts;
    private final AdminProperties props;

    public AdminBootstrap(AccountService accounts, AdminProperties props) {
        this.accounts = accounts;
        this.props = props;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (props.username() != null && props.password() != null) {
            accounts.ensureAdmin(props.username(), props.password());
        }
    }
}
