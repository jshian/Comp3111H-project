package project.event.eventsets;

/**
 * Enum of events about changes to {@link Tower}s.
 */
public enum ArenaTowerChangeEvent implements EventSet {

    /**
     * A {@link Tower} of any type is scheduled to be upgraded in the arena.
     */
    UPGRADE;
}