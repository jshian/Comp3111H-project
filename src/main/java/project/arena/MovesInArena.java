package project.arena;

/**
 * Interface for objects that can move inside the Arena.
 */
public interface MovesInArena extends ExistsInArena {
    /**
     * Accesses the current speed of the object.
     * @return The current speed of the object.
     */
    public double getSpeed();
}