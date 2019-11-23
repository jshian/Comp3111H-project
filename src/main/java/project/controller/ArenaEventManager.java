package project.controller;

import project.event.*;
import project.event.eventargs.*;
import project.event.eventsets.*;

/**
 * Singleton that manages a set of event managers for the arena.
 */
public final class ArenaEventManager {

    /**
     * Constructs a newly allocated {@link ArenaEventManager} object.
     */
    ArenaEventManager() {}

    /**
     * Event manager for arena-wide events.
     */
    public final EventManager<ArenaEvent, EventArgs> ARENA = new EventManager<>();

    /**
     * Event manager for events about {@ArenaObject} I/O.
     */
    public final EventManager<ArenaObjectIOEvent, ArenaObjectEventArgs> OBJECT_IO = new EventManager<>();

    /**
     * Event manager for events about {@ArenaObject} movement.
     */
    public final EventManager<ArenaObjectMoveEvent, ArenaObjectMoveEventArgs> OBJECT_MOVE = new EventManager<>();

    /**
     * Event manager for events about {@Tower}.
     */
    public final EventManager<ArenaTowerChangeEvent, ArenaTowerUpgradeArgs> TOWER_CHANGE = new EventManager<>();
}