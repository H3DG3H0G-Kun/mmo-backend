package ge.mmo.world.economy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A character's gold balance. */
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @Column(name = "character_id")
    private UUID characterId;

    @Column(nullable = false)
    private long gold;

    protected Wallet() {
    }

    public Wallet(UUID characterId, long gold) {
        this.characterId = characterId;
        this.gold = gold;
    }

    public void credit(long amount) {
        this.gold += amount;
    }

    /** @return true if the debit succeeded (sufficient funds). */
    public boolean debit(long amount) {
        if (gold < amount) {
            return false;
        }
        gold -= amount;
        return true;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public long getGold() {
        return gold;
    }
}
