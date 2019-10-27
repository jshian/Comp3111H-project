package project;

import java.util.*;

import org.apache.commons.lang3.*;

public class Arena {
    /**
     * A linked list containing all of the monsters in the arena.
     */
    private LinkedList<Monster> monsters = new LinkedList<>();

    /**
     * The default constructor of the Arena class.
     */
    public Arena() {

    }

    /**
     * Finds all objects that are located at a pixel.
     * @param x The horizontal coordinate of the pixel, increasing towards the right.
     * @param y The vertical coordinate of the pixel, increasing towards the bottom.
     * @return A linked list containing all objects that satisfy the above criteria.
     */
    public LinkedList<Object> getObjectsAtPixel(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Finds all objects that are located inside the grid where a pixel is located.
     * @param x The horizontal coordinate of the pixel, increasing towards the right.
     * @param y The vertical coordinate of the pixel, increasing towards the bottom.
     * @return A linked list containing all objects that satisfy the above criteria.
     */
    public LinkedList<Object> getObjectsInGrid(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Updates the arena by one frame.
     */
    public void nextFrame() {
        throw new NotImplementedException("TODO");
    }
}

/**
 * Interface for objects that exist in the arena.
 */
interface ExistsInArena {
    /**
     * Accesses the image path of the object.
     * @return The file path to the image relative to the project root.
     */
    String getImagePath();

    /**
     * Accesses the coordinates of the object.
     * @return The coordinates of the object.
     * @see Coordinates
     */
    Coordinates getCoordinates();
}

/**
 * Interface for objects that can move in the arena.
 */
interface MovesInArena extends ExistsInArena {
    /**
     * Moves the object by one frame.
     */
    void MoveOneFrame();
}