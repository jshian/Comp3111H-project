package project.event.eventsets;

/**
 * Enum of events about the addition and removal of {@link ArenaObject}s.
 */
public enum ArenaObjectIOEvent implements EventSet {

    /**
     * An {@link ArenaObject} of any type is scheduled to be added to the arena.
     */
    ADD,

    /**
     * An {@link ArenaObject} of any type is scheduled to be removed from the arena.
     */
    REMOVE;

}