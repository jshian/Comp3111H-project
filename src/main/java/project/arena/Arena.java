package project.arena;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.Node;
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
 * {@link Monster}s spawn at the starting position and try to reach the end-zone.
 * {@link Tower}s reside on the arena and fire {@link Projectile}s at enemies in range.
 */
@Entity
public final class Arena {

    /**
     * x-position of the {@link Grid} representing the starting position.
     */
    static final short STARTING_GRID_X_POS = 0;

    /**
     * y-position of the {@link Grid} representing the starting position.
     */
    static final short STARTING_GRID_Y_POS = 0;

    /**
     * Coordinates of the center of the {@link Grid} representing the starting position.
     */
    static final Coordinates STARTING_COORDINATES = Grid.findGridCenter(STARTING_GRID_X_POS, STARTING_GRID_Y_POS);

    /**
     * x-position of the {@link Grid} representing the end-zone.
     */
    static final short END_GRID_X_POS = 11;

    /**
     * y-position of the {@link Grid} representing the end-zone.
     */
    static final short END_GRID_Y_POS = 0;

    /**
     * Coordinates of the center of the {@link Grid} representing the end-zone.
     */
    static final Coordinates END_COORDINATES = Grid.findGridCenter(END_GRID_X_POS, END_GRID_Y_POS);

    /**
     * The number of frames between each wave of {@link Monster}s.
     * @see #spawnWave()
     */
    static final int WAVE_INTERVAL = 50;

    /**
     * The duration of laser of {@link LaserTower} being displayed.
     */
    static final int LASER_DURATION = 2;

    /**
     * The player of the game.
     */
    @NotNull
    @OneToOne
    private Player player;

    /**
     * The arena of the game.
     */
    @Transient
    private AnchorPane paneArena;

    /**
     * The current frame number of the arena since the game began.
     */
    private int currentFrame = 0;

    /**
     * The current difficulty of the arena.
     */
    private double difficulty = 1;

    /**
     * Contains a reference to line, circle, and monster explosion on the arena.
     */
    private HashMap<Node, Integer> toRemove = new HashMap<>();

    /**
     * make a deep copy of another HashMap.
     * @param other the HashMap to be copied.
     * @return the deep copied HashMap.
     */
    private HashMap<Node, Integer> copyToRemove(HashMap<Node, Integer> other) {
        HashMap<Node, Integer> newHashMap = new HashMap<>();
        for (HashMap.Entry<Node, Integer> entry : other.entrySet()) {
            Node key = entry.getKey();
            if (key instanceof Line) {
                Line old = (Line) key;
                Line n = new Line(old.getStartX(), old.getStartY(), old.getEndX(), old.getEndY());
                n.setStroke(old.getStroke());
                n.setStrokeWidth(old.getStrokeWidth());
                newHashMap.put(n, entry.getValue());
            } else if (key instanceof Circle) {
                Circle old = (Circle) key;
                Circle n = new Circle();
                n.setCenterX(old.getCenterX());
                n.setCenterY(old.getCenterY());
                n.setRadius(old.getRadius());
                n.setFill(old.getFill());
                newHashMap.put(n, entry.getValue());
            } else if (key instanceof ImageView) {
                ImageView old = (ImageView) key;
                ImageView n = new ImageView(old.getImage());
                n.setX(old.getX());
                n.setY(old.getY());
                newHashMap.put(n, entry.getValue());
            }
        }
        return newHashMap;
    }

    /**
     * The objects stored in this arena.
     */
    @NotNull
    @OneToOne
    private ArenaObjectStorage arenaObjectStorage;

    /**
     * Adds an object to the arena.
     * @param obj The object to add.
     */
    public void addObject(@NonNull ExistsInArena obj) {
        paneArena.getChildren().add(obj.getImageView());
        arenaObjectStorage.processAddObject(obj);

        if (obj instanceof Tower) {
            arenaScalarFields.processAddTower((Tower)obj, false);
        }
    }

    /**
     * Removes an object from the arena.
     * @param obj The object to remove.
     */
    public void removeObject(@NonNull ExistsInArena obj) {
        paneArena.getChildren().remove(obj.getImageView());
        arenaObjectStorage.processRemoveObject(obj);

        if (obj instanceof Tower) {
            arenaScalarFields.processRemoveTower((Tower)obj, false);
            player.receiveResources(((Tower)obj).getBuildingCost() / 2);
        }
        if (obj instanceof Monster) {
            player.receiveResources(((Monster)obj).getResources());
        }
    }

    /**
     * Moves an object to another location in the arena.
     * @param obj The object to move.
     * @param newCoordinates The coordinates of the new location.
     * @throws IllegalArgumentException The object type is not recognized.
     */
    private void moveObject(@NonNull ExistsInArena obj, @NonNull Coordinates newCoordinates)
    {
        arenaObjectStorage.processMoveObject(obj, newCoordinates);
    }

    /**
     * Updates the object to the next frame and updates the Arena accordingly.
     * @param obj The object to update.
     * @return the object that has been marked as pending { add, removal }.
     */
    private ExistsInArena[] objectNextFrame(@NonNull ExistsInArena obj) {
        return arenaObjectStorage.processObjectNextFrame(obj);
    }

    /**
     * The scalar fields stored in this arena.
     */
    @Transient
    private ArenaScalarFields arenaScalarFields;

    /**
     * The Arena during the previous frame. Only used for saving the game.
     */
    @OneToOne
    private Arena shadowArena;

    /**
     * Constructor of the Arena class. Bind the label to resources.
     * @param resourceLabel the label to show remaining resources of player.
     * @param paneArena the arena pane of the game.
     */
    public Arena(@NonNull Label resourceLabel, @NonNull AnchorPane paneArena) {
        player = new Player("name", 200);
        resourceLabel.textProperty().bind(Bindings.format("Money: %d", player.resourcesProperty()));
        this.paneArena = paneArena;

        arenaObjectStorage = new ArenaObjectStorage(this);
        arenaScalarFields = new ArenaScalarFields(this);

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

        this.toRemove = copyToRemove(other.toRemove);

        // arenaScalarFields must be set up before arenaObjectStorage because Monster ordering depends on it
        this.arenaScalarFields = new ArenaScalarFields(this, other.arenaScalarFields);
        this.arenaObjectStorage = new ArenaObjectStorage(this, other.arenaObjectStorage);

        // Deep copy paneArena without initial grids (that should be handled by UIController)
        // Must be done after arenaObjectStorage so that the new images of the objects are fetched.
        this.paneArena = new AnchorPane();
        for (Monster m : getMonsters()) {
            paneArena.getChildren().add(m.getImageView());
        }
        for (Projectile p : getProjectiles()) {
            paneArena.getChildren().add(p.getImageView());
        }
        for (Tower t : getTowers()) {
            paneArena.getChildren().add(t.getImageView());
        }
        for (HashMap.Entry<Node, Integer> entry : toRemove.entrySet()) {
            paneArena.getChildren().add(entry.getKey());
        }

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
     * Find the player who are playing now.
     * @return The player who are playing.
     */
    public Player getPlayer() { return player; }

    /**
     * Find the pane of the arena
     * @return the pane of the arena
     */
    public AnchorPane getPane() { return paneArena; }

    /**
     * @see ArenaScalarFields#getDistanceToEndZone(Coordinates)
     */
    public short getDistanceToEndZone(@NonNull Coordinates coordinates) {
        return arenaScalarFields.getDistanceToEndZone(coordinates);
    }

    /**
     * An enum for filtering objects in the Arena according to type.
     */
    public enum TypeFilter {
        /**
         * {@link Tower} objects are selected.
         */
        Tower,

        /**
         * {@link Projectile} objects are selected.
         */
        Projectile,

        /**
         * {@link Monster} objects are selected.
         */
        Monster }

    /**
     * An enum for generate monster in the Arena according to type.
     */
    public static enum MonsterType {
        /**
         * The {@link Fox}.
         */
        Fox,

        /**
         * The {@link Penguin}.
         */
        Penguin,

        /**
         * The {@link Unicorn}.
         */
        Unicorn
    }

    /**
     * An enum for building towers in the Arena according to type.
     */
    public static enum TowerType {
        /**
         * The {@link BasicTower}.
         */
        BasicTower,

        /**
         * The {@link Catapult}.
         */
        Catapult,

        /**
         * The {@link IceTower}.
         */
        IceTower,

        /**
         * The {@link LaserTower}.
         */
        LaserTower
    }

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
     * @return A linked list containing a reference to each object that satisfies the above criteria. If only {@link Monster}s are included, they will be sorted by path length to the end-zone.
     * @see TypeFilter
     */
    public LinkedList<ExistsInArena> findObjectsInRange(@NonNull Coordinates coordinates, double range, @NonNull EnumSet<TypeFilter> filter)
    {
        return arenaObjectStorage.findObjectsInRange(coordinates, range, filter);
    }

    /**
     * Finds all objects that are located inside the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria. If only {@link Monsters} are included, they will be sorted by path length to the end-zone.
     * @see TypeFilter
     */
    public LinkedList<ExistsInArena> findObjectsInGrid(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        return arenaObjectStorage.findObjectsInGrid(coordinates, filter);
    }

    /**
     * check if the player has enough resources to build the tower.
     * @param type type of the tower.
     * @return true if the player has enough resources or false otherwise.
     */
    public boolean hasResources(@NonNull TowerType type)
    {
        int cost = 500;
        Coordinates c = new Coordinates((short) 0, (short) 0);
        switch(type) {
            case BasicTower: cost = new BasicTower(this, c).getBuildingCost(); break;
            case IceTower: cost = new IceTower(this, c).getBuildingCost(); break;
            case Catapult: cost = new Catapult(this, c).getBuildingCost(); break;
            case LaserTower: cost = new LaserTower(this, c).getBuildingCost(); break;
        }
        return player.hasResources(cost);
    }

    /**
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param type type of the tower.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     */
    public boolean canBuildTower(@NonNull Coordinates coordinates, @NonNull TowerType type)
    {
        boolean empty = findObjectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower, TypeFilter.Monster)).isEmpty();
        if (!empty) return false;

        short gridX = Grid.findGridCenterX(coordinates);
        short gridY = Grid.findGridCenterY(coordinates);
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
        for (short i = 0; i < noTower.length; i++) {
            for (short j = 0; j < noTower[0].length; j++) {
                noTower[i][j] = findObjectsInGrid(Grid.findGridCenter(i,j), EnumSet.of(TypeFilter.Tower)).isEmpty();
                visited[i][j] = false;
            }
        }
        noTower[gridToBeBuilt.getXPos()][gridToBeBuilt.getYPos()] = false;
        gridDFS(noTower, visited, Grid.findGridXPos(END_COORDINATES), Grid.findGridYPos(END_COORDINATES));

        ArrayList<Grid> hasMonster = new ArrayList<>();
        hasMonster.add(new Grid((short) 0,(short) 0));
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
    private void gridDFS(@NonNull boolean[][] noTower, @NonNull boolean[][] visited, short x, short y)
    {
        if (x < 0 || y < 0 || x >= noTower.length || y >= noTower[0].length)
            return;
        if (visited[x][y] == true || noTower[x][y] == false)
            return;
        visited[x][y] = true;

        gridDFS(noTower, visited, (short) (x+1), y);
        gridDFS(noTower, visited, (short) (x-1), y);
        gridDFS(noTower, visited, x, (short) (y+1));
        gridDFS(noTower, visited, x, (short) (y-1));
    }

    /**
     * Builds a Tower at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param type specify the class of tower.
     * @return the tower being built, or null if not enough resources
     */
    public Tower buildTower(@NonNull Coordinates coordinates, @NonNull TowerType type)
    {
        Tower t = null;
        int cost = 0;
        Coordinates center = Grid.findGridCenter(coordinates);
        switch(type) {
            case BasicTower: t = new BasicTower(this, center); break;
            case IceTower: t = new IceTower(this, center); break;
            case Catapult: t = new Catapult(this, center); break;
            case LaserTower: t = new LaserTower(this, center); break;
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
    }

    /**
     * Upgrade the tower at the grid where a specified pixel is located.
     * @param t the tower to be upgrade
     * @return true if upgrade is successful, false if player don't have enough resources.
     */
    public boolean upgradeTower(@NonNull Tower t) {
        if (t.canUpgrade(player)) {
            arenaScalarFields.processRemoveTower(t, true);
            t.tryUpgrade(player);
            arenaScalarFields.processAddTower(t, false);

            return true;
        }

        return false;
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

        // Create some randomness of spawn location
        short startX = (short) (STARTING_COORDINATES.getX() + Math.random() * UIController.GRID_WIDTH / 2);
        if (startX < 0) startX = 0;
        if (startX >= UIController.ARENA_WIDTH) startX = UIController.ARENA_WIDTH;
        short startY = (short) (STARTING_COORDINATES.getY() + Math.random() * UIController.GRID_HEIGHT / 2);
        if (startY < 0) startY = 0;
        if (startY >= UIController.ARENA_HEIGHT) startY = UIController.ARENA_HEIGHT;
        Coordinates start = new Coordinates(startX, startY);

        // The end zone is always the same
        Coordinates end = Grid.findGridCenter(END_COORDINATES);

        switch(type) {
            case Fox: m = new Fox(this, start, end, difficulty); break;
            case Penguin: m = new Penguin(this, start, end, difficulty); break;
            case Unicorn: m = new Unicorn(this, start, end, difficulty); break;
        }
        if (m == null) return null;
            
        addObject(m);
        System.out.println(String.format("%s:%.2f generated", type, m.getHealth()));

        return m;
    }

    /**
     * Finds the next location for an object to move in order to reach the end-zone by moving as few pixels as possible.
     * @param coordinates The coordinates of the object.
     * @return The coordinates of the next pixel to move to. If there is no valid path or the pixel is already at the end-zone, returns <code>null</code>.
     */
    public Coordinates findNextTowardsEnd_prioritizeMovement(@NonNull Coordinates coordinates) {
        if (Double.isInfinite(arenaScalarFields.getDistanceToEndZone(coordinates))) return null;
        if (Geometry.isAt(END_COORDINATES.getX(), END_COORDINATES.getY(), coordinates.getX(), coordinates.getY())) return null;

        LinkedList<Coordinates> neighbours = Coordinates.findTaxicabNeighbours(coordinates);

        short lowestCost = Short.MAX_VALUE;
        Coordinates lowestCostNeighbour = null;
        for (Coordinates neighbour : neighbours) {
            short cost = arenaScalarFields.getDistanceToEndZone(neighbour);

            if (cost < lowestCost) {
                lowestCost = cost;
                lowestCostNeighbour = neighbour;
            }
        }

        return lowestCostNeighbour;
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
            }
        }

        return lowestCostNeighbour;
    }

    /**
     * remove laser, circle and explosion that generate a few frames ago.
     */
    private void remove() {
        List<Node> list = new ArrayList<>();
        for(Map.Entry<Node, Integer> entry : toRemove.entrySet()) {
            Node key = entry.getKey();
            Integer value = entry.getValue();
            if (value > 0 ) {
                entry.setValue(--value);
            }else {
                list.add(key);
            }
        }
        for(Node n:list){
            toRemove.remove(n);
            paneArena.getChildren().remove(n);
        }
    }

    /**
     * process next frame for all objects in the arena.
     */
    private void nextFrameForAllObjects() {
        ArrayList<ExistsInArena> objectsToBeAdded = new ArrayList<>();
        ArrayList<ExistsInArena> objectsToBeRemoved = new ArrayList<>();

        // update projectile
        for (Projectile p : getProjectiles()) {
            ExistsInArena toRemove = objectNextFrame(p)[1];
            if (toRemove != null) {
                objectsToBeRemoved.add(toRemove);
            }
        }

        // towers attack monsters
        for (Tower t : getTowers()) {
            ExistsInArena toAdd = objectNextFrame(t)[0];
            if (toAdd != null) {
                objectsToBeAdded.add(toAdd);
            }
        }

        // update monsters
        for (Monster m : getMonsters()) {
            ExistsInArena toRemove = objectNextFrame(m)[1];
            if (toRemove != null) {
                objectsToBeRemoved.add(toRemove);
            }
        }

        // remove objects
        for (ExistsInArena e : objectsToBeRemoved) {
            if (e instanceof Projectile) {
                removeObject(e);
            } else if (e instanceof Monster) { // turn dead monster to explosion
                removeObject(e);
                Coordinates c = new Coordinates(e.getX(), e.getY());
                ImageView explosion = new ImageView(new Image("/collision.png", UIController.GRID_WIDTH
                        , UIController.GRID_WIDTH, true, true));
                c.bindByImage(explosion);
                paneArena.getChildren().add(explosion);
                toRemove.put(explosion, LASER_DURATION);
            }
        }

        // add objects
        for (ExistsInArena e : objectsToBeAdded) {
            if (e instanceof Projectile) {
                addObject(e);
            }
        }
    }

    /**
     * Updates the arena by one frame.
     * @return true if game over, false otherwise.
     */
    public boolean nextFrame() {
        shadowArena = new Arena(this);

        if (findObjectsInGrid(END_COORDINATES, EnumSet.of(TypeFilter.Monster)).size() > 0) {
            System.out.println("Gameover");
            return true;
        }


        // Now update currentState
        remove();
        nextFrameForAllObjects();
        if (currentFrame % WAVE_INTERVAL == 0)
            spawnWave();
        currentFrame++;


        if (findObjectsInGrid(END_COORDINATES, EnumSet.of(TypeFilter.Monster)).size() > 0) {
            System.out.println("Gameover");
            return true;
        }
        return false;
    }

    /**
     * Draws a ray from one object to another object, extending towards the edge of the arena.
     * @param source The origin of the ray.
     * @param target The target of the ray.
     */
    public void drawRay(@NonNull ExistsInArena source, @NonNull ExistsInArena target) {
        Point2D edgePt = Geometry.intersectBox(source.getX(), source.getY(), target.getX(), target.getY(),
                                                    0, 0, UIController.ARENA_WIDTH, UIController.ARENA_HEIGHT);
        
        Line ray = new Line(source.getX(), source.getY(), edgePt.getX(), edgePt.getY());
        ray.setStroke(javafx.scene.paint.Color.rgb(255, 255, 0));
        ray.setStrokeWidth(3);
        toRemove.put(ray, LASER_DURATION);
        paneArena.getChildren().add(ray);
    }

    /**
     * Draws a ray from one point to another point, extending towards the edge of the arena.
     * @param source The origin of the ray.
     * @param target The target of the ray.
     */
    public void drawRay(@NonNull Coordinates source, @NonNull Coordinates target) {
        Point2D edgePt = Geometry.intersectBox(source.getX(), source.getY(), target.getX(), target.getY(),
                                                    0, 0, UIController.ARENA_WIDTH, UIController.ARENA_HEIGHT);
        
        Line ray = new Line(source.getX(), source.getY(), edgePt.getX(), edgePt.getY());
        ray.setStroke(javafx.scene.paint.Color.rgb(255, 255, 0));
        ray.setStrokeWidth(3);
        toRemove.put(ray, LASER_DURATION);
        paneArena.getChildren().add(ray);
    }

    /**
     * Draw a circle in a specific center and radius.
     * @param source The center coordinates og the circle.
     * @param damageRange The radius of the circle.
     */
    public void drawCircle(@NonNull Coordinates source, @NonNull short damageRange){
        Circle circle = new Circle();
        circle.setCenterX(source.getX());
        circle.setCenterY(source.getY());
        circle.setRadius(damageRange);
        circle.setFill(Color.rgb(255, 255, 0));
        paneArena.getChildren().add(circle);
        toRemove.put(circle,1);
    }
}