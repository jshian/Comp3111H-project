package project;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import project.monsters.*;
import project.projectiles.CatapultProjectile;
import project.projectiles.IceProjectile;
import project.projectiles.Projectile;
import project.towers.*;

import java.lang.reflect.Array;
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
    private static final int END_GRID_Y_POS = 0;
    /**
     * Coordinates of the end zone.
     */
    private static final Coordinates END_COORDINATES = Grid.findGridCenter(END_GRID_X_POS, END_GRID_Y_POS);

    /**
     * The number of frames between each wave of Monsters.
     * @see #spawnWave()
     */
    private static final int WAVE_INTERVAL = 50;

    /**
     * The duration of laser being displayed.
     */
    private static final int laserDuration = 2;

    /**
     * The current frame number of the arena since the game began.
     */
    private int currentFrame;

    /**
     * The current difficulty of the arena.
     */
    private double difficulty = 1;

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
     * Contains a reference to each laser shot by LaserTower on the arena.
     */
    private HashMap<Line, Integer> lasers = new HashMap<>();

    /**
     * Contains a reference explosion of monster when it died.
     */
    private HashMap<ImageView, Integer> explosions = new HashMap<>();

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
         * @param coordinates The coordinates of the pixel.
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
     * @see Monster
     * @see Coordinates
     */
    private double[][] movementCostToEnd;

    /**
     * Stores the cost for a monster in each pixel to reach the end-zone due to movement and being attacked. Indices correspond to the x- and y- coordinates.
     * The cost is in terms of per unit speed of the monster.
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
     * The player of the game.
     */
    private static Player player;

    /**
     * The arena of the game.
     */
    private static AnchorPane paneArena;

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
     * @param paneArena the arena pane of the game.
     */
    public Arena(@NonNull Label resourceLabel, @NonNull AnchorPane paneArena) {
        player = new Player("name", 200);
        resourceLabel.textProperty().bind(Bindings.format("Money: %d", player.resourcesProperty()));
        this.paneArena = paneArena;

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
        this.player = new Player(other.player.getName(), other.player.getResources());
        this.currentFrame = other.currentFrame;
        this.difficulty = other.difficulty;

        this.towers = new LinkedList<>();
        for (Tower t : other.towers) {

        }
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
     * An enum for generate monster in the Arena according to type.
     */
    public static enum MonsterType { Fox, Penguin, Unicorn }

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
     * check if the player has enough resources to build the tower.
     * @param type type of the tower.
     * @return true if the player has enough resources or false otherwise.
     */
    public boolean hasResources(@NonNull String type)
    {
        int cost = 500;
        Coordinates c = new Coordinates(0,0);
        switch(type) {
            case "Basic Tower": cost = new BasicTower(this, c).getBuildingCost(); break;
            case "Ice Tower": cost = new IceTower(this, c).getBuildingCost(); break;
            case "Catapult": cost = new Catapult(this, c).getBuildingCost(); break;
            case "Laser Tower": cost = new LaserTower(this, c).getBuildingCost(); break;
        }
        return player.hasResources(cost);
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
            || !hasResources(type) || !hasRoute(coordinates))
            return false;
        return true;
    }

    /**
     * Determines whether some monster cannot go to end-zone after building a tower.
     * @param coordinates The coordinates of tower to be built.
     * @return true if all monster can go to end-zone after building a tower, false otherwise.
     */
    private boolean hasRoute(@NonNull Coordinates coordinates) {
        Grid gridToBeBuilt = getGrid(coordinates);

        boolean[][] noTower = new boolean[UIController.MAX_H_NUM_GRID][UIController.MAX_H_NUM_GRID];
        boolean[][] visited = new boolean[UIController.MAX_H_NUM_GRID][UIController.MAX_H_NUM_GRID];
        for (int i = 0; i < noTower.length; i++) {
            for (int j = 0; j < noTower[0].length; j++) {
                noTower[i][j] = findObjectsInGrid(Grid.findGridCenter(i,j), EnumSet.of(TypeFilter.Tower)).isEmpty();
                visited[i][j] = false;
            }
        }
        noTower[gridToBeBuilt.getXPos()][gridToBeBuilt.getYPos()] = false;
        gridDFS(noTower, visited, Grid.findGridXPos(END_COORDINATES), Grid.findGridYPos(END_COORDINATES));

        ArrayList<Grid> hasMonster = new ArrayList<>();
        hasMonster.add(new Grid(0,0));
        for (Monster m : monsters) {
            Coordinates c = new Coordinates(m.getX(), m.getY());
            hasMonster.add(getGrid(c));
        }

        for (Grid g : hasMonster) {
            if (!visited[g.getXPos()][g.getYPos()])
                return false;
        }
        return true;
    }

    /**
     * helper function of hasRoute(). It performs DFS to find positions of monster where it can go to a destination.
     * @param noTower 2d boolean array to indicate which grid does not contain tower.
     * @param visited 2d boolean array to indicate which grid has already visited by DFS.
     * @param x x-position of the destination.
     * @param y y-position of the destination.
     */
    private void gridDFS(@NonNull boolean[][] noTower, @NonNull boolean[][] visited, int x, int y)
    {
        if (x < 0 || y < 0 || x >= noTower.length || y >= noTower[0].length)
            return;
        if (visited[x][y] == true || noTower[x][y] == false)
            return;
        visited[x][y] = true;

        gridDFS(noTower, visited, x+1, y);
        gridDFS(noTower, visited, x-1, y);
        gridDFS(noTower, visited, x, y+1);
        gridDFS(noTower, visited, x, y-1);
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
        Coordinates center = Grid.findGridCenter(coordinates);
        switch(type) {
            case "Basic Tower": t = new BasicTower(this, center, iv); break;
            case "Ice Tower": t = new IceTower(this, center, iv); break;
            case "Catapult": t = new Catapult(this, center, iv); break;
            case "Laser Tower": t = new LaserTower(this, center, iv); break;
            default: return null;
        }
        cost = t.getBuildingCost();

        if (player.hasResources(cost)) {
            player.spendResources(cost);
            towers.add(t);
            getGrid(coordinates).addObject(t);
            return t;
        } else {
            return null;
        }
        // TODO: recalculate monster path.
    }

    /**
     * Upgrade the tower at the grid where a specified pixel is located.
     * @param t the tower to be upgrade
     * @return true if upgrade is successful, false if player don't have enough resources.
     */
    public boolean upgradeTower(@NonNull Tower t) {
        boolean canBuild = t.upgrade(player.getResources());
        if (canBuild) {
            player.spendResources(t.getUpgradeCost());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Destroys the specified Tower.
     * @param tower The Tower to be destroyed.
     */
    public void destroyTower(@NonNull Tower tower)
    {
        paneArena.getChildren().remove(tower.getImageView());
        getGrid(new Coordinates(tower.getX(), tower.getY())).removeObject(tower);
        towers.remove(tower);
    }

    /**
     * Creates a Projectile at a specified pixel.
     * @param t the tower which attack the monster by creating projectile.
     */
    public void createProjectile(@NonNull Tower t)
    {
        // laser tower
        if (t instanceof LaserTower) {
            ((LaserTower) t).attackMonster();
            Line laserLine = ((LaserTower) t).getLaserLine();
            if (laserLine != null && !lasers.containsKey(laserLine)) {
                lasers.put(laserLine, currentFrame);
                paneArena.getChildren().add(laserLine);
            }

        } else { // other towers
            Projectile p = t.attackMonster();
            if (p != null) {
                paneArena.getChildren().add(p.getImageView());
                projectiles.add(p);
                getGrid(new Coordinates(p.getX(), p.getY())).addObject(p);
            }
        }
    }

    /**
     * Move the specified Projectile.
     * @param projectile The Projectile to be moved.
     */
    public void moveProjectile(@NonNull Projectile projectile) {
        getGrid(new Coordinates(projectile.getX(), projectile.getY())).removeObject(projectile);
        projectile.moveOneFrame();
        getGrid(new Coordinates(projectile.getX(), projectile.getY())).addObject(projectile);
    }

    /**
     * Removes the specified Projectile from the arena.
     * @param projectile The Projectile to be removed.
     */
    public void removeProjectile(@NonNull Projectile projectile)
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
        int spawnCount = (int) (1 + difficulty * 0.2 + 2 * Math.random());
        for (int i = 0; i < spawnCount; i++) {
            double randomNumber = Math.random();
            if (randomNumber < 1/3.0)
                generateMonster(MonsterType.Fox);
            else if (randomNumber < 2/3.0)
                generateMonster(MonsterType.Penguin);
            else
                generateMonster(MonsterType.Unicorn);
        }

        difficulty += 1;
    }

    /**
     * Generate a monster to the arena.
     * @param type specify the type of the monster.
     * @return the monster being generated.
     */
    public Monster generateMonster(@NonNull MonsterType type)
    {
        Monster m = null;
        ImageView iv = null;
        // we need to update c when monster move so create a new Coordinate instead.
        Coordinates start = Grid.findGridCenter(STARTING_COORDINATES);
        Coordinates end = Grid.findGridCenter(END_COORDINATES);
        switch(type) {
            case Fox: iv = new ImageView(new Image("/fox.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
                m = new Fox(this, start, end, iv, difficulty); break;
            case Penguin: iv = new ImageView(new Image("/penguin.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
                m = new Penguin(this, start, end, iv, difficulty); break;
            case Unicorn: iv = new ImageView(new Image("/unicorn.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
                m = new Unicorn(this, start, end, iv, difficulty); break;
        }
        if (m == null)
            return null;
        paneArena.getChildren().add(iv);
        monsters.add(m);
        getGrid(STARTING_COORDINATES).addObject(m);
        System.out.println(String.format("%s:%f generated", type, m.getHealth()));

        return m;
    }

    /**
     * Removes the specified Monster from the arena.
     * @param monster The Monster to be removed.
     */
    public void removeMonster(@NonNull Monster monster)
    {
        paneArena.getChildren().remove(monster.getImageView());
        getGrid(new Coordinates(monster.getX(), monster.getY())).removeObject(monster);
        monsters.remove(monster);
    }

    /**
     * Moves the specified Monster to another location.
     * @param monster The Monster to be moved.
     * @param newCoordinates The coordinates of the new location.
     */
    public void moveMonster(@NonNull Monster monster, @NonNull Coordinates newCoordinates)
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
     * remove laser and explosion that generate a few frames ago.
     */
    private void removeLaser() {
        // remove previous lasers and explosion from arena
        List<Line> toRemove = new ArrayList();
        for(Map.Entry<Line, Integer> entry : lasers.entrySet()) {
            Line key = entry.getKey();
            Integer value = entry.getValue();
            if (value < currentFrame - laserDuration) {
                toRemove.add(key);
            }
        }
        for (Line key : toRemove) {
            lasers.remove(key);
            paneArena.getChildren().remove(key);
        }
        List<ImageView> toRemove2 = new ArrayList();
        for(Map.Entry<ImageView, Integer> entry : explosions.entrySet()) {
            ImageView key = entry.getKey();
            Integer value = entry.getValue();
            if (value < currentFrame - laserDuration) {
                toRemove2.add(key);
            }
        }
        for (ImageView key : toRemove2) {
            lasers.remove(key);
            paneArena.getChildren().remove(key);
        }
    }

    /**
     * tower attack monsters.
     */
    private void attackMonster() {
        // update projectile
        List<Projectile> toRemove3 = new ArrayList();
        for (Projectile p : projectiles) {

            moveProjectile(p);
            // when projectile reach its destination
            if (p.hasReachedTarget()) {
                // find monster in target grid
                Coordinates targetCoordinates = new Coordinates(p.getX(), p.getY());
                LinkedList<ExistsInArena> targets = findObjectsInGrid(targetCoordinates, EnumSet.of(TypeFilter.Monster));
                int attackPower = p.getAttackPower();


                // find the first monster at attack it
                if (p instanceof IceProjectile) {
                    if (targets.size() > 0 && targets.get(0) instanceof Monster) {
                        Monster target = (Monster)targets.get(0);
                        if (target != null) {
                            target.setSpeed(target.getSpeed() - ((IceProjectile) p).getSlowDown());
                            System.out.println(String.format("Ice Tower@(%d,%d) -> %s@(%d,%d)", p.getTower().getX(), p.getTower().getY()
                                    , target.getClassName(), target.getX(), target.getY()));
                        }
                    }
                } else if (p instanceof CatapultProjectile) {
                    LinkedList<ExistsInArena> monsters = findObjectsInRange(targetCoordinates, 25, EnumSet.of(TypeFilter.Monster));
                    for (ExistsInArena monster : monsters) {
                        if (monster instanceof Monster) {
                            ((Monster) monster).setHealth(((Monster) monster).getHealth() - attackPower);
                            System.out.println(String.format("Catapult@(%d,%d) -> %s@(%d,%d)", p.getTower().getX(), p.getTower().getY()
                                    , ((Monster) monster).getClassName(), monster.getX(), monster.getY()));
                        }
                    }
                } else {
                    if (targets.size() > 0 && targets.get(0) instanceof Monster) {
                        Monster target = (Monster)targets.get(0);
                        if (target != null) {
                            target.setHealth(target.getHealth() - attackPower);
                            System.out.println(String.format("Basic Tower@(%d,%d) -> %s@(%d,%d)", p.getTower().getX(), p.getTower().getY()
                                    , target.getClassName(), target.getX(), target.getY()));
                        }
                    }
                }
                toRemove3.add(p);
            }
        }
        // remove projectiles that reach its destination.
        for (Projectile p : toRemove3) {
            removeProjectile(p);
        }

        // towers attack monsters
        for (Tower t : towers) {
            createProjectile(t);
        }

        // turn monster with 0hp to explosion.png
        ArrayList<Monster> monsters = new ArrayList<>();
        for (Monster m : getMonsters()) {
            if (m.getHealth() <= 0) {
                monsters.add(m);
            }
        }
        for (Monster m : monsters) {
            removeMonster(m);
            Coordinates c = new Coordinates(m.getX(), m.getY());
            ImageView explosion = new ImageView(new Image("/collision.png", UIController.GRID_WIDTH
                    , UIController.GRID_WIDTH, true, true));
            c.bindByImage(explosion);
            paneArena.getChildren().add(explosion);
            explosions.put(explosion, currentFrame);
        }

    }

    /**
     * Updates the arena by one frame.
     * @return true if gameover, false otherwise.
     */
    public boolean nextFrame() {
            shadowArena = new Arena(this);

        // Now update currentState
        // TODO:
        if (findObjectsInGrid(END_COORDINATES, EnumSet.of(TypeFilter.Monster)).size() > 0)
            return true;

        removeLaser();
        attackMonster();

//        for (Monster m : currentState.monsters) {
//            Coordinates nextFrame = m.getNextFrame();
//            if (nextFrame != null) {
//                moveMonster(m, nextFrame);
//            }
//        }
        if (currentFrame % WAVE_INTERVAL == 0)
            spawnWave();
        else // this is for testing only
            if (monsters.peek() != null)
                moveMonster(monsters.peek(), Grid.findGridCenter(10, 10));
//        newMonster.recalculateFuturePath();

        currentFrame++;

        if (findObjectsInGrid(END_COORDINATES, EnumSet.of(TypeFilter.Monster)).size() > 0)
            return true;

        return false;
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
        public void moveOneFrame();
    }
}