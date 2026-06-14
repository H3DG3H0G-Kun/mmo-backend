package ge.mmo.auth.web;

import ge.mmo.auth.account.Account;
import ge.mmo.auth.account.AccountService;
import ge.mmo.auth.account.UsernameTakenException;
import ge.mmo.common.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AccountService accounts;

    @MockitoBean
    JwtService jwt;

    private Account sampleAccount() {
        return new Account(UUID.randomUUID(), "watcher", "w@example.com", "hash");
    }

    @Test
    void registerReturns201WithToken() throws Exception {
        when(accounts.register(eq("watcher"), any(), eq("supersecret"))).thenReturn(sampleAccount());
        when(jwt.issueAccessToken(any(), any(), any())).thenReturn("signed.jwt.token");
        when(jwt.accessTtl()).thenReturn(Duration.ofHours(1));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"watcher","email":"w@example.com","password":"supersecret"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("signed.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.account.username").value("watcher"));
    }

    @Test
    void registerRejectsShortPassword() throws Exception {
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"watcher","password":"short"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void registerDuplicateReturns409() throws Exception {
        when(accounts.register(any(), any(), any())).thenThrow(new UsernameTakenException("watcher"));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"watcher","password":"supersecret"}"""))
                .andExpect(status().isConflict());
    }

    @Test
    void loginReturnsToken() throws Exception {
        when(accounts.authenticate(eq("watcher"), eq("supersecret"))).thenReturn(sampleAccount());
        when(jwt.issueAccessToken(any(), any(), any())).thenReturn("signed.jwt.token");
        when(jwt.accessTtl()).thenReturn(Duration.ofHours(1));

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"watcher","password":"supersecret"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("signed.jwt.token"));
    }
}
