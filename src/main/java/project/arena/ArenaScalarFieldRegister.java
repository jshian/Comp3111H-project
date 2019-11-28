package project.arena;

import project.entity.Monster;
import project.field.MonsterDistanceToEndField;
import project.field.MonsterAttacksToEndField;

/**
 * List of scalar fields for the arena.
 */
public final class ArenaScalarFieldRegister {

    /**
     * Constructs a newly allocated {@link ArenaScalarFieldRegister} object and attaches it to an arena instance.
     * @param arenaInstance The arena instance.
     */
    ArenaScalarFieldRegister(ArenaInstance arenaInstance) {
        MONSTER_DISTANCE_TO_END = new MonsterDistanceToEndField(arenaInstance);
        MONSTER_ATTACKS_TO_END = new MonsterAttacksToEndField(arenaInstance);
    }

    /**
     * The scalar field where the value on each point equals the minimum distance
     * travelled from that point to the end zone.
     */
    public final MonsterDistanceToEndField MONSTER_DISTANCE_TO_END;

    /**
     * The scalar field where the value on each point equals the minimum attacks
     * received from that point to the end zone per unit speed of a {@link Monster}.
     */
    public final MonsterAttacksToEndField MONSTER_ATTACKS_TO_END;
}