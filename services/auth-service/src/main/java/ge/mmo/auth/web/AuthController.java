package ge.mmo.auth.web;

import ge.mmo.auth.account.Account;
import ge.mmo.auth.account.AccountService;
import ge.mmo.auth.web.dto.AccountResponse;
import ge.mmo.auth.web.dto.LoginRequest;
import ge.mmo.auth.web.dto.RegisterRequest;
import ge.mmo.auth.web.dto.TokenResponse;
import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.common.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accounts;
    private final JwtService jwt;

    public AuthController(AccountService accounts, JwtService jwt) {
        this.accounts = accounts;
        this.jwt = jwt;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest req) {
        Account account = accounts.register(req.username(), req.email(), req.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenFor(account));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        Account account = accounts.authenticate(req.username(), req.password());
        return tokenFor(account);
    }

    @GetMapping("/me")
    public AccountResponse me(@AuthenticationPrincipal AuthPrincipal principal) {
        return AccountResponse.of(accounts.require(principal.accountId()));
    }

    private TokenResponse tokenFor(Account account) {
        String token = jwt.issueAccessToken(account.getId(), account.getUsername(), account.roleList());
        return TokenResponse.bearer(token, jwt.accessTtl().toSeconds(), AccountResponse.of(account));
    }
}
