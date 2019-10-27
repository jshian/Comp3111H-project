package project;

import java.util.*;

import org.apache.commons.lang3.*;

/**
 * The area where most of the action takes place in the game.
 * Monsters spawn at the starting position and try to reach the end-zone.
 * Towers can be built on the arena.
 */
public class Arena {

    private static final int STARTING_POSITION_X = 0;
    private static final int STARTING_POSITION_Y = 0;
    private static final int END_ZONE_X = 440;
    private static final int END_ZONE_Y = 0;

    // Objects in the arena
    // private LinkedList<Tower> towers = new LinkedList<>();
    private LinkedList<Projectile> projectiles = new LinkedList<>();
    private LinkedList<Monster> monsters = new LinkedList<>();
    

    /**
     * The default constructor of the Arena class.
     */
    public Arena() {

    }

    /**
     * Finds all objects that are located at a specified pixel.
     * @param x The x-coordinate of the pixel, as defined in {@link Coordinates#Coordinates()}.
     * @param y The y-coordinate of the pixel, as defined in {@link Coordinates#Coordinates()}.
     * @return A linked list containing all objects that satisfy the above criteria.
     * @see Coordinates
     */
    public LinkedList<Object> getObjectsAtPixel(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Finds all objects that are located inside the grid where a specified pixel is located.
     * @param x The x-coordinate of the pixel, as defined in {@link Coordinates#Coordinates()}.
     * @param y The y-coordinate of the pixel, as defined in {@link Coordinates#Coordinates()}.
     * @return A linked list containing all objects that satisfy the above criteria.
     * @see Coordinates
     */
    public LinkedList<Object> getObjectsInGrid(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * @param x The x-coordinate of the pixel, as defined in {@link Coordinates#Coordinates()}.
     * @param y The y-coordinate of the pixel, as defined in {@link Coordinates#Coordinates()}.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     * @see Coordinates
     */
    public boolean canBuildTower(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }
    /**
     * Builds a tower at the grid where a specified pixel is located.
     * @param x The x-coordinate of the pixel, as defined in {@link Coordinates#Coordinates()}.
     * @param y The y-coordinate of the pixel, as defined in {@link Coordinates#Coordinates()}.
     * @see Coordinates
     */
    public void buildTower(int x, int y)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Spawns a monster at the starting position of the arena.
     */
    public void spawnMonster()
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Kills a monster.
     */
    public void killMonster()
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