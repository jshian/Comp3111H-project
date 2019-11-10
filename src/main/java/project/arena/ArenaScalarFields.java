package project.arena;

import java.util.EnumSet;
import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.Geometry;
import project.UIController;
import project.arena.monsters.Monster;
import project.arena.towers.Tower;

/**
 * Manages the scalar fields of the arena.
 * @see Arena
 */
class ArenaScalarFields {

    /**
     * The arena that the class is linked to.
     */
    private Arena arena;

    /**
     * Helper class to store a pair of x- and y- values.
     */
    private class IntTuple {
        private int x;
        private int y;

        IntTuple(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Stores the shortest distance to reach the end-zone in terms of number of grids from each grid.
     * Indices correspond to the x- and y- positions.
     * @see Monster
     * @see Grid
     */
    private int[][] distanceToEndZone = new int[UIController.MAX_H_NUM_GRID][UIController.MAX_V_NUM_GRID];

    /**
     * Refreshes {@link #distanceToEndZone}.
     */
    private void refreshDistanceToEndZone() {
        
    	// Reset values
    	for (int i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
    		for (int j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
    			distanceToEndZone[i][j] = Integer.MAX_VALUE;
    		}
        }

        // Calculate distance
    	LinkedList<IntTuple> openSet = new LinkedList<>();
        openSet.add(new IntTuple(Arena.END_GRID_X_POS, Arena.END_GRID_Y_POS));
        distanceToEndZone[Arena.END_GRID_X_POS][Arena.END_GRID_Y_POS] = 0;
    	while (!openSet.isEmpty()) {
    		IntTuple current = openSet.poll();
    		// Monsters can only travel horizontally or vertically
    		LinkedList<int[]> neighbours = Grid.findTaxicabNeighbours(current.x, current.y);
    		for (int[] pos : neighbours) {
                int xPos = pos[0];
                int yPos = pos[1];

    			// Monsters can only go to grids that do not contain a Tower
    			if (arena.findObjectsInGrid(xPos, yPos, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
        			int newCost = distanceToEndZone[current.x][current.y] + 1;
        			if (distanceToEndZone[xPos][yPos] > newCost ) {
        				distanceToEndZone[xPos][yPos] = newCost;
        				openSet.add(new IntTuple(xPos, yPos));
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
     * @see Monster
     * @see Coordinates
     */
    private double[][] attacksPerFrame = new double[UIController.ARENA_WIDTH + 1][UIController.ARENA_HEIGHT + 1];

    /**
     * Refreshes {@link #attacksToEndZone}.
     */
    private void refreshAttacksToEndZone() {
    	// Reset values
    	for (int i = 0; i <= UIController.ARENA_WIDTH; i++) {
    		for (int j = 0; j <= UIController.ARENA_HEIGHT; j++) {
    			attacksToEndZone[i][j] = Double.POSITIVE_INFINITY;
    		}
        }

        // Calculate attacks
    	LinkedList<IntTuple> openSet = new LinkedList<>();
        openSet.add(new IntTuple(Arena.END_COORDINATES.getX(), Arena.END_COORDINATES.getY()));

    	attacksToEndZone[Arena.END_COORDINATES.getX()][Arena.END_COORDINATES.getY()] = 0;
    	while (!openSet.isEmpty()) {
    		IntTuple current = openSet.poll();

    		// Monsters can only travel horizontally or vertically
    		LinkedList<Coordinates> neighbours = Coordinates.findTaxicabNeighbours(current.x, current.y);
    		for (Coordinates c : neighbours) {
    			// Monsters can only go to grids that do not contain a Tower
    			if (arena.findObjectsInGrid(c, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
                    double newCost = attacksToEndZone[current.x][current.y] + attacksPerFrame[current.x][current.y];
        			if (attacksToEndZone[c.getX()][c.getY()] > newCost ) {
        				attacksToEndZone[c.getX()][c.getY()] = newCost;
        				openSet.add(new IntTuple(c.getX(), c.getY()));
        			}
    			}
    		}
        }
    }

    /**
     * Finds the pixels that are within range of a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param minRange The minimum range.
     * @param maxRange The maximum range.
     * @return A linked list containing the coordinates of each pixel that satisfies the criteria
     */
    private LinkedList<Coordinates> getPixelsInRange(Coordinates coordinates, int minRange, int maxRange) {

        LinkedList<Coordinates> result = new LinkedList<>();

        int x = coordinates.getX();
        int y = coordinates.getY();

        for (int i = x - maxRange; i <= x + maxRange; i++) {
            for (int j = y - maxRange; j <= y + maxRange; j++) {
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
                double shotsPerFrame = 1 / t.getReload();
                for (Coordinates c : pixelsInRange) {
                    attacksPerFrame[c.getX()][c.getY()] -= shotsPerFrame;
                }
            }

            refreshAttacksToEndZone();
        }

        refreshDistanceToEndZone();
    }

    /**
     * Constructor for making a copy of ArenaScalarFields class that is linked to another arena.
     * @param arena The arena to link this object to.
     * @param other The object to copy from.
     */
    ArenaScalarFields(Arena arena, ArenaScalarFields other) {
        this.arena = arena;

        // Copy distanceToEndZone
        for (int i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
    		for (int j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
    			distanceToEndZone[i][j] = other.distanceToEndZone[i][j];
    		}
        }

        // Copy attacksToEndZone and attacksPerFrame
    	for (int i = 0; i <= UIController.ARENA_WIDTH; i++) {
    		for (int j = 0; j <= UIController.ARENA_HEIGHT; j++) {
                attacksToEndZone[i][j] = other.attacksToEndZone[i][j];
                attacksPerFrame[i][j] = other.attacksPerFrame[i][j];
    		}
        }
    }

    /**
     * Accesses the distance to reach the end-zone from the grid containing a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @return The distance to reach the end-zone from the grid, in number of grids traversed.
     */
    int getDistanceToEndZone(@NonNull Coordinates coordinates) {
        return distanceToEndZone[Grid.findGridXPos(coordinates)][Grid.findGridYPos(coordinates)];
    }

    /**
     * Accesses the distance to reach the end-zone from a specified grid.
     * @param xPos The x-position of the grid.
     * @param yPos The y-position of the grid.
     * @return The distance to reach the end-zone from the grid, in number of grids traversed.
     */
    int getDistanceToEndZone(int xPos, int yPos) {
        return distanceToEndZone[xPos][yPos];
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
    double getAttacksToEndZone(int x, int y) {
        return attacksToEndZone[x][y];
    }
    
    /**
     * Updates the scalar fields with respect to the addition of a tower.
     * @param tower The tower that was added.
     */
    void processAddTower(@NonNull Tower tower) {
        LinkedList<Coordinates> pixelsInRange = getPixelsInRange(new Coordinates(tower.getX(), tower.getY()), tower.getMinShootingRange(), tower.getMaxShootingRange());

        // Update attacksPerFrame
        double shotsPerFrame = 1 / tower.getReload();
        for (Coordinates c : pixelsInRange) {
            attacksPerFrame[c.getX()][c.getY()] += shotsPerFrame;
        }

        refreshDistanceToEndZone();
        refreshAttacksToEndZone();
    }
    
    /**
     * Updates the scalar fields with respect to the removal of a tower.
     * @param tower The tower that was removed.
     */
    void processRemoveTower(@NonNull Tower tower) {
        LinkedList<Coordinates> pixelsInRange = getPixelsInRange(new Coordinates(tower.getX(), tower.getY()), tower.getMinShootingRange(), tower.getMaxShootingRange());

        // Update attacksPerFrame
        double shotsPerFrame = 1 / tower.getReload();
        for (Coordinates c : pixelsInRange) {
            attacksPerFrame[c.getX()][c.getY()] -= shotsPerFrame;
        }

        refreshDistanceToEndZone();
        refreshAttacksToEndZone();
    }
}