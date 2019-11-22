package project.event.eventargs;

import project.entity.ArenaObject;

/**
 * Struct containing data of an {@link ArenaObject}.
 */
public abstract class ArenaObjectEventArgs extends EventArgs {

    /**
     * The subject of the event.
     */
    public ArenaObject subject;
}