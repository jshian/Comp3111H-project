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

    public LinkedList<Object> getObjectsAtPixel(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }

    public LinkedList<Object> getObjectsInGrid(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Updates the arena by one time step.
     */
    public void update() {

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