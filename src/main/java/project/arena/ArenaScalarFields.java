package project.arena;

import java.util.EnumSet;
import java.util.LinkedList;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.Geometry;
import project.UIController;
import project.arena.monsters.Monster;
import project.arena.towers.Tower;

import debug.ScalarFieldVisualizer;

/**
 * Manages the scalar fields of the arena.
 * @see Arena
 */
@Entity
class ArenaScalarFields {

    /**
     * The arena that the class is linked to.
     */
    private Arena arena;

    /**
     * Helper class to store a pair of x- and y- values.
     */
    private class ShortTuple {
        private short x;
        private short y;

        ShortTuple(short x, short y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Stores the shortest distance to reach the end-zone in terms of number of pixels from each pixel.
     * Indices correspond to the x- and y- coordinates.
     * @see Monster
     * @see Coordinates
     */
    private short[][] distanceToEndZone = new short[UIController.ARENA_WIDTH + 1][UIController.ARENA_WIDTH + 1];

    /**
     * Refreshes {@link #distanceToEndZone}.
     */
    private void refreshDistanceToEndZone() {
    	// Reset values
    	for (short i = 0; i <= UIController.ARENA_WIDTH; i++) {
    		for (short j = 0; j <= UIController.ARENA_HEIGHT; j++) {
    			distanceToEndZone[i][j] = Short.MAX_VALUE;
    		}
        }

        // Calculate distance
    	LinkedList<ShortTuple> openSet = new LinkedList<>();
        openSet.add(new ShortTuple(Arena.END_COORDINATES.getX(), Arena.END_COORDINATES.getY()));

    	distanceToEndZone[Arena.END_COORDINATES.getX()][Arena.END_COORDINATES.getY()] = 0;
    	while (!openSet.isEmpty()) {
    		ShortTuple current = openSet.poll();
    		// Monsters can only travel horizontally or vertically
    		LinkedList<Coordinates> neighbours = Coordinates.findTaxicabNeighbours(current.x, current.y);
    		for (Coordinates c : neighbours) {
    			// Monsters can only go to grids that do not contain a Tower
    			if (arena.findObjectsInGrid(c, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
        			short newCost = (short) (distanceToEndZone[current.x][current.y] + 1);
        			if (distanceToEndZone[c.getX()][c.getY()] > newCost ) {
        				distanceToEndZone[c.getX()][c.getY()] = newCost;
        				openSet.add(new ShortTuple(c.getX(), c.getY()));
        			}
    			}
    		}
        }
    }

    /**
     * Stores the number of attacks a lone monster that moves at 1 pixel per frame would take to reach the end-zone from each pixel.
     * Indices correspond to the x- and y- coordinates.
     * @see Monster
     * @see Coordinates
     */
    private double[][] attacksToEndZone = new double[UIController.ARENA_WIDTH + 1][UIController.ARENA_HEIGHT + 1];

    /**
     * Stores the number of attacks a lone monster would take per frame at each pixel.
     * Indices correspond to the x- and y- coordinates.
     * Note: To prevent being stuck, a very small movement cost is added.
     * @see Monster
     * @see Coordinates
     */
    private double[][] attacksPerFrame = new double[UIController.ARENA_WIDTH + 1][UIController.ARENA_HEIGHT + 1];

    /**
     * Refreshes {@link #attacksToEndZone}.
     */
    private void refreshAttacksToEndZone() {
        final double MOVEMENT_COST = 0.001;

    	// Reset values
    	for (short i = 0; i <= UIController.ARENA_WIDTH; i++) {
    		for (short j = 0; j <= UIController.ARENA_HEIGHT; j++) {
    			attacksToEndZone[i][j] = Double.POSITIVE_INFINITY;
    		}
        }

        // Calculate attacks
    	LinkedList<ShortTuple> openSet = new LinkedList<>();
        openSet.add(new ShortTuple(Arena.END_COORDINATES.getX(), Arena.END_COORDINATES.getY()));

    	attacksToEndZone[Arena.END_COORDINATES.getX()][Arena.END_COORDINATES.getY()] = 0;
    	while (!openSet.isEmpty()) {
    		ShortTuple current = openSet.poll();

    		// Monsters can only travel horizontally or vertically
    		LinkedList<Coordinates> neighbours = Coordinates.findTaxicabNeighbours(current.x, current.y);
    		for (Coordinates c : neighbours) {
    			// Monsters can only go to grids that do not contain a Tower
    			if (arena.findObjectsInGrid(c, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
                    double newCost = attacksToEndZone[current.x][current.y] + attacksPerFrame[c.getX()][c.getY()] + MOVEMENT_COST;
        			if (attacksToEndZone[c.getX()][c.getY()] > newCost ) {
        				attacksToEndZone[c.getX()][c.getY()] = newCost;
        				openSet.add(new ShortTuple(c.getX(), c.getY()));
        			}
    			}
    		}
        }

        ScalarFieldVisualizer.visualizeDoubleArray(attacksToEndZone);
    }

    /**
     * Finds the pixels that are within range of a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param minRange The minimum range.
     * @param maxRange The maximum range.
     * @return A linked list containing the coordinates of each pixel that satisfies the criteria
     */
    private LinkedList<Coordinates> getPixelsInRange(Coordinates coordinates, short minRange, short maxRange) {

        LinkedList<Coordinates> result = new LinkedList<>();

        short x = coordinates.getX();
        short y = coordinates.getY();

        for (short i = (short) Math.max(x - maxRange, 0); i <= x + maxRange && i <= UIController.ARENA_WIDTH; i++) {

            for (short j = (short) Math.max(y - maxRange, 0); j <= y + maxRange && j <= UIController.ARENA_HEIGHT; j++) {
                double distance = Geometry.findEuclideanDistance(x, y, i, j);

                if (minRange < distance && distance <= maxRange) result.add(new Coordinates(i, j));
            }
        }

        return result;
    }

    /**
     * Constructor for ArenaScalarFields class.
     * @param arena The arena to link this object to.
     */
    ArenaScalarFields(Arena arena) {
        this.arena = arena;

        LinkedList<Tower> towers = arena.getTowers();
        if (towers.size() > 0) {
            for (Tower t : towers) {
                LinkedList<Coordinates> pixelsInRange = getPixelsInRange(new Coordinates(t.getX(), t.getY()), t.getMinShootingRange(), t.getMaxShootingRange());

                // Update attacksPerFrame
                double shotsPerFrame = 1.0 / t.getReload();
                for (Coordinates c : pixelsInRange) {
                    attacksPerFrame[c.getX()][c.getY()] += shotsPerFrame;
                }
            }
        }

        refreshPathfinding();
    }

    /**
     * Constructor for making a copy of ArenaScalarFields class that is linked to another arena.
     * @param arena The arena to link this object to.
     * @param other The object to copy from.
     */
    ArenaScalarFields(Arena arena, ArenaScalarFields other) {
        this.arena = arena;

        // Copy arrays
    	for (short i = 0; i <= UIController.ARENA_WIDTH; i++) {
    		for (short j = 0; j <= UIController.ARENA_HEIGHT; j++) {
    			distanceToEndZone[i][j] = other.distanceToEndZone[i][j];
                attacksToEndZone[i][j] = other.attacksToEndZone[i][j];
                attacksPerFrame[i][j] = other.attacksPerFrame[i][j];
    		}
        }
    }

    /**
     * Accesses the distance to reach the end-zone from the specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @return The distance to reach the end-zone from the pixel, in number of pixels traversed.
     */
    short getDistanceToEndZone(@NonNull Coordinates coordinates) {
        return distanceToEndZone[coordinates.getX()][coordinates.getY()];
    }

    /**
     * Accesses the number of attacks a lone monster would take to reach the end-zone from a specified pixel. 
     * @param coordinates The coordinates of the pixel.
     * @return The number of attacks a lone monster that moves at 1 pixel per frame would get to reach the end-zone from the specified pixel. 
     */
    double getAttacksToEndZone(@NonNull Coordinates coordinates) {
        return attacksToEndZone[coordinates.getX()][coordinates.getY()];
    }

    /**
     * Accesses the number of attacks a lone monster would take to reach the end-zone from a specified pixel. 
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @return The number of attacks a lone monster that moves at 1 pixel per frame would get to reach the end-zone from the specified pixel. 
     */
    double getAttacksToEndZone(short x, short y) {
        return attacksToEndZone[x][y];
    }

    /**
     * Refreshes the scalar fields that directly influence pathfinding.
     */
    void refreshPathfinding() {
        refreshDistanceToEndZone();
        refreshAttacksToEndZone();
    }
    
    /**
     * Updates the scalar fields with respect to the addition of a tower.
     * @param tower The tower that was added.
     * @param deferRefresh Whether to defer the refreshing of scalar fields that directly influence pathfinding.
     */
    void processAddTower(@NonNull Tower tower, boolean deferRefresh) {
        LinkedList<Coordinates> pixelsInRange = getPixelsInRange(new Coordinates(tower.getX(), tower.getY()), tower.getMinShootingRange(), tower.getMaxShootingRange());

        // Update attacksPerFrame
        double shotsPerFrame = 1.0 / tower.getReload();
        for (Coordinates c : pixelsInRange) {
            attacksPerFrame[c.getX()][c.getY()] += shotsPerFrame;
        }

        if (!deferRefresh) refreshPathfinding();
    }
    
    /**
     * Updates the scalar fields with respect to the removal of a tower.
     * @param tower The tower that was removed.
     * @param deferRefresh Whether to defer the refreshing of scalar fields that directly influence pathfinding.
     */
    void processRemoveTower(@NonNull Tower tower, boolean deferRefresh) {
        LinkedList<Coordinates> pixelsInRange = getPixelsInRange(new Coordinates(tower.getX(), tower.getY()), tower.getMinShootingRange(), tower.getMaxShootingRange());

        // Update attacksPerFrame
        double shotsPerFrame = 1.0 / tower.getReload();
        for (Coordinates c : pixelsInRange) {
            attacksPerFrame[c.getX()][c.getY()] -= shotsPerFrame;
        }

        if (!deferRefresh) refreshPathfinding();
    }
}