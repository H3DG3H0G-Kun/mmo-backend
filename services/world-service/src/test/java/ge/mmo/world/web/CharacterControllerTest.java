package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.world.Era;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Standalone MVC test: a fixed {@link AuthPrincipal} is injected via a custom argument resolver,
 * so the controller's delegation/validation is verified without depending on the security filter
 * chain (enforcement/401 is covered by the live boot verification).
 */
class CharacterControllerTest {

    private final CharacterService characters = mock(CharacterService.class);
    private final UUID accountId = UUID.randomUUID();
    private final AuthPrincipal principal = new AuthPrincipal(accountId, "watcher", List.of("PLAYER"));

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CharacterController(characters))
                .setControllerAdvice(new ApiExceptionHandler())
                .setCustomArgumentResolvers(new FixedPrincipalResolver(principal))
                .build();
    }

    private PlayerCharacter sampleCharacter() {
        Era era = mock(Era.class);
        when(era.getId()).thenReturn(1);
        when(era.getCode()).thenReturn("PARNAVAZ");
        when(era.getName()).thenReturn("The Age of Parnavaz");
        when(era.getOrdinal()).thenReturn(1);
        PlayerCharacter c = mock(PlayerCharacter.class);
        when(c.getId()).thenReturn(UUID.randomUUID());
        when(c.getName()).thenReturn("Mtsveli");
        when(c.getCurrentEra()).thenReturn(era);
        when(c.getCreatedAt()).thenReturn(Instant.parse("2026-01-01T00:00:00Z"));
        return c;
    }

    @Test
    void createReturns201WithCharacter() throws Exception {
        PlayerCharacter sample = sampleCharacter();
        when(characters.create(eq(accountId), eq("Mtsveli"))).thenReturn(sample);

        mvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Mtsveli"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mtsveli"))
                .andExpect(jsonPath("$.currentEra.code").value("PARNAVAZ"));
    }

    @Test
    void createRejectsBadName() throws Exception {
        mvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"x"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void listReturnsMine() throws Exception {
        PlayerCharacter sample = sampleCharacter();
        when(characters.listForAccount(accountId)).thenReturn(List.of(sample));

        mvc.perform(get("/api/characters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mtsveli"));
    }

    /** Injects a fixed principal wherever the controller asks for an {@link AuthPrincipal}. */
    private record FixedPrincipalResolver(AuthPrincipal principal) implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return AuthPrincipal.class.equals(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
            return principal;
        }
    }
}
