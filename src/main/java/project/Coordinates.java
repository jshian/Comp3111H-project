package project;

import java.util.*;

import org.apache.commons.lang3.*;

/**
 * Custom class to store 2D Cartesian coordinates of objects in the arena. Also
 * stores a link to the arena itself.
 * @see Arena
 */
public class Coordinates {

    private Arena arena;
    
    private int x = 0;
    private int y = 0;

    /**
     * Constructor for the Coordinate class. Both coordinates default to 0.
     * @param arena The arena that the Coordinate is linked to.
     */
    public Coordinates(Arena arena) { this.arena = arena; }

    /**
     * Parametized onstructor for the Coordinate class.
     * @param arena The arena that the Coordinate is linked to.
     * @param x The horizontal coordinate, increasing towards the right.
     * @param y The vertical coordinate, increasing towards the bottom.
     */
    public Coordinates(Arena arena, int x, int y) { this.arena = arena; update(x, y); }

    /**
     * Finds all objects that occupy the specified coordinate in the arena.
     * @param x The x-coordinate, as defined in {@link Coordinates#Coordinates()}.
     * @param y The y-coordinate, as defined in {@link Coordinates#Coordinates()}.
     * @return A linked list containing all objects that satisfy the above criteria. Note that they do not have to be located at said coordinate.
     */
    public LinkedList<Object> findObjectsOccupying(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Accesses the x-coordinate.
     * @return The horizontal coordinate, as defined in {@link Coordinates#Coordinates()}.
     */
    public int getX() { return x; }

    /**
     * Accesses the y-coordinate.
     * @return The vertical coordinate, as defined in {@link Coordinates#Coordinates()}.
     */
    public int getY() { return y; }

    /**
     * Updates both coordinates.
     * @param x The x-coordinate, as defined in {@link Coordinates#Coordinates()}.
     * @param y The y-coordinate, as defined in {@link Coordinates#Coordinates()}.
     */
    public void update(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Calculates the distance between the coordinates represented by the two Coordinate objects.
     * @param other The Coordinates of the other object.
     * @return The distance between the coordinates represented by the two Coordinate objects.
     */
    public double distanceFrom(Coordinates other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * Calculates the angle between the coordinates represented by the two Coordinate objects.
     * @param other The Coordinates of the other object.
     * @return The angle in radians from this object to the other object, as if this object is at the origin of a polar coordinate system.
     */
    public double angleFrom(Coordinates other) {
        return Math.atan2(other.y - this.y, other.x - this.x);
    }
}