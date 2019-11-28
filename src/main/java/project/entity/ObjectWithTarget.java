package project.entity;

/**
 * Interface for objects with a target location.
 */
interface ObjectWithTarget {
    
    /**
     * Returns the x-coordinate of the target location.
     * @return The x-coordinate of the target location.
     */
    abstract short getTargetLocationX();

    /**
     * Returns the y-coordinate of the target location.
     * @return The y-coordinate of the target location.
     */
    abstract short getTargetLocationY();

    /**
     * Returns the current movement distance of the object from the target location.
     * @return The current movement distance of the object from the target location.
     */
    abstract double getMovementDistanceToDestination();
}