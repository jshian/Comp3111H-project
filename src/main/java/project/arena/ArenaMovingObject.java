package project.arena;

/**
 * Interface for objects that can move inside the {@link Arena}.
 */
public interface ArenaMovingObject extends ArenaObject {
    /**
     * Accesses the current speed of the object.
     * @return The current speed of the object.
     */
    public double getSpeed();
}