package project.arena;

import java.util.LinkedList;
import java.util.PriorityQueue;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.UIController;
import project.arena.monsters.Monster;
import project.arena.projectiles.Projectile;
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
    private final short xPos;

    /**
     * The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     */
    private final short yPos;

    /**
     * Performs bounds checking of the grid.
     * @param xPos The x position.
     * @param yPos The y position.
     * @throws IllegalArgumentException If the grid is outside the arena.
     */
    private static void checkGrid(short xPos, short yPos) throws IllegalArgumentException {
        if (xPos < 0 || xPos >= UIController.MAX_H_NUM_GRID) {
            throw new IllegalArgumentException("The parameter 'x' is out of bounds.");
        }
        
        if (yPos < 0 || yPos >= UIController.MAX_V_NUM_GRID) {
            throw new IllegalArgumentException("The parameter 'y' is out of bounds.");
        }
    }

    /**
     * Contains a reference to each Tower on the arena.
     * @see Tower
     */
    private LinkedList<Tower> towers = new LinkedList<>();

    /**
     * Contains a reference to each Projectile on the arena.
     * @see Projectile
     */
    private LinkedList<Projectile> projectiles = new LinkedList<>();

    /**
     * Contains a reference to each Monster on the arena.
     * In addition, the monsters are sorted according to how close they are from reaching the end zone.
     * The first element is closest to the end zone while the last element is furthest.
     * @see Monster
     */
    private PriorityQueue<Monster> monsters = new PriorityQueue<>();

    /**
     * Constructor for the Grid class.
     * @param xPos The horizontal position of the grid, with 0 denoting the left-most grid increasing towards the right. This should be at least 0 and less than {@value UIController#MAX_H_NUM_GRID}.
     * @param yPos The vertical position of the grid, with 0 denoting the top-most grid, increasing towards the bottom. This should be at least 0 and less than {@value UIController#MAX_V_NUM_GRID}.
     */
    Grid(short xPos, short yPos) {
        checkGrid(xPos, yPos);

        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Accesses the x-position of the grid.
     * @return The x-position of the grid, where 0 is left-most, increasing towards the right.
     */
    short getXPos() { return xPos; }

    /**
     * Accesses the y-position of the grid.
     * @return The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     */
    short getYPos() { return yPos; }

    /**
     * Finds the coordinates of the center of the grid
     * @return The coordinates of the center of the grid.
     */
    Coordinates getCenter() {
        return findGridCenter(xPos, yPos);
    }
    
    /**
     * Accessor for the Towers contained in the object.
     * @return A linked list containing a reference to each Tower in the arena.
     */
    LinkedList<Tower> getTowers() { return towers; }

    /**
     * Accessor for the Projectiles contained in the object.
     * @return A linked list containing a reference to each Projectile in the arena.
     */
    LinkedList<Projectile> getProjectiles() { return projectiles; }

    /**
     * Accessor for the Monsters contained in the object.
     * @return A priority queue containing a reference to each Monster in the arena. The first element is closest to the end zone while the last element is furthest.
     */
    PriorityQueue<Monster> getMonsters() { return monsters; }

    /**
     * Adds an object to the grid.
     * @param obj The object to add.
     * @throws IllegalArgumentException If the object type is not recognized.
     */
    void addObject(@NonNull ExistsInArena obj) throws IllegalArgumentException {
        if (obj instanceof Tower) {
            if (!towers.contains(obj)) {
                towers.add((Tower)obj);
            }
        } else if (obj instanceof Projectile) {
            if (!projectiles.contains(obj)) {
                projectiles.add((Projectile)obj);
            }
        } else if (obj instanceof Monster) {
            if (!monsters.contains(obj)) {
                monsters.add((Monster)obj);
            }
        } else {
            throw new IllegalArgumentException("The object type is not recognized");
        }
    }

    /**
     * Removes an object from the grid.
     * @param obj The object to be removed.
     * @throws IllegalArgumentException If the object type is not recognized.
     */
    void removeObject(@NonNull ExistsInArena obj) throws IllegalArgumentException {
        if (obj instanceof Tower) {
            towers.remove((Tower)obj);
        } else if (obj instanceof Projectile) {
            projectiles.remove((Projectile)obj);
        } else if (obj instanceof Monster) {
            monsters.remove((Monster)obj);
        } else {
            throw new IllegalArgumentException("The object type is not recognized");
        }
    }

    /**
     * Finds the x-position of the grid which encloses the specified set of coordinates.
     * @param coordinates The specified set of coordinates.
     * @return The x-position of the grid, where 0 is left-most, increasing towards the right.
     */
    public static short findGridXPos(@NonNull Coordinates coordinates) {
        return (short) Math.min(coordinates.getX() / UIController.GRID_WIDTH, UIController.MAX_H_NUM_GRID - 1);
    }

    /**
     * Finds the y-position of the grid which encloses the specified set of coordinates.
     * @param coordinates The specified set of coordinates.
     * @return The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     */
    public static short findGridYPos(@NonNull Coordinates coordinates) {
        return (short) Math.min(coordinates.getY() / UIController.GRID_HEIGHT, UIController.MAX_V_NUM_GRID - 1);
    }

    /**
     * Finds the x-coordinate of the center of the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The x-coordinate of the center of the grid which encloses the specified set of coordinates.
     */
    static short findGridCenterX(@NonNull Coordinates coordinates) {
        return findGridCenterX(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the x-coordinate of the center of the specified grid.
     * @param xPos The x-position of the grid, where 0 is left-most, increasing towards the right.
     * @param yPos The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     * @return The x-coordinate of the center of the specified grid.
     */
    static short findGridCenterX(short xPos, short yPos) {
        checkGrid(xPos, yPos);

        return (short) ((xPos + 0.5) * UIController.GRID_WIDTH);
    }

    /**
     * Finds the y-coordinate of the center of the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The y-coordinate of the center of the grid which encloses the specified set of coordinates.
     */
    static short findGridCenterY(@NonNull Coordinates coordinates) {
        return findGridCenterY(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the y-coordinate of the center of the specified grid.
     * @param xPos The x-position of the grid, where 0 is left-most, increasing towards the right.
     * @param yPos The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     * @return The y-coordinate of the center of the specified grid.
     */
    static short findGridCenterY(short xPos, short yPos) {
        checkGrid(xPos, yPos);

        return (short) ((yPos + 0.5) * UIController.GRID_HEIGHT);
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
    public static Coordinates findGridCenter(short xPos, short yPos) {
        return new Coordinates(findGridCenterX(xPos, yPos), findGridCenterY(xPos, yPos));
    }

    /**
     * Finds the x-coordinate of the left edge of the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The x-coordinate of the left edge of the grid which encloses the specified set of coordinates.
     */
    static short findGridLeftX(@NonNull Coordinates coordinates) {
        return findGridLeftX(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the x-coordinate of the left edge of the specified grid.
     * @param xPos The x-position of the grid, where 0 is left-most, increasing towards the right.
     * @param yPos The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     * @return The x-coordinate of the left edge of the specified grid.
     */
    static short findGridLeftX(short xPos, short yPos) {
        checkGrid(xPos, yPos);

        return (short) (xPos * UIController.GRID_WIDTH);
    }

    /**
     * Finds the y-coordinate of the top edge of the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The y-coordinate of the top edge of the grid which encloses the specified set of coordinates.
     */
    static short findGridTopY(@NonNull Coordinates coordinates) {
        return findGridTopY(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the y-coordinate of the top edge of the specified grid.
     * @param xPos The x-position of the grid, where 0 is left-most, increasing towards the right.
     * @param yPos The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     * @return The y-coordinate of the top edge of the specified grid.
     */
    static short findGridTopY(short xPos, short yPos) {
        checkGrid(xPos, yPos);

        return (short) (yPos * UIController.GRID_HEIGHT);
    }

    /**
     * Finds the coordinates of the top-left corner of the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The coordinates of the top-left corner of the grid which encloses the specified set of coordinates.
     */
    public static Coordinates findGridTopLeft(@NonNull Coordinates coordinates) {
        return findGridTopLeft(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the coordinates of the top-left corner of the specified grid.
     * @param xPos The x-position of the grid, where 0 is left-most, increasing towards the right.
     * @param yPos The y-position of the grid, where 0 is top-most, increasing towards the bottom.
     * @return The coordinates of the top-left corner of the specified grid.
     */
    public static Coordinates findGridTopLeft(short xPos, short yPos) {
        return new Coordinates(findGridLeftX(xPos, yPos), findGridTopY(xPos, yPos));
    }

    /**
     * Finds the grids within a taxicab distance of one from the grid from the grid containing the specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @return A linked list containing a reference to the {x, y} position of each taxicab neighbour.
     */
    public static LinkedList<short[]> findTaxicabNeighbours(@NonNull Coordinates coordinates) {
        return findTaxicabNeighbours(findGridXPos(coordinates), findGridYPos(coordinates));
    }

    /**
     * Finds the grids within a taxicab distance of one from the grid from the specified grid.
     * @param xPos The x-position of the grid.
     * @param yPos The y-position of the grid.
     * @return A linked list containing a reference to the {x, y} position of each taxicab neighbour.
     */
    public static LinkedList<short[]> findTaxicabNeighbours(short xPos, short yPos) {
        checkGrid(xPos, yPos);

        LinkedList<short[]> result = new LinkedList<>();

        // Left neighbour
        if (xPos > 0)
            result.add(new short[] { (short) (xPos - 1), yPos });
        
        // Right neighbour
        if (xPos < UIController.MAX_H_NUM_GRID - 1)
            result.add(new short[] { (short) (xPos + 1), yPos });
        
        // Top neighbour
        if (yPos > 0)
            result.add(new short[] { xPos, (short) (yPos - 1) });

        // Bottom neighbour
        if (yPos < UIController.MAX_V_NUM_GRID - 1)
            result.add(new short[] { xPos, (short) (yPos + 1) });

        return result;
    }
}
