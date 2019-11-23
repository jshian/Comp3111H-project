package project.controller;

import project.field.MonsterDistanceToEndField;
import project.field.MonsterAttacksToEndField;

public final class ArenaScalarFieldManager {

    /**
     * Constructs a newly allocated {@link ArenaScalarFieldManager} object.
     */
    ArenaScalarFieldManager() {}

    /**
     * The scalar field where the value on each point equals the minimum distance
     * travelled from that point to the end zone.
     */
    public final MonsterDistanceToEndField MONSTER_DISTANCE_TO_END = new MonsterDistanceToEndField();

    /**
     * The scalar field where the value on each point equals the minimum attacks
     * received from that point to the end zone per unit speed of a {@link Monster}.
     */
    public final MonsterAttacksToEndField MONSTER_ATTACKS_TO_END = new MonsterAttacksToEndField();
}