package project.event.eventargs;

import project.entity.Tower;

/**
 * Struct containing data of an {@link Tower}.
 */
public abstract class ArenaTowerEventArgs extends EventArgs {

    /**
     * The subject of the event.
     */
    public Tower subject;
}