package project.arena;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import project.Geometry;
import project.Player;
import project.UIController;
import project.arena.monsters.Fox;
import project.arena.monsters.Monster;
import project.arena.monsters.Penguin;
import project.arena.monsters.Unicorn;
import project.arena.projectiles.Projectile;
import project.arena.towers.BasicTower;
import project.arena.towers.Catapult;
import project.arena.towers.IceTower;
import project.arena.towers.LaserTower;
import project.arena.towers.Tower;

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
    static final int STARTING_GRID_X_POS = 0;

    /**
     * y-coordinate of the starting position.
     * @see Coordinates
     */
    static final int STARTING_GRID_Y_POS = 0;

    /**
     * Coordinates of the starting position.
     */
    static final Coordinates STARTING_COORDINATES = Grid.findGridCenter(STARTING_GRID_X_POS, STARTING_GRID_Y_POS);

    /**
     * x-coordinate of the end zone.
     * @see Coordinates
     */
    static final int END_GRID_X_POS = 11;

    /**
     * y-coordinate of the end zone.
     * @see Coordinates
     */
    static final int END_GRID_Y_POS = 0;
    /**
     * Coordinates of the end zone.
     */
    static final Coordinates END_COORDINATES = Grid.findGridCenter(END_GRID_X_POS, END_GRID_Y_POS);

    /**
     * The number of frames between each wave of Monsters.
     * @see #spawnWave()
     */
    static final int WAVE_INTERVAL = 50;

    /**
     * The duration of laser being displayed.
     */
    static final int LASER_DURATION = 2;

    /**
     * The current frame number of the arena since the game began.
     */
    private int currentFrame;

    /**
     * The current difficulty of the arena.
     */
    private double difficulty = 1;


    /**
     * Contains a reference to each laser shot by LaserTower on the arena.
     */
    private HashMap<Line, Integer> lasers = new HashMap<>();

    /**
     * Contains a reference explosion of monster when it died.
     */
    private HashMap<ImageView, Integer> explosions = new HashMap<>();

    /**
     * Contains a reference to each circle shot by Catapult on the arena.
     */
    private HashMap<Circle, Integer> circles = new HashMap<>();

    /**
     * The objects stored in this arena.
     */
    private ArenaObjectStorage arenaObjectStorage = new ArenaObjectStorage(this);

    /**
     * Adds an object to the arena.
     * @param obj The object to add.
     */
    public void addObject(@NonNull ExistsInArena obj) {
        arenaObjectStorage.processAddObject(obj);

        if (obj instanceof Tower) {
            arenaScalarFields.processAddTower((Tower)obj);
        }
    }

    /**
     * Removes an object from the arena.
     * @param obj The object to remove.
     */
    public void removeObject(@NonNull ExistsInArena obj) {
        arenaObjectStorage.processRemoveObject(obj);

        if (obj instanceof Tower) {
            arenaScalarFields.processRemoveTower((Tower)obj);
        }
    }

    /**
     * The scalar fields stored in this arena.
     */
    private ArenaScalarFields arenaScalarFields = new ArenaScalarFields(this);

    /**
     * The Arena during the previous frame. Only used for saving the game.
     */
    private Arena shadowArena;

    /**
     * The player of the game.
     */
    private Player player;

    /**
     * The arena of the game.
     */
    private AnchorPane paneArena;

    /**
     * Constructor of the Arena class. Bind the label to resources.
     * @param resourceLabel the label to show remaining resources of player.
     * @param paneArena the arena pane of the game.
     */
    public Arena(@NonNull Label resourceLabel, @NonNull AnchorPane paneArena) {
        player = new Player("name", 200);
        resourceLabel.textProperty().bind(Bindings.format("Money: %d", player.resourcesProperty()));
        this.paneArena = paneArena;

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

        this.lasers = new HashMap<>();
        for (HashMap.Entry<Line, Integer> entry : other.lasers.entrySet()) {
            this.lasers.put(entry.getKey(), entry.getValue());
        }

        this.explosions = new HashMap<>();
        for (HashMap.Entry<ImageView, Integer> entry : other.explosions.entrySet()) {
            this.explosions.put(entry.getKey(), entry.getValue());
        }

        this.arenaObjectStorage = new ArenaObjectStorage(this, other.arenaObjectStorage);

        this.arenaScalarFields = new ArenaScalarFields(this, other.arenaScalarFields);

        this.shadowArena = null;
    }

    /**
     * Finds all Towers that are in the arena.
     * @return A linked list containing a reference to each Tower in the arena.
     */
    public LinkedList<Tower> getTowers() { return arenaObjectStorage.getTowers(); }

    /**
     * Finds all Projectiles that are in the arena.
     * @return A linked list containing a reference to each Projectile in the arena.
     */
    public LinkedList<Projectile> getProjectiles() { return arenaObjectStorage.getProjectiles(); }

    /**
     * Finds all Monsters that are in the arena.
     * @return A priority queue containing a reference to each Monster in the arena. The first element is closest to the end zone while the last element is furthest.
     */
    public PriorityQueue<Monster> getMonsters() { return arenaObjectStorage.getMonsters(); }

    /**
     * @see ArenaScalarFields#getDistanceToEndZone(Coordinates)
     */
    public int getDistanceToEndZone(@NonNull Coordinates coordinates) {
        return arenaScalarFields.getDistanceToEndZone(coordinates);
    }

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
        return arenaObjectStorage.findObjectsAtPixel(coordinates, filter);
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
        return arenaObjectStorage.findObjectsInRange(coordinates, range, filter);
    }

    /**
     * Finds all objects that are located inside a specified grid.
     * @param xPos The x-position of the grid.
     * @param yPos The y-position of the grid.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public LinkedList<ExistsInArena> findObjectsInGrid(int xPos, int yPos, @NonNull EnumSet<TypeFilter> filter)
    {
        return arenaObjectStorage.findObjectsInGrid(xPos, yPos, filter);
    }

    /**
     * Finds all objects that are located inside the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public LinkedList<ExistsInArena> findObjectsInGrid(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        return arenaObjectStorage.findObjectsInGrid(coordinates, filter);
    }

    /**
     * Finds all objects that occupy a specified coordinate in the arena.
     * @param coordinates The specified coordinates.
     * @return A linked list containing a reference to each object that satisfy the above criteria. Note that they do not have to be located at said coordinate.
     */
    public LinkedList<Object> findObjectsOccupying(@NonNull Coordinates coordinates)
    {
        return arenaObjectStorage.findObjectsOccupying(coordinates);
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
        for (Tower t : getTowers()) {
            int towerX = t.getX();
            int towerY = t.getY();
            int minRange = t.getMinShootingRange();
            int maxRange = t.getMaxShootingRange();

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
        Grid gridToBeBuilt = arenaObjectStorage.getGrid(coordinates);

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
        for (Monster m : getMonsters()) {
            Coordinates c = new Coordinates(m.getX(), m.getY());
            hasMonster.add(arenaObjectStorage.getGrid(c));
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
            addObject(t);
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
        return t.upgrade(player);
    }

    /**
     * Destroys the specified Tower.
     * @param tower The Tower to be destroyed.
     */
    public void destroyTower(@NonNull Tower tower)
    {
        paneArena.getChildren().remove(tower.getImageView());
        removeObject(tower);
    }

    /**
     * Creates a Projectile at a specified pixel.
     * @param t the tower which attack the monster by creating projectile.
     */
    public void createProjectile(@NonNull Tower t)
    {
        // laser tower
        if (t instanceof LaserTower) {
            ((LaserTower) t).generateProjectile();
            Line laserLine = ((LaserTower) t).getLaserLine();
            if (laserLine != null && !lasers.containsKey(laserLine)) {
                lasers.put(laserLine, currentFrame);
                paneArena.getChildren().add(laserLine);
            }

        } else { // other towers
            Projectile p = t.generateProjectile();
            if (p != null) {
                paneArena.getChildren().add(p.getImageView());
                addObject(p);
                arenaObjectStorage.getGrid(new Coordinates(p.getX(), p.getY())).addObject(p);
            }
        }
    }

    /**
     * Removes the specified Projectile from the arena.
     * @param projectile The Projectile to be removed.
     */
    public void removeProjectile(@NonNull Projectile projectile)
    {
        paneArena.getChildren().remove(projectile.getImageView());
        removeObject(projectile);
    }

    /**
     * Moves the specified Projectile to the specified location.
     * Note: Please instead use {@link #objectNextFrame(ExistsInArena)} to update it to the next frame.
     * @param projectile The Projectile to be moved.
     * @param coordinates The coordinates of the new location.
     */
    public void moveProjectile(@NonNull Projectile projectile, @NonNull Coordinates coordinates) {
        arenaObjectStorage.processMoveObject(projectile, coordinates);
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
        addObject(m);
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
        removeObject(monster);
    }

    /**
     * Moves the specified Monster to the specified location.
     * Note: Please instead use {@link #objectNextFrame(ExistsInArena)} to update it to the next frame.
     * @param monster The Monster to be moved.
     * @param coordinates The coordinates of the new location.
     */
    public void moveMonster(@NonNull Monster monster, @NonNull Coordinates coordinates) {
        arenaObjectStorage.processMoveObject(monster, coordinates);
    }

    /**
     * Finds the next location for an object to move in order to reach the end-zone by moving as few pixels as possible.
     * @param coordinates The coordinates of the object.
     * @return The coordinates of the next pixel to move to. If there is no valid path or the pixel is already at the end-zone, returns <code>null</code>.
     */
    public Coordinates findNextTowardsEnd_prioritizeMovement(@NonNull Coordinates coordinates) {
        if (Double.isInfinite(arenaScalarFields.getDistanceToEndZone(coordinates))) return null;
        if (Geometry.isAt(END_COORDINATES.getX(), END_COORDINATES.getY(), coordinates.getX(), coordinates.getY())) return null;

        LinkedList<int[]> neighbours = Grid.findTaxicabNeighbours(coordinates);
        int lowestCost = Integer.MAX_VALUE;
        int[] lowestCostNeighbour = null;
        for (int[] pos : neighbours) {
            int cost = arenaScalarFields.getDistanceToEndZone(pos[0], pos[1]);

            if (cost < lowestCost) {
                lowestCost = cost;
                lowestCostNeighbour = pos;
            }
        }

        int current_xPos = Grid.findGridXPos(coordinates);
        int current_yPos = Grid.findGridYPos(coordinates);
        int neighbour_xPos = lowestCostNeighbour[0];
        int neighbour_yPos = lowestCostNeighbour[1];

        // Move left
        if (neighbour_xPos < current_xPos && neighbour_yPos == current_yPos) {
            return new Coordinates(coordinates.getX() - 1, coordinates.getY());
        }

        // Move right
        if (neighbour_xPos > current_xPos && neighbour_yPos == current_yPos) {
            return new Coordinates(coordinates.getX() + 1, coordinates.getY());
        }

        // Move up
        if (neighbour_xPos == current_xPos && neighbour_yPos < current_yPos) {
            return new Coordinates(coordinates.getX(), coordinates.getY() - 1);
        }

        // Move down
        if (neighbour_xPos == current_xPos && neighbour_yPos > current_yPos) {
            return new Coordinates(coordinates.getX(), coordinates.getY() + 1);
        }

        return null;
    }

    /**
     * Finds the next location for an object to move in order to reach the end-zone with the least attacks received.
     * @param coordinates The coordinates of the object.
     * @return The coordinates of the next pixel to move to. If there is no valid path or the pixel is already at the end-zone, returns <code>null</code>.
     */
    public Coordinates findNextTowardsEnd_prioritizeAttack(@NonNull Coordinates coordinates) {
        if (Double.isInfinite(arenaScalarFields.getDistanceToEndZone(coordinates))) return null;
        if (Geometry.isAt(END_COORDINATES.getX(), END_COORDINATES.getY(), coordinates.getX(), coordinates.getY())) return null;

        LinkedList<Coordinates> neighbours = Coordinates.findTaxicabNeighbours(coordinates);

        double lowestCost = Double.POSITIVE_INFINITY;
        Coordinates lowestCostNeighbour = null;
        for (Coordinates neighbour : neighbours) {
            double cost = arenaScalarFields.getAttacksToEndZone(neighbour);

            if (cost < lowestCost) {
                lowestCost = cost;
                lowestCostNeighbour = neighbour;
            } else if (cost == lowestCost) {
                LinkedList<int[]> grids = Grid.findTaxicabNeighbours(coordinates);
                int lowestCostNeighbourMovementCost = Integer.MAX_VALUE;
                int neighbourMovementCost = Integer.MAX_VALUE;
                for (int[] pos : grids) {
                    int gridCost = arenaScalarFields.getDistanceToEndZone(pos[0], pos[1]);
                    int grid_xPos = pos[0];
                    int grid_yPos = pos[1];
                    int current_xPos = Grid.findGridXPos(coordinates);
                    int current_yPos = Grid.findGridYPos(coordinates);
            
                    // Left
                    if (grid_xPos < current_xPos && grid_yPos == current_yPos) {
                        if (lowestCostNeighbour.getX() < coordinates.getX() && lowestCostNeighbour.getY() == coordinates.getY()) {
                            lowestCostNeighbourMovementCost = gridCost;
                        }
                        if (neighbour.getX() < coordinates.getX() && neighbour.getY() == coordinates.getY()) {
                            neighbourMovementCost = gridCost;
                        }
                    }
            
                    // Right
                    if (grid_xPos > current_xPos && grid_yPos == current_yPos) {
                        if (lowestCostNeighbour.getX() > coordinates.getX() && lowestCostNeighbour.getY() == coordinates.getY()) {
                            lowestCostNeighbourMovementCost = gridCost;
                        }
                        if (neighbour.getX() > coordinates.getX() && neighbour.getY() == coordinates.getY()) {
                            neighbourMovementCost = gridCost;
                        }
                    }
            
                    // Up
                    if (grid_xPos == current_xPos && grid_yPos < current_yPos) {
                        if (lowestCostNeighbour.getX() == coordinates.getX() && lowestCostNeighbour.getY() < coordinates.getY()) {
                            lowestCostNeighbourMovementCost = gridCost;
                        }
                        if (neighbour.getX() == coordinates.getX() && neighbour.getY() < coordinates.getY()) {
                            neighbourMovementCost = gridCost;
                        }
                    }
            
                    // Down
                    if (grid_xPos == current_xPos && grid_yPos > current_yPos) {
                        if (lowestCostNeighbour.getX() == coordinates.getX() && lowestCostNeighbour.getY() > coordinates.getY()) {
                            lowestCostNeighbourMovementCost = gridCost;
                        }
                        if (neighbour.getX() == coordinates.getX() && neighbour.getY() > coordinates.getY()) {
                            neighbourMovementCost = gridCost;
                        }
                    }
                }

                if (neighbourMovementCost < lowestCostNeighbourMovementCost) {
                    lowestCostNeighbour = neighbour;
                }
            }
        }

        return lowestCostNeighbour;
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
            if (value < currentFrame - LASER_DURATION) {
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
            if (value < currentFrame - LASER_DURATION) {
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
        for (Projectile p : getProjectiles()) {

            arenaObjectStorage.processObjectNextFrame(p);
            // when projectile reach its destination
            if (p.hasReachedTarget()) {
//                // find monster in target grid
//                Coordinates targetCoordinates = new Coordinates(p.getX(), p.getY());
//                LinkedList<ExistsInArena> targets = findObjectsInGrid(targetCoordinates, EnumSet.of(TypeFilter.Monster));
//                int attackPower = p.getAttackPower();
//
//
//                // find the first monster at attack it
//                if (p instanceof IceProjectile) {
//                    if (targets.size() > 0 && targets.get(0) instanceof Monster) {
//                        Monster target = (Monster)targets.get(0);
//                        if (target != null) {
//                            target.setSpeed(target.getSpeed() - ((IceProjectile) p).getSlowDown());
//                            System.out.println(String.format("Ice Tower@(%d,%d) -> %s@(%d,%d)", p.getTower().getX(), p.getTower().getY()
//                                    , target.getClassName(), target.getX(), target.getY()));
//                        }
//                    }
//                } else if (p instanceof CatapultProjectile) {
//                    LinkedList<ExistsInArena> monsters = findObjectsInRange(targetCoordinates, 25, EnumSet.of(TypeFilter.Monster));
//                    for (ExistsInArena monster : monsters) {
//                        if (monster instanceof Monster) {
//                            ((Monster) monster).setHealth(((Monster) monster).getHealth() - attackPower);
//                            System.out.println(String.format("Catapult@(%d,%d) -> %s@(%d,%d)", p.getTower().getX(), p.getTower().getY()
//                                    , ((Monster) monster).getClassName(), monster.getX(), monster.getY()));
//                        }
//                    }
//                } else {
//                    if (targets.size() > 0 && targets.get(0) instanceof Monster) {
//                        Monster target = (Monster)targets.get(0);
//                        if (target != null) {
//                            target.setHealth(target.getHealth() - attackPower);
//                            System.out.println(String.format("Basic Tower@(%d,%d) -> %s@(%d,%d)", p.getTower().getX(), p.getTower().getY()
//                                    , target.getClassName(), target.getX(), target.getY()));
//                        }
//                    }
//                }
                toRemove3.add(p);
            }
        }
        // remove projectiles that reach its destination.
        for (Projectile p : toRemove3) {
            removeProjectile(p);
        }

        // towers attack monsters
        for (Tower t : getTowers()) {
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
        for(Map.Entry<Circle, Integer> c : circles.entrySet()) {
            Circle key = c.getKey();
            Integer value = c.getValue();
            if (value == 0) {
                circles.remove(key);
                paneArena.getChildren().remove(key);
            }else c.setValue(0);
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
            if (getMonsters().peek() != null)
                moveMonster(getMonsters().peek(), Grid.findGridCenter(10, 10));

        currentFrame++;

        if (findObjectsInGrid(END_COORDINATES, EnumSet.of(TypeFilter.Monster)).size() > 0)
            return true;

        return false;
    }

    /**
     * Draws a ray from one object to another object, extending towards the edge of the arena.
     * @param source The origin of the ray.
     * @param target The target of the ray.
     * @return The instance of the ray.
     */
    public Line drawRay(@NonNull ExistsInArena source, @NonNull ExistsInArena target) {
        Point2D edgePt = Geometry.intersectBox(source.getX(), source.getY(), target.getX(), target.getY(),
                                                    0, 0, UIController.ARENA_WIDTH, UIController.ARENA_HEIGHT);
        
        Line ray = new Line(source.getX(), source.getY(), edgePt.getX(), edgePt.getY());
        ray.setStroke(javafx.scene.paint.Color.rgb(255, 255, 0));
        ray.setStrokeWidth(3);
        return ray;
    }

    /**
     * Draws a ray from one point to another point, extending towards the edge of the arena.
     * @param source The origin of the ray.
     * @param target The target of the ray.
     * @return The instance of the ray.
     */
    public Line drawRay(@NonNull Coordinates source, @NonNull Coordinates target) {
        Point2D edgePt = Geometry.intersectBox(source.getX(), source.getY(), target.getX(), target.getY(),
                                                    0, 0, UIController.ARENA_WIDTH, UIController.ARENA_HEIGHT);
        
        Line ray = new Line(source.getX(), source.getY(), edgePt.getX(), edgePt.getY());
        ray.setStroke(javafx.scene.paint.Color.rgb(255, 255, 0));
        ray.setStrokeWidth(3);
        return ray;
    }

    public void drawCircle(@NonNull Coordinates source, @NonNull int damageRange){
        Circle circle = new Circle();
        circle.setCenterX(source.getX());
        circle.setCenterY(source.getY());
        circle.setRadius(damageRange);
        circle.setFill(Color.rgb(100,0,0));
        paneArena.getChildren().add(circle);
        circles.put(circle,1);
    }
}