package ge.mmo.world.combat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** A live fight between a character and a Distortion. Backend owns all HP and resolution. */
@Entity
@Table(name = "encounter")
public class Encounter {

    public enum Status { ACTIVE, WON, LOST }

    @Id
    private UUID id;

    @Column(name = "enemy_def_id", nullable = false)
    private UUID enemyDefId;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "enemy_hp", nullable = false)
    private int enemyHp;

    @Column(name = "character_hp", nullable = false)
    private int characterHp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    /** If set, victory in this encounter contributes to this siege (combat -> siege loop). */
    @Column(name = "siege_id")
    private UUID siegeId;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "ended_at")
    private Instant endedAt;

    protected Encounter() {
    }

    public Encounter(UUID id, UUID enemyDefId, UUID characterId, int enemyHp, int characterHp) {
        this.id = id;
        this.enemyDefId = enemyDefId;
        this.characterId = characterId;
        this.enemyHp = enemyHp;
        this.characterHp = characterHp;
    }

    public void damageEnemy(int amount) {
        this.enemyHp = Math.max(0, this.enemyHp - amount);
    }

    public void damageCharacter(int amount) {
        this.characterHp = Math.max(0, this.characterHp - amount);
    }

    public void win() {
        this.status = Status.WON;
        this.endedAt = Instant.now();
    }

    public void lose() {
        this.status = Status.LOST;
        this.endedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getEnemyDefId() {
        return enemyDefId;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public int getEnemyHp() {
        return enemyHp;
    }

    public int getCharacterHp() {
        return characterHp;
    }

    public Status getStatus() {
        return status;
    }

    public UUID getSiegeId() {
        return siegeId;
    }

    public void setSiegeId(UUID siegeId) {
        this.siegeId = siegeId;
    }
}
