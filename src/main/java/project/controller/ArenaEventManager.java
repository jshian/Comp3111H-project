package project.controller;

import project.event.*;
import project.event.eventargs.*;
import project.event.eventsets.*;

/**
 * Singleton that manages a set of event managers for the arena.
 */
public final class ArenaEventManager {

    private ArenaEventManager() {}

    /**
     * Event manager for arena-wide events.
     */
    public static final EventManager<ArenaEvent, EventArgs> ARENA = new EventManager<>();

    /**
     * Event manager for events about {@ArenaObject} I/O.
     */
    public static final EventManager<ArenaObjectIOEvent, ArenaObjectEventArgs> OBJECT_IO = new EventManager<>();

    /**
     * Event manager for events about {@ArenaObject} movement.
     */
    public static final EventManager<ArenaObjectMoveEvent, ArenaObjectMoveEventArgs> OBJECT_MOVE = new EventManager<>();

    /**
     * Event manager for events about {@Tower}.
     */
    public static final EventManager<ArenaTowerChangeEvent, ArenaTowerEventArgs> TOWER_CHANGE = new EventManager<>();
}