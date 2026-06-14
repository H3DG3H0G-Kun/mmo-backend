package ge.mmo.world.web.dto;

import ge.mmo.world.world.Era;

public record EraView(Integer id, String code, String name, int ordinal) {

    public static EraView of(Era era) {
        return new EraView(era.getId(), era.getCode(), era.getName(), era.getOrdinal());
    }
}
