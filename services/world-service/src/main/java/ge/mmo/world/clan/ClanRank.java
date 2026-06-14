package ge.mmo.world.clan;

/** Clan ranks, ordered by authority (LEADER is highest). */
public enum ClanRank {
    MEMBER(0),
    OFFICER(1),
    LEADER(2);

    private final int level;

    ClanRank(int level) {
        this.level = level;
    }

    public boolean outranks(ClanRank other) {
        return this.level > other.level;
    }

    public boolean atLeast(ClanRank other) {
        return this.level >= other.level;
    }
}
