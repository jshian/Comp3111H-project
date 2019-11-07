package project;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import project.monsters.*;
import project.projectiles.Projectile;
import project.towers.*;

import java.util.*;

import org.apache.commons.lang3.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.Image;
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
    private static final int STARTING_GRID_X_POS = 0;

    /**
     * y-coordinate of the starting position.
     * @see Coordinates
     */
    private static final int STARTING_GRID_Y_POS = 0;

    /**
     * Coordinates of the starting position.
     */
    private static final Coordinates STARTING_COORDINATES = Grid.findGridCenter(STARTING_GRID_X_POS, STARTING_GRID_Y_POS);

    /**
     * x-coordinate of the end zone.
     * @see Coordinates
     */
    private static final int END_GRID_X_POS = 11;

    /**
     * y-coordinate of the end zone.
     * @see Coordinates
     */
    private static final int END_GRID_Y_POS = 11;
    /**
     * Coordinates of the end zone.
     */
    private static final Coordinates END_COORDINATES = Grid.findGridCenter(END_GRID_X_POS, END_GRID_Y_POS);

    /**
     * The number of frames between each wave of Monsters.
     * @see #spawnWave()
     */
    private static final int WAVE_INTERVAL = 300;

    /**
     * The resources the player have to build/upgrade towers.
     */
    private IntegerProperty resources = new SimpleIntegerProperty(200);

    /**
     * The current frame number of the arena since the game began.
     */
    private int currentFrame;

    /**
     * The current difficulty of the arena.
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
     * @return A linked list containing references to the conservative estimate of the grids that are within a specified distance of the specified pixel.
     */
    private LinkedList<Grid> getPotentialGridsInRange(Coordinates coordinates, double range) {
        LinkedList<Grid> result = new LinkedList<>();

        for (int i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
            for (int j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
                int x = coordinates.getX();
                int y = coordinates.getY();
                int gridX = Grid.findGridCenterX(i, j);
                int gridY = Grid.findGridCenterY(i, j);

                if (Geometry.findEuclideanDistance(x, y, gridX, gridY)
                    <= range + Math.pow(UIController.GRID_WIDTH + UIController.GRID_HEIGHT, 2))
                    {
                        result.add(grids[i][j]);
                    }
            }
        }

        return result;
    }

    /**
     * Stores the cost for a monster in each pixel to reach the end-zone due to movement. Indices correspond to the x- and y- coordinates.
     * The cost is in terms of per unit speed of the monster.
     * This is essentially a potential field for determining monster movement.
     * @see Monster
     * @see Coordinates
     */
    private double[][] movementCostToEnd;
    
    /**
     * Stores the cost for a monster in each pixel to reach the end-zone due to movement and being attacked. Indices correspond to the x- and y- coordinates.
     * The cost is in terms of per unit speed of the monster.
     * This is essentially a potential field for determining monster movement.
     * @see Monster
     * @see Coordinates
     */
    private double[][] totalCostToEnd;
    
    /**
     * Updates the costs to reach the end-zone from each pixel.
     * @see #movementCostToEnd
     * @see #totalCostToEnd
     */
    private void updateCosts() {
    	class IntTuple {
    		private int x;
    		private int y;
    		
    		IntTuple(int x, int y) {
    			this.x = x;
    			this.y = y;
            }
            
            IntTuple(@NonNull Coordinates coordinates) {
                this.x = coordinates.getX();
                this.y = coordinates.getY();
            }
        }
        
    	// Reset values
    	for (int i = 0; i < UIController.ARENA_WIDTH; i++) {
    		for (int j = 0; j < UIController.ARENA_HEIGHT; j++) {
    			movementCostToEnd[i][j] = Double.POSITIVE_INFINITY;
    	    	totalCostToEnd[i][j] = Double.POSITIVE_INFINITY;
    		}
        }
        
        // Calculate movement costs
    	LinkedList<IntTuple> openSet = new LinkedList<>();
        openSet.add(new IntTuple(END_COORDINATES));
        movementCostToEnd[END_COORDINATES.getX()][END_COORDINATES.getY()] = 0;
    	while (!openSet.isEmpty()) {
    		IntTuple current = openSet.poll();
    		// Monsters can only travel horizontally or vertically
    		LinkedList<Coordinates> neighbours = findTaxicabNeighbours(new Coordinates(current.x, current.y));
    		for (Coordinates c : neighbours) {
    			// Monsters can only go to grids that do not contain a Tower
    			if (findObjectsInGrid(c, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
        			double newCost = movementCostToEnd[current.x][current.y] + 1;
        			if (movementCostToEnd[c.getX()][c.getY()] > newCost ) {
        				movementCostToEnd[c.getX()][c.getY()] = newCost;
        				openSet.add(new IntTuple(c.getX(), c.getY()));
        			}
    			}
    		}
        }
        
        // Calculate total costs
        final int MOVEMENT_TO_ATTACKED_COST_RATIO = 100000; // The ratio between the cost of moving one pixel and the cost of getting attacked once.

        openSet.add(new IntTuple(END_COORDINATES));

    	totalCostToEnd[END_COORDINATES.getX()][END_COORDINATES.getY()] = 0;
    	while (!openSet.isEmpty()) {
    		IntTuple current = openSet.poll();
    		
    		// Monsters can only travel horizontally or vertically
    		LinkedList<Coordinates> neighbours = findTaxicabNeighbours(new Coordinates(current.x, current.y));
    		for (Coordinates c : neighbours) {
    			// Monsters can only go to grids that do not contain a Tower
    			if (findObjectsInGrid(c, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
                    double newCost = totalCostToEnd[current.x][current.y] + 1
                        + countInRangeOfTowers(c) * MOVEMENT_TO_ATTACKED_COST_RATIO;
        			if (totalCostToEnd[c.getX()][c.getY()] > newCost ) {
        				totalCostToEnd[c.getX()][c.getY()] = newCost;
        				openSet.add(new IntTuple(c.getX(), c.getY()));
        			}
    			}
    		}
        }
    }

    /**
     * The Arena during the previous frame. Only used for saving the game.
     */
    private Arena shadowArena;

    /**
     * Adds an object to the current arena state.
     * @param obj The object to add.
     * @throws IllegalArgumentException The object type is not recognized.
     */
    private void addObjectToCurrentState(ExistsInArena obj) {
        if (obj instanceof Tower) {
            if (!towers.contains(obj))
                towers.add((Tower)obj);
        } else if (obj instanceof Projectile) {
            if (!projectiles.contains(obj))
                projectiles.add((Projectile)obj);
        } else if (obj instanceof Monster) {
            if (!monsters.contains(obj))
                monsters.add((Monster)obj);
        } else {
            throw new IllegalArgumentException("The object type is not recognized");
        }

        Coordinates c = new Coordinates(obj.getX(), obj.getY());
        getGrid(c).addObject(obj);
    }

    /**
     * Removes an object to the current arena state.
     * @param obj The object to remove.
     * @throws IllegalArgumentException The object type is not recognized.
     */
    private void removeObjectFromCurrentState(ExistsInArena obj) {
        if (obj instanceof Tower) {
            towers.remove((Tower)obj);
        } else if (obj instanceof Projectile) {
            projectiles.remove((Projectile)obj);
        } else if (obj instanceof Monster) {
            monsters.remove((Monster)obj);
        } else {
            throw new IllegalArgumentException("The object type is not recognized");
        }

        Coordinates c = new Coordinates(obj.getX(), obj.getY());
        getGrid(c).removeObject(obj);
    }

    /**
     * Constructor of the Arena class. Bind the label to resources.
     * @param resourceLabel the label to show remaining resources of player.
     */
    public Arena(@NonNull Label resourceLabel) {
        resourceLabel.textProperty().bind(Bindings.format("Money: %d", resources));
        
        // Set up grids
        for (int i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
            for (int j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
                grids[i][j] = new Grid(i, j);
            }
        }
        
        movementCostToEnd = new double[UIController.ARENA_WIDTH][UIController.ARENA_HEIGHT];
        totalCostToEnd = new double[UIController.ARENA_WIDTH][UIController.ARENA_HEIGHT];
        updateCosts();

        shadowArena = new Arena(this);
    }

    /**
     * Copy constructor of the Arena class. Performs deep copy.
     * @param other The other object to copy from.
     */
    public Arena(@NonNull Arena other) {
        this.resources = new SimpleIntegerProperty(other.resources.get());
        this.currentFrame = other.currentFrame;
        this.difficulty = other.difficulty;

        this.towers = new LinkedList<>();
        for (Tower t : other.towers) {
            
        }

        throw new NotImplementedException("TODO");
    }

    /**
     * Finds all Towers that are in the arena.
     * @return A linked list containing a reference to each Tower in the Arena.
     */
    public LinkedList<Tower> getTowers() { return towers; }

    /**
     * Finds all Monsters that are in the arena.
     * @return A priority queue containing a reference to each Monster in the Arena. The first element is closest to the end zone while the last element is furthest.
     */
    public PriorityQueue<Monster> getMonsters() { return monsters; }

    /**
     * Finds all Projectile that are in the arena.
     * @return A linked list containing a reference to each Projectile in the Arena.
     */
    public LinkedList<Projectile> getProjectiles() { return projectiles; }

    /**
     * An enum for filtering objects in the Arena according to type.
     * @see Tower
     * @see Projectile
     * @see Monster
     */
    public enum TypeFilter { Tower, Projectile, Monster }

    /**
     * Finds all objects that are located at a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public LinkedList<ExistsInArena> findObjectsAtPixel(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();
        
        LinkedList<ExistsInArena> list = getGrid(coordinates).getAllObjects();

        for (ExistsInArena obj : list)
        {
            if ((obj instanceof Tower && filter.contains(TypeFilter.Tower))
                || (obj instanceof Projectile && filter.contains(TypeFilter.Projectile))
                || (obj instanceof Monster && filter.contains(TypeFilter.Monster)))
                {
                    result.add(obj);
                }
        }

        return result;
    }

    /**
     * Finds all objects that are within a specified range of a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param range The maximum distance from this pixel for the object to be within range.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public LinkedList<ExistsInArena> findObjectsInRange(@NonNull Coordinates coordinates, double range, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();
        
        LinkedList<Grid> grids = getPotentialGridsInRange(coordinates, range);

        for (Grid grid : grids) {
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

        return result;
    }

    /**
     * Finds all objects that are located inside the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public LinkedList<ExistsInArena> findObjectsInGrid(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();
        
        LinkedList<ExistsInArena> list = getGrid(coordinates).getAllObjects();
        for (ExistsInArena obj : list)
        {
            if ((obj instanceof Tower && filter.contains(TypeFilter.Tower))
                || (obj instanceof Projectile && filter.contains(TypeFilter.Projectile))
                || (obj instanceof Monster && filter.contains(TypeFilter.Monster)))
                {
                    result.add(obj);
                }
        }

        return result;
    }

    /**
     * Finds the pixels within a taxicab distance of one from the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @return A linked list containing a reference to the coordinates of the center of each taxicab neighbour.
     */
    public LinkedList<Coordinates> findTaxicabNeighbours(@NonNull Coordinates coordinates)
    {
        LinkedList<Coordinates> result = new LinkedList<>();

        int x = coordinates.getX();
        int y = coordinates.getY();

        // Left neighbour
        if (x > 0)
            result.add(new Coordinates(x - 1, y));
        
        // Right neighbour
        if (x < UIController.ARENA_WIDTH - 1)
            result.add(new Coordinates(x + 1, y));
        
        // Top neighbour
        if (y > 0)
            result.add(new Coordinates(x, y - 1));

        // Bottom neighbour
        if (y < UIController.ARENA_HEIGHT - 1)
            result.add(new Coordinates(x, y + 1));

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

        result.addAll(findObjectsAtPixel(coordinates, EnumSet.of(TypeFilter.Projectile, TypeFilter.Monster)));
        result.addAll(findObjectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower)));
        
        return result;
    }

    /**
     * Finds the number of towers that can shoot at the specified pixel.
     * @param coordinates The coordinates of the specified pixel.
     * @return The number of towers that can shoot at the specified pixel.
     */
    public int countInRangeOfTowers(@NonNull Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();

        int count = 0;
        for (Tower t : towers) {
            int towerX = t.getX();
            int towerY = t.getY();
            int minRange = t.getShootLimit();
            int maxRange = t.getShootingRange();

            if (!Geometry.isInCircle(x, y, towerX, towerY, minRange)
                && Geometry.isInCircle(x, y, towerX, towerY, maxRange))
                {
                    count++;
                }
        }

        return count;
    }

    /**
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param type type of the tower.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     */
    public boolean canBuildTower(@NonNull Coordinates coordinates, @NonNull String type)
    {
        boolean empty = findObjectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower, TypeFilter.Monster)).isEmpty();
        if (!empty)
            return false;

        int gridX = Grid.findGridCenterX(coordinates);
        int gridY = Grid.findGridCenterY(coordinates);
        if (Geometry.isAt(gridX, gridY, STARTING_COORDINATES.getX(), STARTING_COORDINATES.getY())
            || Geometry.isAt(gridX, gridY, END_COORDINATES.getX(), END_COORDINATES.getY())
            || !hasResources(type))
            return false;

        // TODO: false when grid prevents at least one monster from reaching the end-zone by completely blocking its path.
        return true;
    }

    /**
     * check if the player has enough resources to perform the action.
     * @param cost cost of an action.
     * @return true if the player has enough resources or false otherwise.
     */
    public boolean hasResources(@NonNull int cost)
    {
        if (cost > resources.get()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * check if the player has enough resources to build the tower.
     * @param type type of the tower.
     * @return true if the player has enough resources or false otherwise.
     */
    public boolean hasResources(@NonNull String type)
    {
        int cost = 500;
        switch(type) {
            case "Basic Tower": cost = new BasicTower(this, STARTING_COORDINATES).getBuildingCost(); break;
            case "Ice Tower": cost = new IceTower(this, STARTING_COORDINATES).getBuildingCost(); break;
            case "Catapult": cost = new Catapult(this, STARTING_COORDINATES).getBuildingCost(); break;
            case "Laser Tower": cost = new LaserTower(this, STARTING_COORDINATES).getBuildingCost(); break;
        }
        return hasResources(cost);
    }

    /**
     * Builds a Tower at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param iv ImageView of the tower
     * @param type specify the class of tower.
     * @return the tower being built, or null if not enough resources
     */
    public Tower buildTower(@NonNull Coordinates coordinates, @NonNull ImageView iv, @NonNull String type)
    {
        Tower t = null;
        int cost = 0;
        switch(type) {
            case "Basic Tower": t = new BasicTower(this, coordinates, iv); break;
            case "Ice Tower": t = new IceTower(this, coordinates, iv); break;
            case "Catapult": t = new Catapult(this, coordinates, iv); break;
            case "Laser Tower": t = new LaserTower(this, coordinates, iv); break;
            default: return null;
        }
        cost = t.getBuildingCost();

        if (hasResources(cost)) {
            resources.setValue(resources.get() - cost);
            towers.add(t);
            getGrid(coordinates).addObject(t);

            updateCosts();
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
    public boolean upgradeTower(@NonNull Tower t) {
        boolean canbuild = t.upgrade(resources.getValue());
        if (canbuild) {
            resources.set(resources.getValue() - t.getUpgradeCost());

            updateCosts();
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
    public void destroyTower(@NonNull Tower tower, @NonNull AnchorPane paneArena)
    {
        paneArena.getChildren().remove(tower.getImageView());
        getGrid(new Coordinates(tower.getX(), tower.getY())).removeObject(tower);
        towers.remove(tower);

        updateCosts();
    }

    /**
     * Creates a Projectile at a specified pixel.
     * @param coordinates The coordinates of the pixel.
     */
    public void createProjectile(@NonNull Coordinates coordinates)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Removes the specified Projectile from the arena.
     * @param projectile The Projectile to be removed.
     * @param paneArena the pane where graphic of projectile needed to be removed.
     */
    public void removeProjectile(@NonNull Projectile projectile, @NonNull AnchorPane paneArena)
    {
        paneArena.getChildren().remove(projectile.getImageView());
        getGrid(new Coordinates(projectile.getX(), projectile.getY())).removeObject(projectile);
        projectiles.remove(projectile);
    }

    /**
     * Spawns a wave of Monsters at the starting position of the arena.
     */
    public void spawnWave()
    {
        final int IMAGE_SIZE = 16;
        // TODO: add UI, log. may use a function to generate each monster
        int spawnCount = (int) (1 + difficulty * 0.2 + 2 * Math.random());
        for (int i = 0; i < spawnCount; i++) {
            double randomNumber = Math.random();
            Monster newMonster;
            if (randomNumber < 1/3) {
                Image img = new Image("/fox.png", IMAGE_SIZE, IMAGE_SIZE, true, false);
                ImageView iv = new ImageView(img);
                newMonster = new Fox(this, STARTING_COORDINATES, END_COORDINATES, iv, difficulty);
            }
            else if (randomNumber < 2/3) {
                Image img = new Image("/penguin.png", IMAGE_SIZE, IMAGE_SIZE, true, false);
                ImageView iv = new ImageView(img);
                newMonster = new Penguin(this, STARTING_COORDINATES, END_COORDINATES, iv, difficulty);
            }
            else {
                Image img = new Image("/unicorn.png", IMAGE_SIZE, IMAGE_SIZE, true, false);
                ImageView iv = new ImageView(img);
                newMonster = new Unicorn(this, STARTING_COORDINATES, END_COORDINATES, iv, difficulty);
            }

            monsters.add(newMonster);
            getGrid(STARTING_COORDINATES).addObject(newMonster);
        }

        difficulty += 1;    // Modified by settings later
    }

    /**
     * Removes the specified Monster from the arena.
     * @param monster The Monster to be removed.
     * @param paneArena the pane where graphic of monster needed to be removed.
     */
    public void removeMonster(@NonNull Monster monster, @NonNull AnchorPane paneArena)
    {
        paneArena.getChildren().remove(monster.getImageView());
        getGrid(new Coordinates(monster.getX(), monster.getY())).removeObject(monster);
        monsters.remove(monster);
    }

    /**
     * Moves the specified Monster to another location.
     * @param monster The Monster to be moved.
     * @param newCoordinates The coordinates of the new location.
     * @param paneArena the pane where graphic of monster needed to be removed.
     */
    public void moveMonster(@NonNull Monster monster, @NonNull Coordinates newCoordinates, @NonNull AnchorPane paneArena)
    {
        getGrid(new Coordinates(monster.getX(), monster.getY())).removeObject(monster);

        int newX = newCoordinates.getX();
        int newY = newCoordinates.getY();
        monster.setLocation(newX, newY);

        getGrid(newCoordinates).addObject(monster);
    }

    // For debugging
    public void printCost(@NonNull Coordinates coordinates) {
        System.out.println("Movement Cost = " + movementCostToEnd[coordinates.getX()][coordinates.getY()] +
            "; Total Cost = " + totalCostToEnd[coordinates.getX()][coordinates.getY()]);
    }

    /**
     * Finds the lowest cost path from the specified pixel to the end-zone.
     * @param coordinates The coordinates of the pixel.
     * @param movementOnly Whether the method should only consider movement cost. Otherwise, it also considers the cost of being attacked by Towers.
     * @return A linked list representing the lowest cost path, with the first element being the coordinates of the first pixel to move to.
     */
    public LinkedList<Coordinates> findPathToEndZone(@NonNull Coordinates coordinates, boolean movementOnly) {
        Coordinates currentCoordinates = coordinates;

        LinkedList<Coordinates> path = new LinkedList<>();
        path.add(coordinates);

        while (!Geometry.isAt(END_COORDINATES.getX(), END_COORDINATES.getY(), currentCoordinates.getX(), currentCoordinates.getY())) {
            currentCoordinates = path.peekLast();
            LinkedList<Coordinates> neighbours = findTaxicabNeighbours(currentCoordinates);

            double lowestCost = Double.POSITIVE_INFINITY;
            Coordinates lowestCostNeighbour = null;
            for (Coordinates neighbour : neighbours) {
                double cost;
                if (movementOnly) {
                    cost = movementCostToEnd[neighbour.getX()][neighbour.getY()];
                } else {
                    cost = totalCostToEnd[neighbour.getX()][neighbour.getY()];
                }

                if (cost < lowestCost) {
                    lowestCost = cost;
                    lowestCostNeighbour = neighbour;
                }
            }

            path.add(lowestCostNeighbour);
        }

        return path;
    }

    /**
     * Updates the arena by one frame.
     */
    public void nextFrame() {
        shadowArena = new Arena(this);

        // Now update currentState
        for (Monster m : monsters) {
            m.MoveOneFrame();
        }
        throw new NotImplementedException("TODO");
        // currentState.currentFrame++;
    }

    /**
     * Draws a ray from one point to another point, extending towards the edge of the arena.
     * @return The instance of the ray.
     */
    public Line drawRay(@NonNull Coordinates source, @NonNull Coordinates target) {
        Point2D edge = Geometry.intersectBox(source.getX(), source.getY(), target.getX(), target.getY(),
                                                    0, 0, UIController.ARENA_WIDTH, UIController.ARENA_HEIGHT);
        
        return new Line(source.getX(), source.getY(), edge.getX(), edge.getY());
    }

    /**
     * Interface for objects that exist inside the arena.
     */
    public interface ExistsInArena {
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
    public interface MovesInArena extends ExistsInArena {
        /**
         * Moves the object by one frame.
         */
        public void MoveOneFrame();
    }
}