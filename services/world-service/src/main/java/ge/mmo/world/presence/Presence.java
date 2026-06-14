package ge.mmo.world.presence;

import java.util.UUID;

/** A character's live presence in the world: who they are, where they are, and in which era. */
public class Presence {

    private final UUID characterId;
    private final String name;
    private final String eraCode;
    private volatile double x;
    private volatile double y;
    private volatile double z;

    public Presence(UUID characterId, String name, String eraCode) {
        this.characterId = characterId;
        this.name = name;
        this.eraCode = eraCode;
    }

    public void moveTo(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public UUID characterId() {
        return characterId;
    }

    public String name() {
        return name;
    }

    public String eraCode() {
        return eraCode;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }
}
