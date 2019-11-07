package project;

import java.util.Collections;
import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.NonNull;
import project.towers.*;
import project.Arena.ExistsInArena;

/**
 * Grids are arranged in a tabular manner in the Arena and limit the positioning of Towers.
 * The class is comprised mainly of helper functions to manage the conversion between {@link Coordinates} and the corresponding grid.
 * @see Arena
 * @see Tower
 */
public class Grid {
    /**
     * The x-position of the grid, where 0 is left-most, increasing towards the right.
     */
    private final int xPos;

    /**
     * The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     */
    private final int yPos;

    /**
     * A linked list containing a reference to each object within the grid.
     */
    private LinkedList<ExistsInArena> objects = new LinkedList<>();

    /**
     * Constructor for the Grid class.
     * @param xPos The horizontal position of the grid, with 0 denoting the left-most grid increasing towards the right. This should be at least 0 and less than {@value UIController#MAX_H_NUM_GRID}.
     * @param yPos The vertical position of the grid, with 0 denoting the top-most grid, increasing towards the bottom. This should be at least 0 and less than {@value UIController#MAX_V_NUM_GRID}.
     */
    Grid(int xPos, int yPos) {
        if (xPos < 0 || xPos >= UIController.MAX_H_NUM_GRID)
            throw new IllegalArgumentException("The parameter 'x' is out of bounds.");
        if (yPos < 0 || yPos >= UIController.MAX_V_NUM_GRID)
            throw new IllegalArgumentException("The parameter 'y' is out of bounds.");

        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Accesses the x-position of the grid.
     * @return The x-position of the grid, where 0 is left-most, increasing towards the right.
     */
    int getXPos() { return xPos; }

    /**
     * Accesses the y-position of the grid.
     * @return The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     */
    int getYPos() { return yPos; }

    /**
     * Finds the coordinates of the center of the grid
     * @return The coordinates of the center of the grid.
     */
    Coordinates getCenter() {
        return findGridCenter(xPos, yPos);
    }
    
    /**
     * Accesses all objects contained within the grid.
     * @return A linked list containing a reference to each object in the grid.
     */
    LinkedList<ExistsInArena> getAllObjects() { return objects; }

    /**
     * Adds an object to the grid.
     */
    void addObject(@NonNull ExistsInArena obj) {
        if (!objects.contains(obj)) objects.add(obj);
    }

    /**
     * Removes an object from the grid.
     * @param obj The object to be removed.
     */
    void removeObject(@NonNull ExistsInArena obj) { objects.removeFirstOccurrence(obj); }

    /**
     * Finds the x-position of the grid which encloses the specified set of coordinates.
     * @param coordinates The specified set of coordinates.
     * @return The x-position of the grid, where 0 is left-most, increasing towards the right.
     */
    public static int findGridXPos(@NonNull Coordinates coordinates) {
        return coordinates.getX() / UIController.GRID_WIDTH;
    }

    /**
     * Finds the y-position of the grid which encloses the specified set of coordinates.
     * @param coordinates The specified set of coordinates.
     * @return The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     */
    public static int findGridYPos(@NonNull Coordinates coordinates) {
        return coordinates.getY() / UIController.GRID_HEIGHT;
    }

    /**
     * Finds the x-coordinate of the center of the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The x-coordinate of the center of the grid which encloses the specified set of coordinates.
     */
    static int findGridCenterX(@NonNull Coordinates coordinates) {
        return findGridCenterX(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the x-coordinate of the center of the specified grid.
     * @param xPos The x-position of the grid, where 0 is left-most, increasing towards the right.
     * @param yPos The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     * @return The x-coordinate of the center of the specified grid.
     */
    static int findGridCenterX(int xPos, int yPos) {
        return (int) ((xPos + 0.5) * UIController.GRID_WIDTH);
    }

    /**
     * Finds the y-coordinate of the center of the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The y-coordinate of the center of the grid which encloses the specified set of coordinates.
     */
    static int findGridCenterY(@NonNull Coordinates coordinates) {
        return findGridCenterY(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the y-coordinate of the center of the specified grid.
     * @param xPos The x-position of the grid, where 0 is left-most, increasing towards the right.
     * @param yPos The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     * @return The y-coordinate of the center of the specified grid.
     */
    static int findGridCenterY(int xPos, int yPos) {
        return (int) ((yPos + 0.5) * UIController.GRID_HEIGHT);
    }

    /**
     * Finds the coordinates of the center of the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The coordinates of the center of the grid which encloses the specified set of coordinates.
     */
    public static Coordinates findGridCenter(@NonNull Coordinates coordinates) {
        return findGridCenter(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the coordinates of the center of the specified grid.
     * @param xPos The x-position of the grid, where 0 is left-most, increasing towards the right.
     * @param yPos The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     * @return The coordinates of the center of the specified grid.
     */
    public static Coordinates findGridCenter(int xPos, int yPos) {
        return new Coordinates(findGridCenterX(xPos, yPos), findGridCenterY(xPos, yPos));
    }
}
