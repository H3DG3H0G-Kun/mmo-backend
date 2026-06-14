package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.Interaction;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Walks a Tale's beat graph: given the current beat and its outgoing edges, picks the next.
 * WITNESS/AID/WARD/RESTORE follow the single outgoing edge; CHOOSE requires a matching
 * choiceKey. Shared by solo ({@link NarrativeService}) and co-op ({@link InstanceService}) runs.
 */
@Component
public class BeatNavigator {

    public BeatEdge selectEdge(Beat current, List<BeatEdge> outgoing, String choiceKey) {
        if (outgoing.isEmpty()) {
            throw new InvalidChoiceException("Beat " + current.getCode() + " has no outgoing edge");
        }
        if (current.getInteraction() == Interaction.CHOOSE) {
            if (choiceKey == null || choiceKey.isBlank()) {
                throw new InvalidChoiceException("This beat requires a choiceKey");
            }
            return outgoing.stream()
                    .filter(e -> choiceKey.equals(e.getChoiceKey()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidChoiceException("No branch for choiceKey: " + choiceKey));
        }
        return outgoing.getFirst();
    }
}
