package project;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.NumberStringConverter;
import project.monsters.*;
import project.projectiles.Projectile;
import project.towers.*;

import java.util.*;

import org.apache.commons.lang3.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;

/**
 * The area where most of the action takes place in the game.
 * Monsters spawn at the starting position and try to reach the end-zone.
 * Towers reside on the arena and fire Projectiles at Monsters in range.
 * @see Monster
 * @see Tower
 * @see Projectile
 */
public final class Arena {

    /**
     * x-coordinate of the starting position.
     * @see Coordinates
     */
    private static final int STARTING_POSITION_X = 0;

    /**
     * y-coordinate of the starting position.
     * @see Coordinates
     */
    private static final int STARTING_POSITION_Y = 0;

    /**
     * Coordinates of the starting position.
     */
    private static final Coordinates STARTING_COORDINATES = new Coordinates(STARTING_POSITION_X, STARTING_POSITION_Y);

    /**
     * x-coordinate of the end zone.
     * @see Coordinates
     */
    private static final int END_ZONE_X = 440;

    /**
     * y-coordinate of the end zone.
     * @see Coordinates
     */
    private static final int END_ZONE_Y = 0;

    /**
     * Coordinates of the end zone.
     */
    private static final Coordinates END_COORDINATES = new Coordinates(END_ZONE_X, END_ZONE_Y);

    /**
     * The number of frames between each wave of Monsters.
     * @see #spawnWave()
     */
    private static final int WAVE_INTERVAL = 300;

    /**
     * The resources the player have to build/upgrade towers.
     */
    private static IntegerProperty resources = new SimpleIntegerProperty(200);

    /**
     * Describes the state of the Arena during a frame.
     */
    private static class ArenaState {
    	/**
    	 * Stores the cost for a monster in each pixel to reach the end-zone due to movement. Indices correspond to the x- and y- coordinates.
    	 * The cost is in terms of per unit speed of the monster.
    	 * @see Monster
    	 * @see Coordinates
    	 */
    	private double[][] movementCostToEnd = new double[UIController.ARENA_WIDTH][UIController.ARENA_HEIGHT];
    	
    	/**
    	 * Stores the cost for a monster in each pixel o reach the end-zone due to being attacked. Indices correspond to the x- and y- coordinates.
    	 * The cost is in terms of per unit speed of the monster.
    	 * @see Monster
    	 * @see Coordinates
    	 */
    	private double[][] attackCostToEnd = new double[UIController.ARENA_WIDTH][UIController.ARENA_HEIGHT];
    	
        /**
         * Contains a reference to each Tower on the Arena.
         * @see Tower
         */
        private LinkedList<Tower> towers = new LinkedList<>();

        /**
         * Contains a reference to each Projectile on the Arena.
         * @see Projectile
         */
        private LinkedList<Projectile> projectiles = new LinkedList<>();

        /**
         * Contains a reference to each Monster on the Arena.
         * In addition, the monsters are sorted according to how close they are from reaching the end zone.
         * The first element is closest to the end zone while the last element is furthest.
         * @see Monster
         */
        private LinkedList<Monster> monsters = new LinkedList<>();

        /**
         * The current frame number of the Arena since the game began.
         */
        private int currentFrame;

        /**
         * The current difficulty of the Arena.
         */
        private double difficulty;
    }

    /**
     * The ArenaState of the previous frame. Only used for saving the game.
     */
    private static ArenaState shadowState = null;

    /**
     * The ArenaState of the current frame.
     */
    private static ArenaState currentState = new ArenaState();

    /**
     * The default constructor of the Arena class.
     */
    public Arena() {}

    /**
     * constructor of the Arena class. Bind the label to resources.
     * @param resourceLabel the label to show remaining resources of player.
     */
    public Arena(@NonNull Label resourceLabel) {
        resourceLabel.textProperty().bind(Bindings.format("Money: %d", resources));
    }

    /**
     * An enum for filtering objects in the Arena according to type.
     * @see Tower
     * @see Projectile
     * @see Monster
     */
    public static enum TypeFilter { Tower, Projectile, Monster }

    /**
     * Finds all objects that are located at a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public static LinkedList<Object> objectsAtPixel(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<Object> list = new LinkedList<>();

        if (filter.contains(TypeFilter.Tower))
            for (Tower t : currentState.towers)
                if (coordinates.isAt(t))
                    list.add(t);
        
        if (filter.contains(TypeFilter.Projectile))
            for (Projectile p : currentState.projectiles)
                if (coordinates.isAt(p))
                    list.add(p);

        if (filter.contains(TypeFilter.Monster))
            for (Monster m : currentState.monsters)
                if (coordinates.isAt(m))
                    list.add(m);

        return list;
    }

    /**
     * Finds all objects that are within a specified range of a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param range The maximum distance from this pixel for the object to be within range.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public static LinkedList<Object> objectsInRange(@NonNull Coordinates coordinates, double range, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<Object> list = new LinkedList<>();

        if (filter.contains(TypeFilter.Tower))
            for (Tower t : currentState.towers)
                if (coordinates.diagonalDistanceFrom(t) <= range)
                    list.add(t);
        
        if (filter.contains(TypeFilter.Projectile))
            for (Projectile p : currentState.projectiles)
                if (coordinates.diagonalDistanceFrom(p) <= range)
                    list.add(p);

        if (filter.contains(TypeFilter.Monster))
            for (Monster m : currentState.monsters)
                if (coordinates.diagonalDistanceFrom(m) <= range)
                    list.add(m);

        return list;
    }

    /**
     * Finds all objects that are located inside the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public static LinkedList<Object> objectsInGrid(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<Object> list = new LinkedList<>();

        if (filter.contains(TypeFilter.Tower)) {
            for (Tower t : currentState.towers) {
                Coordinates c = new Coordinates(t.getX(), t.getY());
    
                if (coordinates.isAt(c))
                    list.add(t);
                else {
                    Grid grid = Grid.findGrid(c);
    
                    int xMin = grid.getXPos() * UIController.GRID_WIDTH;
                    int xMax = xMin + UIController.GRID_WIDTH;
                    int yMin = grid.getYPos() * UIController.GRID_HEIGHT;
                    int yMax = yMin + UIController.GRID_HEIGHT;
                    
                    int x = coordinates.getX();
                    int y = coordinates.getY();

                    if (xMin <= x && x < xMax && yMin <= y && y < yMax)
                        list.add(t);
                }
            }
        }
        
        if (filter.contains(TypeFilter.Projectile))
            for (Projectile p : currentState.projectiles)
                if (coordinates.isAt(p))
                    list.add(p);

        if (filter.contains(TypeFilter.Monster))
            for (Monster m : currentState.monsters)
                if (coordinates.isAt(m))
                    list.add(m);

        return list;
    }

    /**
     * Finds the grids within a taxicab distance of one from the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @return A linked list containing a reference to the coordinates of the center of each taxicab neighbour.
     */
    public static LinkedList<Coordinates> taxicabNeighbours(@NonNull Coordinates coordinates)
    {
        LinkedList<Coordinates> result = new LinkedList<>();

        Grid grid = Grid.findGrid(coordinates);
        int gridX = grid.getXPos();
        int gridY = grid.getYPos();
        Coordinates gridC = grid.getCenterCoordinates();

        // Left neighbour
        if (gridX > 0)
            result.add(new Coordinates(
                gridC.getX() - UIController.GRID_WIDTH,
                gridC.getY()
            ));
        
        // Right neighbour
        if (gridX < UIController.MAX_H_NUM_GRID - 1)
            result.add(new Coordinates(
                gridC.getX() + UIController.GRID_WIDTH,
                gridC.getY()
            ));
        
        // Top neighbour
        if (gridY > 0)
            result.add(new Coordinates(
                gridC.getX(),
                gridC.getY() - UIController.GRID_HEIGHT
            ));

        // Bottom neighbour
        if (gridY < UIController.MAX_V_NUM_GRID - 1)
            result.add(new Coordinates(
                gridC.getX(),
                gridC.getY() + UIController.GRID_HEIGHT
            ));

        return result;
    }

    /**
     * Finds all objects that occupy the specified coordinate in the arena.
     * @param coordinates The specified coordinates.
     * @return A linked list containing a reference to each object that satisfy the above criteria. Note that they do not have to be located at said coordinate.
     */
    public LinkedList<Object> findObjectsOccupying(@NonNull Coordinates coordinates)
    {
        LinkedList<Object> result = new LinkedList<>();

        result.addAll(objectsAtPixel(coordinates, EnumSet.of(TypeFilter.Projectile, TypeFilter.Monster)));
        result.addAll(objectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower)));
        
        return result;
    }

    /**
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     */
    public static boolean canBuildTower(@NonNull Coordinates coordinates)
    {
        return objectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower, TypeFilter.Monster)).isEmpty();
    }

    /**
     * Deduct resources from arena if the player has enough resources to perform the action.
     * @param cost cost of an action.
     * @return true if the player has enough resources or false otherwise.
     */
    public static boolean useResources(@NonNull int cost)
    {
        if (cost > resources.get()) {
            return false;
        } else {
            resources.setValue(resources.get() - cost);
            return true;
        }
    }

    /**
     * Builds a Tower at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param iv ImageView of the tower
     * @param type specify the class of tower.
     * @return the tower being built, or null if not enough resources
     */
    public static Tower buildTower(@NonNull Coordinates coordinates, @NonNull ImageView iv, @NonNull String type)
    {
        coordinates.bindByImage(iv);
        Tower t = null;
        int cost = 0;
        switch(type) {
            case "Basic Tower": t = new BasicTower(coordinates, iv); break;
            case "Ice Tower": t = new IceTower(coordinates, iv); break;
            case "Catapult": t = new Catapult(coordinates, iv); break;
            case "Laser Tower": t = new LaserTower(coordinates, iv); break;
            default: return null;
        }
        cost = t.getBuildingCost();

        if (useResources(cost)) {
            currentState.towers.add(t);
            return t;
        } else {
            return null;
        }

    }

    /**
     * Destroys the specified Tower.
     * @param tower The Tower to be destroyed.
     * @param paneArena the pane where graphic of Tower needed to be removed.
     */
    public static void destroyTower(@NonNull Tower tower, @NonNull AnchorPane paneArena)
    {
        paneArena.getChildren().remove(tower.getImageView());
        currentState.towers.remove(tower);
    }

    /**
     * Creates a Projectile at a specified pixel.
     * @param coordinates The coordinates of the pixel.
     */
    public static void createProjectile(@NonNull Coordinates coordinates)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Removes the specified Projectile from the arena.
     * @param projectile The Projectile to be removed.
     * @param paneArena the pane where graphic of projectile needed to be removed.
     */
    public static void removeProjectile(@NonNull Projectile projectile, @NonNull AnchorPane paneArena)
    {
        paneArena.getChildren().remove(projectile.getImageView());
        currentState.projectiles.remove(projectile);
    }

    /**
     * Spawns a wave of Monsters at the starting position of the arena.
     */
    public static void spawnWave()
    {
        int spawnCount = (int) (1 + currentState.difficulty * 0.2 + 2 * Math.random());
        for (int i = 0; i < spawnCount; i++) {
            double randomNumber = Math.random();
            if (randomNumber < 1/3)
                currentState.monsters.add(new Fox(currentState.difficulty, STARTING_COORDINATES, END_COORDINATES));
            else if (randomNumber < 2/3)
                currentState.monsters.add(new Penguin(currentState.difficulty, STARTING_COORDINATES, END_COORDINATES));
            else
                currentState.monsters.add(new Unicorn(currentState.difficulty, STARTING_COORDINATES, END_COORDINATES));
        }

        currentState.difficulty += 1;    // Modified by settings later
    }

    /**
     * Removes the specified Monster from the arena.
     * @param monster The Monster to be removed.
     * @param paneArena the pane where graphic of monster needed to be removed.
     */
    public static void removeMonster(@NonNull Monster monster, @NonNull AnchorPane paneArena)
    {
        paneArena.getChildren().remove(monster.getImageView());
        currentState.monsters.remove(monster);
    }
    
    /**
     * Updates the costs to reach the end-zone from each pixel for the current ArenaState.
     * @see ArenaState#MovementCostToEnd
     * @see ArenaState#attackCostToEnd
     */
    private static void updateCosts() {
    	class IntTuple {
    		private int x;
    		private int y;
    		
    		IntTuple(int x, int y) {
    			this.x = x;
    			this.y = y;
    		}
    	}
    	
    	PriorityQueue<IntTuple> openSet = new PriorityQueue<>(UIController.ARENA_WIDTH * UIController.ARENA_HEIGHT - currentState.towers.size());
    	openSet.add(new IntTuple(END_ZONE_X, END_ZONE_Y));
    	
    	// Reset values
    	for (int i = 0; i < END_ZONE_X; i++) {
    		for (int j = 0; j < END_ZONE_Y; j++) {
    			currentState.movementCostToEnd[i][j] = Double.POSITIVE_INFINITY;
    	    	currentState.attackCostToEnd[i][j] = 0; // Placeholder. Should count number of Towers that cover this pixel.
    		}
    	}
    	
    	currentState.movementCostToEnd[END_ZONE_X][END_ZONE_Y] = 0;
    	
    	while (!openSet.isEmpty()) {
    		IntTuple current = openSet.poll();
    		
    		// Monsters can only travel horizontally or vertically
    		LinkedList<Coordinates> neighbours = taxicabNeighbours(new Coordinates(current.x, current.y));
    		for (Coordinates c : neighbours) {
    			// Monsters can only go to grids that do not contain a Tower
    			if (objectsInGrid(c, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
        			double newCost = currentState.movementCostToEnd[current.x][current.y] + 1;
        			if (currentState.movementCostToEnd[c.getX()][c.getY()] > newCost ) {
        				currentState.movementCostToEnd[c.getX()][c.getY()] = newCost;
        				openSet.add(new IntTuple(c.getX(), c.getY()));
        			}
    			}
    		}
    	}
    }

    /**
     * Updates the arena by one frame.
     */
    public static void nextFrame() {
        shadowState = currentState;

        // Now update currentState
        throw new NotImplementedException("TODO");
        // currentState.monsters.sort(null);
        // currentState.currentFrame++;
    }

    /**
     * Finds all Towers that are in the arena.
     * @return A READONLY linked list containing a reference to each Tower in the Arena.
     */
    public static LinkedList<Tower> getTowers() { return (LinkedList<Tower>) Collections.unmodifiableList(currentState.towers); }

    /**
     * Finds all Monsters that are in the arena.
     * @return A READONLY linked list containing a reference to each Monster in the Arena. The first element is closest to the end zone while the last element is furthest.
     */
    public static LinkedList<Monster> getMonsters() { return (LinkedList<Monster>) Collections.unmodifiableList(currentState.monsters); }

    /**
     * Finds all Projectile that are in the arena.
     * @return A READONLY linked list containing a reference to each Projectile in the Arena.
     */
    public static LinkedList<Projectile> getProjectiles() { return (LinkedList<Projectile>) Collections.unmodifiableList(currentState.projectiles); }

    /**
     * Interface for objects that exist inside the arena.
     */
    public static interface ExistsInArena {
        /**
         * Access the ImageView associated with the object.
         * @return The ImageView associated with the object.
         */
        public ImageView getImageView();
    
        /**
         * Accesses the x-coordinate of the object.
         * @return The x-coordinate of the object.
         * @see Coordinates
         */
        public int getX();

        /**
         * Accesses the y-coordinate of the object.
         * @return The y-coordinate of the object.
         * @see Coordinates
         */
        public int getY();

        /**
         * Updates the corresponding UI object.
         */
        public void refreshDisplay();

        /**
         * Updates the coordinates of the object.
         * @param x The new x-coordinate.
         * @param y The new y-coordinate.
         * @see Coordinates
         */
        public void setLocation(int x, int y);
    }
    
    /**
     * Interface for objects that can move inside the Arena.
     */
    public static interface MovesInArena extends ExistsInArena {
        /**
         * Moves the object by one frame.
         */
        public void MoveOneFrame();
    }
}