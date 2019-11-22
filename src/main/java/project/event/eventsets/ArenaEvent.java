package project.event.eventsets;

/**
 * Enum of events about the arena.
 */
public enum ArenaEvent implements EventSet {

    /**
     * The arena is processing its next frame.
     */
    NEXT_FRAME,

    /**
     * The arena has finished processing its next frame.
     */
    NEXT_FRAME_END;
}