package project;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import project.monsters.*;
import project.projectiles.CatapultProjectile;
import project.projectiles.IceProjectile;
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
     * The duration of laser being displayed.
     */
    private static final int laserDuration = 4;

    /**
     * Describes the state of the Arena during a frame.
     */
    private static class ArenaState {
        /**
         * The current frame number of the Arena since the game began.
         */
        private int currentFrame = 0;

        /**
         * The current difficulty of the Arena.
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
                    Coordinates otherGridCoordinates = Grid.findGridCenter(grids[i][j].getXPos(), grids[i][j].getYPos());
                    if (coordinates.diagonalDistanceFrom(otherGridCoordinates)
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
    	private double[][] movementCostToEnd = new double[UIController.ARENA_WIDTH][UIController.ARENA_HEIGHT];
    	
    	/**
    	 * Stores the cost for a monster in each pixel to reach the end-zone due to being attacked. Indices correspond to the x- and y- coordinates.
    	 * The cost is in terms of per unit speed of the monster.
    	 * @see Monster
    	 * @see Coordinates
    	 */
        private double[][] attackCostToEnd = new double[UIController.ARENA_WIDTH][UIController.ARENA_HEIGHT];

        private ArenaState() {
            for (int i = 0; i < UIController.MAX_H_NUM_GRID; i++)
                for (int j = 0; j < UIController.MAX_V_NUM_GRID; j++)
                    grids[i][j] = new Grid(i, j);
        }
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
     * @param paneArena the arena pane of the game.
     */
    public Arena(@NonNull Label resourceLabel, @NonNull AnchorPane paneArena) {
        player = new Player("name", 200);
        resourceLabel.textProperty().bind(Bindings.format("Money: %d", player.resourcesProperty()));
        this.paneArena = paneArena;
    }

    /**
     * An enum for filtering objects in the Arena according to type.
     * @see Tower
     * @see Projectile
     * @see Monster
     */
    public static enum TypeFilter { Tower, Projectile, Monster }

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
    public static LinkedList<ExistsInArena> objectsAtPixel(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();

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
    public static LinkedList<ExistsInArena> objectsInRange(@NonNull Coordinates coordinates, double range, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();

        LinkedList<Grid> grids = currentState.getPotentialGridsInRange(coordinates, range);

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

        return result;
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
        if(gridCoordinates.isAt(Grid.findGridCenter(STARTING_COORDINATES)) || gridCoordinates.isAt(Grid.findGridCenter(END_COORDINATES))
            || !hasResources(type) || !hasRoute(coordinates))
            return false;
        return true;
    }

    /**
     * Determines whether some monster cannot go to end-zone after building a tower.
     * @param coordinates The coordinates of tower to be built.
     * @return true if all monster can go to end-zone after building a tower, false otherwise.
     */
    private static boolean hasRoute(@NonNull Coordinates coordinates) {
        Grid gridToBeBuilt = currentState.getGrid(coordinates);

        boolean[][] noTower = new boolean[UIController.MAX_H_NUM_GRID][UIController.MAX_H_NUM_GRID];
        boolean[][] visited = new boolean[UIController.MAX_H_NUM_GRID][UIController.MAX_H_NUM_GRID];
        for (int i = 0; i < noTower.length; i++) {
            for (int j = 0; j < noTower[0].length; j++) {
                noTower[i][j] = objectsInGrid(Grid.findGridCenter(i,j), EnumSet.of(TypeFilter.Tower)).isEmpty();
                visited[i][j] = false;
            }
        }
        noTower[gridToBeBuilt.getXPos()][gridToBeBuilt.getYPos()] = false;
        gridDFS(noTower, visited, Grid.findGridXPos(END_COORDINATES), Grid.findGridYPos(END_COORDINATES));

        ArrayList<Grid> hasMonster = new ArrayList<>();
        hasMonster.add(new Grid(0,0));
        for (Monster m : currentState.monsters) {
            Coordinates c = new Coordinates(m.getX(), m.getY());
            hasMonster.add(currentState.getGrid(c));
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
    private static void gridDFS(@NonNull boolean[][] noTower, @NonNull boolean[][] visited, int x, int y)
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
    public static Tower buildTower(@NonNull Coordinates coordinates, @NonNull ImageView iv, @NonNull String type)
    {
        Tower t = null;
        int cost = 0;
        Coordinates center = Grid.findGridCenter(coordinates);
        switch(type) {
            case "Basic Tower": t = new BasicTower(center, iv); break;
            case "Ice Tower": t = new IceTower(center, iv); break;
            case "Catapult": t = new Catapult(center, iv); break;
            case "Laser Tower": t = new LaserTower(center, iv); break;
            default: return null;
        }
        cost = t.getBuildingCost();

        if (player.hasResources(cost)) {
            player.spendResources(cost);
            currentState.towers.add(t);
            currentState.getGrid(coordinates).addObject(t);
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
     */
    public static void destroyTower(@NonNull Tower tower)
    {
        paneArena.getChildren().remove(tower.getImageView());
        currentState.getGrid(new Coordinates(tower.getX(), tower.getY())).removeObject(tower);
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
     */
    public static void removeProjectile(@NonNull Projectile projectile)
    {
        paneArena.getChildren().remove(projectile.getImageView());
        currentState.getGrid(new Coordinates(projectile.getX(), projectile.getY())).removeObject(projectile);
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
            if (randomNumber < 1/3.0)
                generateMonster(MonsterType.Fox);
            else if (randomNumber < 2/3.0)
                generateMonster(MonsterType.Penguin);
            else
                generateMonster(MonsterType.Unicorn);
        }

        currentState.difficulty += 1;
    }

    /**
     * Generate a monster to the arena.
     * @param type specify the type of the monster.
     * @return the monster being generated.
     */
    public static Monster generateMonster(@NonNull MonsterType type)
    {
        Monster m = null;
        ImageView iv = null;
        // we need to update c when monster move so create a new Coordinate instead.
        Coordinates c = Grid.findGridCenter(STARTING_POSITION_X, STARTING_POSITION_Y);
        switch(type) {
            case Fox: iv = new ImageView(new Image("/fox.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
                m = new Fox(currentState.difficulty, c, END_COORDINATES, iv); break;
            case Penguin: iv = new ImageView(new Image("/penguin.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
                m = new Penguin(currentState.difficulty, c, END_COORDINATES, iv); break;
            case Unicorn: iv = new ImageView(new Image("/unicorn.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
                m = new Unicorn(currentState.difficulty, c, END_COORDINATES, iv); break;
        }
        if (m == null)
            return null;
        paneArena.getChildren().add(iv);
        currentState.monsters.add(m);
        currentState.getGrid(STARTING_COORDINATES).addObject(m);
        System.out.println(String.format("%s:%f generated", type, m.getHealth()));

        return m;
    }

    /**
     * Removes the specified Monster from the arena.
     * @param monster The Monster to be removed.
     */
    public static void removeMonster(@NonNull Monster monster)
    {
        paneArena.getChildren().remove(monster.getImageView());
        currentState.getGrid(new Coordinates(monster.getX(), monster.getY())).removeObject(monster);
        currentState.monsters.remove(monster);
    }

    /**
     * Moves the specified Monster to another location.
     * @param monster The Monster to be moved.
     * @param newCoordinates The coordinates of the new location.
     */
    public static void moveMonster(@NonNull Monster monster, @NonNull Coordinates newCoordinates)
    {
        currentState.getGrid(new Coordinates(monster.getX(), monster.getY())).removeObject(monster);

        int newX = newCoordinates.getX();
        int newY = newCoordinates.getY();
        monster.setLocation(newX, newY);

        currentState.getGrid(newCoordinates).addObject(monster);
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
     * remove laser and explosion that generate a few frames ago.
     */
    private static void removeLaser() {
        // remove previous lasers and explosion from arena
        List<Line> toRemove = new ArrayList();
        for(Map.Entry<Line, Integer> entry : currentState.lasers.entrySet()) {
            Line key = entry.getKey();
            Integer value = entry.getValue();
            if (value < currentState.currentFrame - laserDuration) {
                toRemove.add(key);
            }
        }
        for (Line key : toRemove) {
            currentState.lasers.remove(key);
            paneArena.getChildren().remove(key);
        }
        List<ImageView> toRemove2 = new ArrayList();
        for(Map.Entry<ImageView, Integer> entry : currentState.explosions.entrySet()) {
            ImageView key = entry.getKey();
            Integer value = entry.getValue();
            if (value < currentState.currentFrame - laserDuration) {
                toRemove2.add(key);
            }
        }
        for (ImageView key : toRemove2) {
            currentState.lasers.remove(key);
            paneArena.getChildren().remove(key);
        }
    }

    /**
     * tower attack monsters.
     */
    private static void attackMonster() {
        // projectile
        List<Projectile> toRemove3 = new ArrayList();
        for (Projectile p : getProjectiles()) {
            p.moveOneFrame();
            // has monster being attacked.
            if (p.hasReachedTarget()) {
                // find monster in target grid
                Coordinates targetCoordinates = new Coordinates(p.getX(), p.getY());
                LinkedList<ExistsInArena> targets = objectsInGrid(targetCoordinates, EnumSet.of(TypeFilter.Monster));
                int attackPower = p.getAttackPower();


                // find the first monster at attack it
                if (p instanceof IceProjectile) {
                    if (targets != null && targets.size() > 0) {
                        Monster target = (Monster)targets.get(0);
                        if (target != null) {
                            target.setSpeed(target.getSpeed() - ((IceProjectile) p).getSlowDown());
                            System.out.println(String.format("Ice Tower@(%d,%d) -> %s@(%d,%d)", p.getTower().getX(), p.getTower().getY()
                                    , target.getClassName(), target.getX(), target.getY()));
                        }
                    }
                } else if (p instanceof CatapultProjectile) {
                    LinkedList<ExistsInArena> monsters = objectsInRange(targetCoordinates, 25, EnumSet.of(TypeFilter.Monster));
                    for (ExistsInArena monster : monsters) {
                        ((Monster) monster).setHealth(((Monster) monster).getHealth() - attackPower);
                        System.out.println(String.format("Catapult@(%d,%d) -> %s@(%d,%d)", p.getTower().getX(), p.getTower().getY()
                                , ((Monster) monster).getClassName(), monster.getX(), monster.getY()));
                    }
                } else {
                    if (targets != null && targets.size() > 0) {
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
        // remove projectiles from arena
        for (Projectile p : toRemove3) {
            currentState.projectiles.remove(p);
            paneArena.getChildren().remove(p.getImageView());
        }

        // towers attack monsters
        for (Tower t : getTowers()) {
            // laser tower
            if (t instanceof LaserTower) {
                ((LaserTower) t).attackMonster();
                Line laserLine = ((LaserTower) t).getLaserLine();
                if (laserLine != null && !currentState.lasers.containsKey(laserLine)) {
                    currentState.lasers.put(laserLine, currentState.currentFrame);
                    paneArena.getChildren().add(laserLine);
                }

            } else { // other towers
                Projectile p = t.attackMonster();
                if (p != null) {
                    paneArena.getChildren().add(p.getImageView());
                    currentState.projectiles.add(p);
                }
            }
        }

        // turn monster with 0 hp to explosion.png
        for (Monster m : getMonsters()) {
            if (m.getHealth() <= 0) {
                removeMonster(m);
                Coordinates c = new Coordinates(m.getX(), m.getY());
                ImageView explosion = new ImageView(new Image("/collision.png", UIController.GRID_WIDTH
                    , UIController.GRID_WIDTH, true, true));
                c.bindByImage(explosion);
                paneArena.getChildren().add(explosion);
                currentState.explosions.put(explosion, currentState.currentFrame);
            }
        }


    }

    /**
     * Updates the arena by one frame.
     * @return true if gameover, false otherwise.
     */
    public static boolean nextFrame() {
        shadowState = currentState;

        // Now update currentState
        // TODO: all.
        removeLaser();
        attackMonster();

//        for (Monster m : currentState.monsters) {
//            Coordinates nextFrame = m.getNextFrame();
//            if (nextFrame != null) {
//                moveMonster(m, nextFrame);
//            }
//        }
        if (currentState.currentFrame % WAVE_INTERVAL == 0)
            spawnWave();
        else // this is for testing only
            if (currentState.monsters.peek() != null)
                moveMonster(currentState.monsters.peek(), Grid.findGridCenter(10, 10));
//        newMonster.recalculateFuturePath();

        currentState.currentFrame++;

        if (objectsInGrid(END_COORDINATES, EnumSet.of(TypeFilter.Monster)).size() > 0)
            return true;

        return false;
    }

    /**
     * Finds all Towers that are in the arena.
     * @return A linked list containing a reference to each Tower in the Arena.
     */
    public static LinkedList<Tower> getTowers() { return currentState.towers; }

    /**
     * Finds all Monsters that are in the arena.
     * @return A priority queue containing a reference to each Monster in the Arena. The first element is closest to the end zone while the last element is furthest.
     */
    public static PriorityQueue<Monster> getMonsters() { return currentState.monsters; }

    /**
     * Finds all Projectile that are in the arena.
     * @return A linked list containing a reference to each Projectile in the Arena.
     */
    public static LinkedList<Projectile> getProjectiles() { return currentState.projectiles; }

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
        public void moveOneFrame();
    }
}