package project.arena;

import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.UIController;
import project.arena.towers.Tower;

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
     * Performs bounds checking of the grid.
     * @param xPos The x position.
     * @param yPos The y position.
     * @throws IllegalArgumentException If the grid is outside the arena.
     */
    private static void checkGrid(int xPos, int yPos) throws IllegalArgumentException {
        if (xPos < 0 || xPos >= UIController.MAX_H_NUM_GRID) {
            throw new IllegalArgumentException("The parameter 'x' is out of bounds.");
        }
        
        if (yPos < 0 || yPos >= UIController.MAX_V_NUM_GRID) {
            throw new IllegalArgumentException("The parameter 'y' is out of bounds.");
        }
    }

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
        checkGrid(xPos, yPos);

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
        return Math.min(coordinates.getX() / UIController.GRID_WIDTH, UIController.MAX_H_NUM_GRID - 1);
    }

    /**
     * Finds the y-position of the grid which encloses the specified set of coordinates.
     * @param coordinates The specified set of coordinates.
     * @return The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     */
    public static int findGridYPos(@NonNull Coordinates coordinates) {
        return Math.min(coordinates.getY() / UIController.GRID_HEIGHT, UIController.MAX_V_NUM_GRID - 1);
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
        checkGrid(xPos, yPos);

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
        checkGrid(xPos, yPos);

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

    /**
     * Finds the grids within a taxicab distance of one from the grid from the grid containing the specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @return A linked list containing a reference to the {x, y} position of each taxicab neighbour.
     */
    public static LinkedList<int[]> findTaxicabNeighbours(@NonNull Coordinates coordinates) {
        return findTaxicabNeighbours(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the grids within a taxicab distance of one from the grid from the specified grid.
     * @param xPos The x-position of the grid.
     * @param yPos The y-position of the grid.
     * @return A linked list containing a reference to the {x, y} position of each taxicab neighbour.
     */
    public static LinkedList<int[]> findTaxicabNeighbours(int xPos, int yPos) {
        checkGrid(xPos, yPos);

        LinkedList<int[]> result = new LinkedList<>();

        // Left neighbour
        if (xPos > 0)
            result.add(new int[] { xPos - 1, yPos });
        
        // Right neighbour
        if (xPos < UIController.MAX_H_NUM_GRID - 1)
            result.add(new int[] { xPos + 1, yPos });
        
        // Top neighbour
        if (yPos > 0)
            result.add(new int[] { xPos, yPos - 1 });

        // Bottom neighbour
        if (yPos < UIController.MAX_V_NUM_GRID - 1)
            result.add(new int[] { xPos, yPos + 1 });

        return result;
    }
}
