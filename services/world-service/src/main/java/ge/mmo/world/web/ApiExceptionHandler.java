package ge.mmo.world.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        problem.setProperty("errors", errors);
        return problem;
    }

    /**
     * Domain conflicts (name taken, too many characters) map to 409; missing entities to 404.
     * These exceptions are package-private in the character module, so match by simple name.
     */
    @ExceptionHandler(RuntimeException.class)
    ProblemDetail handleDomain(RuntimeException ex) {
        String type = ex.getClass().getSimpleName();
        HttpStatus status = switch (type) {
            case "CharacterNameTakenException", "TooManyCharactersException",
                 "ResonanceClosedException", "NoActiveProgressException",
                 "EraLockedException", "AlreadyInPartyException",
                 "PartyFullException", "PartyClosedException",
                 "PartyRequiredException", "InstanceAlreadyActiveException",
                 "AlreadyInClanException", "ClanNameTakenException", "ClanTagTakenException",
                 "ClanFullException", "NoClanInviteException",
                 "ClanRequiredException", "SiegeAlreadyOpenException", "SiegeStateException",
                 "InsufficientItemsException", "InsufficientGoldException",
                 "ListingNotActiveException", "NoGatherableItemsException",
                 "WorldEchoActiveException", "WorldEchoNotActiveException",
                 "AlreadyInEncounterException", "EncounterNotActiveException" -> HttpStatus.CONFLICT;
            case "CharacterNotFoundException", "TaleNotFoundException",
                 "EraNotFoundException", "PartyNotFoundException",
                 "NotInPartyException", "NoActiveInstanceException",
                 "NotInClanException", "ClanNotFoundException",
                 "PlaceNotFoundException", "SiegeNotFoundException",
                 "ItemNotFoundException", "ListingNotFoundException",
                 "WorldEchoNotFoundException",
                 "EnemyNotFoundException", "EncounterNotFoundException" -> HttpStatus.NOT_FOUND;
            case "NotPartyLeaderException", "NotInstanceLeaderException",
                 "InsufficientClanRankException", "NotClanLeaderException",
                 "NotASiegeParticipantException", "ListingOwnershipException" -> HttpStatus.FORBIDDEN;
            case "InvalidChoiceException" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "NoStartingEraException" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> null;
        };
        if (status == null) {
            throw ex; // not ours — let Spring's default handling apply
        }
        return ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    }
}
