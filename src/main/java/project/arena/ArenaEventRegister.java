package project.arena;

import project.entity.ArenaObject;
import project.entity.Tower;
import project.event.*;
import project.event.eventargs.*;

/**
 * List of events for the arena.
 * <p>
 * General rule: Use events for broadcasting, not one-to-one commands.
 */
public final class ArenaEventRegister {

    /**
     * Constructs a newly allocated {@link ArenaEventRegister} object.
     */
    ArenaEventRegister() {}

    /**
     * The arena is processing its next frame.
     */
    public final EventManager<EventArgs> ARENA_NEXT_FRAME = new EventManager<>();

    /**
     * The arena has finished processing its next frame.
     */
    public final EventManager<EventArgs> ARENA_NEXT_FRAME_END = new EventManager<>();

    /**
     * Gameover has occurred.
     */
    public final EventManager<EventArgs> ARENA_GAME_OVER = new EventManager<>();

    /**
     * An {@link ArenaObject} of any type is being added to the arena.
     * The object is assumed to be fully initialized.
     */
    public final EventManager<ArenaObjectEventArgs> ARENA_OBJECT_ADD = new EventManager<>();

    /**
     * An {@link ArenaObject} of any type is being removed from the arena.
     * The object is assumed to be ready for disposal.
     */
    public final EventManager<ArenaObjectEventArgs> ARENA_OBJECT_REMOVE = new EventManager<>();

    /**
     * An {@link ArenaObject} of any type is scheduled to be moved to another location within the arena.
     */
    public final EventManager<ArenaObjectEventArgs> ARENA_OBJECT_MOVE_START = new EventManager<>();

    /**
     * An {@link ArenaObject} of any type has been moved to another location within the arena.
     */
    public final EventManager<ArenaObjectEventArgs> ARENA_OBJECT_MOVE_END = new EventManager<>();

    /**
     * A {@link Tower} of any type is scheduled to be upgraded in the arena.
     */
    public final EventManager<ArenaTowerEventArgs> ARENA_TOWER_UPGRADE_START = new EventManager<>();

    /**
     * A {@link Tower} of any type has been upgraded in the arena.
     */
    public final EventManager<ArenaTowerEventArgs> ARENA_TOWER_UPGRADE_END = new EventManager<>();

}