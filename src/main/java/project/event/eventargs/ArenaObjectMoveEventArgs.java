package project.event.eventargs;

import project.entity.ArenaObjectPositionInfo;

/**
 * Struct containing data about the movement of an {@link ArenaObject}.
 */
public abstract class ArenaObjectMoveEventArgs extends ArenaObjectEventArgs {
    
    /**
     * The original position of the object.
     */
    public ArenaObjectPositionInfo originalPosition;

    /**
     * The new position of the object.
     */
    public ArenaObjectPositionInfo newPosition;
}