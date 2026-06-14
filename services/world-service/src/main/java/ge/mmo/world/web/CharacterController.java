package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.web.dto.CharacterResponse;
import ge.mmo.world.web.dto.CreateCharacterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characters;

    public CharacterController(CharacterService characters) {
        this.characters = characters;
    }

    @PostMapping
    public ResponseEntity<CharacterResponse> create(@AuthenticationPrincipal AuthPrincipal principal,
                                                    @Valid @RequestBody CreateCharacterRequest req) {
        PlayerCharacter created = characters.create(principal.accountId(), req.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(CharacterResponse.of(created));
    }

    @GetMapping
    public List<CharacterResponse> mine(@AuthenticationPrincipal AuthPrincipal principal) {
        return characters.listForAccount(principal.accountId()).stream()
                .map(CharacterResponse::of)
                .toList();
    }
}
