package project;

/**
 * Interface for objects that appear in the arena.
 */
public interface AppearsInArena {
    /**
     * Accesses the image path of the object.
     * @return The file path to the image relative to the project root.
     */
    public String getImagePath();

    /**
     * Accesses the coordinates of the object.
     * @return The coordinates of the object.
     * @see Coordinates
     */
    public Coordinates getCoordinates();
}