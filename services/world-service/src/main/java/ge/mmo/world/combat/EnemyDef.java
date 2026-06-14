package ge.mmo.world.combat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A Distortion definition (Echo/Aspect boss). Content as data. */
@Entity
@Table(name = "enemy_def")
public class EnemyDef {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "max_hp", nullable = false)
    private int maxHp;

    @Column(name = "attack_power", nullable = false)
    private int attackPower;

    @Column(name = "gold_reward", nullable = false)
    private long goldReward;

    protected EnemyDef() {
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public long getGoldReward() {
        return goldReward;
    }
}
