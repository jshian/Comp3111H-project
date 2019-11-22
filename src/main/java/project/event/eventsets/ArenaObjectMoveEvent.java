package project.event.eventsets;

/**
 * Enum of events about the movement of {@link ArenaObject}s.
 */
public enum ArenaObjectMoveEvent implements EventSet {

    /**
     * An {@link ArenaObject} of any type is scheduled to be moved to another location within the arena.
     */
    MOVE;
}