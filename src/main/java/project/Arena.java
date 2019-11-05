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
     * Describes the state of the Arena during a frame.
     */
    private static class ArenaState {
        /**
         * The current frame number of the Arena since the game began.
         */
        private int currentFrame;

        /**
         * The current difficulty of the Arena.
         */
        private double difficulty;

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
         * Stores grid information of the arena.
         */
        private Grid[][] grids = new Grid[UIController.MAX_H_NUM_GRID][UIController.MAX_V_NUM_GRID];

        /**
         * Accesses the grid that contains the specified pixel.
         * @param coordinates The coordinates of the pixel.
         */
        private Grid getGrid(Coordinates coordinates) {
            return grids[Grid.findGridXPos(coordinates)][Grid.findGridYPos(coordinates)];
        }

        /**
         * Finds the grids that may be within a specified distance of a specified pixel.
         * @param The coordinates of the pixel.
         * @param range The maximum allowable distance.
         * @return A READONLY linked list containing references to the conservative estimate of the grids that are within a specified distance of the specified pixel.
         */
        private LinkedList<Grid> getPotentialGridsInRange(Coordinates coordinates, double range) {
            LinkedList<Grid> result = new LinkedList<>();

            for (int i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
                for (int j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
                    Coordinates otherGridCoordinates = Grid.findGridCenter(grids[i][j].getXPos(), grids[i][j].getYPos());
                    if (coordinates.diagonalDistanceFrom(otherGridCoordinates)
                        <= range + Math.pow(UIController.GRID_WIDTH + UIController.GRID_HEIGHT, 2))
                        {
                            result.add(grids[i][j]);
                        }
                }
            }

            return (LinkedList<Grid>) Collections.unmodifiableList(result);
        }

    	/**
    	 * Stores the cost for a monster in each pixel to reach the end-zone due to movement. Indices correspond to the x- and y- coordinates.
    	 * The cost is in terms of per unit speed of the monster.
    	 * @see Monster
    	 * @see Coordinates
    	 */
    	private double[][] movementCostToEnd = new double[UIController.ARENA_WIDTH][UIController.ARENA_HEIGHT];
    	
    	/**
    	 * Stores the cost for a monster in each pixel to reach the end-zone due to being attacked. Indices correspond to the x- and y- coordinates.
    	 * The cost is in terms of per unit speed of the monster.
    	 * @see Monster
    	 * @see Coordinates
    	 */
        private double[][] attackCostToEnd = new double[UIController.ARENA_WIDTH][UIController.ARENA_HEIGHT];
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
     * The ArenaState of the current frame.
     */
    private static Player player;

    /**
     * Adds an object to the current arena state.
     * @param obj The object to add.
     * @throws IllegalArgumentException The object type is not recognized.
     */
    private void addObjectToCurrentState(ExistsInArena obj) {
        if (obj instanceof Tower) {
            if (!currentState.towers.contains(obj))
                currentState.towers.add((Tower)obj);
        } else if (obj instanceof Projectile) {
            if (!currentState.projectiles.contains(obj))
                currentState.projectiles.add((Projectile)obj);
        } else if (obj instanceof Monster) {
            if (!currentState.monsters.contains(obj))
                currentState.monsters.add((Monster)obj);
        } else {
            throw new IllegalArgumentException("The object type is not recognized");
        }

        Coordinates c = new Coordinates(obj.getX(), obj.getY());
        currentState.getGrid(c).addObject(obj);
    }

    /**
     * Removes an object to the current arena state.
     * @param obj The object to remove.
     * @throws IllegalArgumentException The object type is not recognized.
     */
    private void removeObjectFromCurrentState(ExistsInArena obj) {
        if (obj instanceof Tower) {
            currentState.towers.remove((Tower)obj);
        } else if (obj instanceof Projectile) {
            currentState.projectiles.remove((Projectile)obj);
        } else if (obj instanceof Monster) {
            currentState.monsters.remove((Monster)obj);
        } else {
            throw new IllegalArgumentException("The object type is not recognized");
        }

        Coordinates c = new Coordinates(obj.getX(), obj.getY());
        currentState.getGrid(c).removeObject(obj);
    }

    /**
     * Constructor of the Arena class. Bind the label to resources.
     * @param resourceLabel the label to show remaining resources of player.
     */
    public Arena(@NonNull Label resourceLabel) {
        player = new Player("name", 200);
        resourceLabel.textProperty().bind(Bindings.format("Money: %d", player.resourcesProperty()));
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
    public static LinkedList<ExistsInArena> objectsAtPixel(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();
        
        // READONLY
        LinkedList<ExistsInArena> list = currentState.getGrid(coordinates).getAllObjects();

        for (ExistsInArena obj : list)
        {
            if ((obj instanceof Tower && filter.contains(TypeFilter.Tower))
                || (obj instanceof Projectile && filter.contains(TypeFilter.Projectile))
                || (obj instanceof Monster && filter.contains(TypeFilter.Monster)))
                {
                    result.add(obj);
                }
        }

        return (LinkedList<ExistsInArena>) Collections.unmodifiableList(result);
    }

    /**
     * Finds all objects that are within a specified range of a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param range The maximum distance from this pixel for the object to be within range.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public static LinkedList<ExistsInArena> objectsInRange(@NonNull Coordinates coordinates, double range, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();

        // READONLY
        LinkedList<Grid> grids = currentState.getPotentialGridsInRange(coordinates, range);

        for (Grid grid : grids) {
            // READONLY
            LinkedList<ExistsInArena> list = grid.getAllObjects();

            for (ExistsInArena obj : list)
            {
                if ((obj instanceof Tower && filter.contains(TypeFilter.Tower))
                    || (obj instanceof Projectile && filter.contains(TypeFilter.Projectile))
                    || (obj instanceof Monster && filter.contains(TypeFilter.Monster)))
                    {
                        result.add(obj);
                    }
            }
        }

        return (LinkedList<ExistsInArena>) Collections.unmodifiableList(result);
    }

    /**
     * Finds all objects that are located inside the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel
     * @param filter Only the types that are specified will be included in the result.
     * @return A READONLY linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public static LinkedList<ExistsInArena> objectsInGrid(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();

        // READONLY
        LinkedList<ExistsInArena> list = currentState.getGrid(coordinates).getAllObjects();

        for (ExistsInArena obj : list)
        {
            if ((obj instanceof Tower && filter.contains(TypeFilter.Tower))
                || (obj instanceof Projectile && filter.contains(TypeFilter.Projectile))
                || (obj instanceof Monster && filter.contains(TypeFilter.Monster)))
                {
                    result.add(obj);
                }
        }

        return (LinkedList<ExistsInArena>) Collections.unmodifiableList(result);
    }

    /**
     * Finds the grids within a taxicab distance of one from the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @return A linked list containing a reference to the coordinates of the center of each taxicab neighbour.
     */
    public static LinkedList<Coordinates> taxicabNeighbours(@NonNull Coordinates coordinates)
    {
        LinkedList<Coordinates> result = new LinkedList<>();

        Grid grid = currentState.getGrid(coordinates);
        int gridXPos = grid.getXPos();
        int gridYPos = grid.getYPos();

        // Left neighbour
        if (gridXPos > 0)
            result.add(Grid.findGridCenter(gridXPos - 1, gridYPos));
        
        // Right neighbour
        if (gridXPos < UIController.MAX_H_NUM_GRID - 1)
            result.add(Grid.findGridCenter(gridXPos + 1, gridYPos));
        
        // Top neighbour
        if (gridYPos > 0)
            result.add(Grid.findGridCenter(gridXPos, gridYPos - 1));

        // Bottom neighbour
        if (gridYPos < UIController.MAX_V_NUM_GRID - 1)
            result.add(Grid.findGridCenter(gridXPos, gridYPos + 1));

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
     * check if the player has enough resources to build the tower.
     * @param type type of the tower.
     * @return true if the player has enough resources or false otherwise.
     */
    public static boolean hasResources(@NonNull String type)
    {
        int cost = 500;
        Coordinates c = new Coordinates(0,0);
        switch(type) {
            case "Basic Tower": cost = new BasicTower(c).getBuildingCost(); break;
            case "Ice Tower": cost = new IceTower(c).getBuildingCost(); break;
            case "Catapult": cost = new Catapult(c).getBuildingCost(); break;
            case "Laser Tower": cost = new LaserTower(c).getBuildingCost(); break;
        }
        return player.hasResources(cost);
    }


    /**
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param type type of the tower.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     */
    public static boolean canBuildTower(@NonNull Coordinates coordinates, @NonNull String type)
    {
        boolean empty = objectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower, TypeFilter.Monster)).isEmpty();
        if (!empty)
            return false;

        Coordinates gridCoordinates = Grid.findGridCenter(coordinates);
        if(gridCoordinates.isAt(STARTING_COORDINATES) || gridCoordinates.isAt(END_COORDINATES)
            || !hasResources(type))
            return false;

        // TODO: false when grid prevents at least one monster from reaching the end-zone by completely blocking its path.
        return true;
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

        if (player.hasResources(cost)) {
            player.spendResources(cost);
            currentState.towers.add(t);
            return t;
        } else {
            return null;
        }

    }

    /**
     * Upgrade the tower at the grid where a specified pixel is located.
     * @param t the tower to be upgrade
     * @return true if upgrade is successful, false if player don't have enough resources.
     */
    public static boolean upgradeTower(@NonNull Tower t) {
        boolean canbuild = t.upgrade(player.getResources());
        if (canbuild) {
            player.spendResources(t.getUpgradeCost());
            return true;
        } else {
            return false;
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
        // TODO: generate monster in fixed period of time instead.
//        int spawnCount = (int) (1 + currentState.difficulty * 0.2 + 2 * Math.random());
//        for (int i = 0; i < spawnCount; i++) {
//            double randomNumber = Math.random();
//            if (randomNumber < 1/3)
//                currentState.monsters.add(new Fox(currentState.difficulty, STARTING_COORDINATES, END_COORDINATES));
//            else if (randomNumber < 2/3)
//                currentState.monsters.add(new Penguin(currentState.difficulty, STARTING_COORDINATES, END_COORDINATES));
//            else
//                currentState.monsters.add(new Unicorn(currentState.difficulty, STARTING_COORDINATES, END_COORDINATES));
//        }

        currentState.difficulty += 1;    // Modified by settings later
    }

    /**
     * Generate a monster to t he arena.
     * @param iv the image of the monster.
     * @param type specify the type of the monster.
     * @return the monster being generated.
     */
    public static Monster generateMonster(@NonNull ImageView iv, @NonNull String type)
    {
        Monster m = null;
        Coordinates c = new Coordinates(STARTING_POSITION_X, STARTING_POSITION_Y);
        switch(type) {
            case "Fox": m = new Fox(currentState.difficulty, c, END_COORDINATES, iv); break;
            case "Penguin": m = new Penguin(currentState.difficulty, c, END_COORDINATES, iv); break;
            case "Unicorn": m = new Unicorn(currentState.difficulty, c, END_COORDINATES, iv); break;
        }
        if (m == null)
            return null;
        currentState.monsters.add(m);
        System.out.println(String.format("%s:%f generated", type, m.getHealth()));
        currentState.difficulty += 1;    // Modified by settings later
        return m;
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
     * @see ArenaState#movementCostToEnd
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
        for (Monster m : currentState.monsters) {
            m.MoveOneFrame();
        }
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
     * @return A READONLY priority queue containing a reference to each Monster in the Arena. The first element is closest to the end zone while the last element is furthest.
     */
    public static PriorityQueue<Monster> getMonsters() { return (PriorityQueue<Monster>) Collections.unmodifiableCollection(currentState.monsters); }

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