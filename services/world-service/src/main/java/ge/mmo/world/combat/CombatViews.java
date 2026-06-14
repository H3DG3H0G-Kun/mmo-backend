package ge.mmo.world.combat;

import java.util.UUID;

public final class CombatViews {
    private CombatViews() {
    }

    public record EnemyView(String code, String name, int maxHp, int attackPower, long goldReward) {
        static EnemyView of(EnemyDef e) {
            return new EnemyView(e.getCode(), e.getName(), e.getMaxHp(), e.getAttackPower(), e.getGoldReward());
        }
    }

    /** The live state of a fight, plus what happened on the last resolved round. */
    public record EncounterView(
            UUID id,
            String enemyCode,
            String enemyName,
            int enemyHp,
            int characterHp,
            String status,
            long goldAwarded) {
    }
}
