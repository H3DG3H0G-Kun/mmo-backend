package ge.mmo.world.web.dto;

import ge.mmo.world.clan.ClanRank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Leader sets another member's rank (LEADER transfers leadership). */
public record SetClanRankRequest(
        @NotNull UUID characterId,
        @NotNull UUID targetCharacterId,
        @NotNull ClanRank rank) {
}
