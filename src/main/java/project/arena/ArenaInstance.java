package project.arena;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Random;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import javafx.scene.image.Image;

import project.Player;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.ArenaObjectFactory;
import project.entity.Monster;
import project.entity.ArenaObjectFactory.MonsterType;
import project.event.EventHandler;
import project.event.EventManager;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.EventArgs;
import project.query.ArenaObjectGridSelector;
import project.query.ArenaObjectPropertySelector;
import project.query.ArenaObjectRectangleSelector;
import project.query.ArenaObjectStorage;
import project.query.ArenaObjectStorage.StoredType;

/**
 * The area where most of the action takes place in the game.
 * {@link Monster}s spawn at the starting position and try to reach the end-zone.
 * {@link Tower}s reside on the arena and fire {@link Projectile}s at enemies in range.
 */
@Entity
public final class ArenaInstance {

    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The player attached to the arena.
     */
    @NotNull
    @OneToOne
    private Player player;

    /**
     * The register of {@link EventManager}s attached to the arena.
     */
    @Transient
    private ArenaEventRegister eventRegister;

    /**
     * The register of (@link ArenaScalarField}s attached to the arena.
     */
    @Transient
    private ArenaScalarFieldRegister scalarFieldRegister;

    /**
     * The storage of {@link ArenaObject}s attached to this arena.
     */
    @NotNull
    @OneToOne
    private ArenaObjectStorage storage;

    /**
     * The current frame number of the arena since the game began.
     */
    private int currentFrame = 0;

    /**
     * The current difficulty of the arena.
     */
    private double difficulty = 1;

    /**
     * The method invoked when an {@link ArenaObject} is being added.
     */
    @Transient
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        ArenaManager.getActiveUIController().addToPane(subject.getImageView());
    };

    /**
     * The method invoked when an {@link ArenaObject} is being removed.
     */
    @Transient
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        ArenaManager.getActiveUIController().removeFromPane(subject.getImageView());

        // Draw monster explosion
        if (subject instanceof Monster) {
            Image img = new Image("/collision.png", ArenaManager.GRID_WIDTH / 8, ArenaManager.GRID_WIDTH / 8, true, true);
            ArenaManager.getActiveUIController().drawImage(img, subject.getX(), subject.getY(), Monster.DEATH_DISPLAY_DURATION);
        }
    };

    /**
     * The method invoked when the next frame has finished processing.
     * <p>
     * Invokes the game over event if a monster has reached the end-zone.
     */
    @Transient
    private EventHandler<EventArgs> onEndNextFrame = (sender, args) -> {
        if (currentFrame++ % ArenaManager.WAVE_INTERVAL == 0) spawnWave();

        ArenaObjectRectangleSelector selector = new ArenaObjectRectangleSelector(ArenaManager.END_X, ArenaManager.END_Y, (short) 0, (short) 0);
        LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.MONSTER));
        if (!result.isEmpty()) {
            ArenaManager.getActiveEventRegister().ARENA_GAME_OVER.invoke(this, new EventArgs());
        }
    };

    /**
     * Default constructor.
     */
    public ArenaInstance() {}

    /**
     * load object after initialise from jpa.
     */
    @PostLoad
    public void loadArenaInstance() {
        eventRegister = new ArenaEventRegister();
        eventRegister.ARENA_OBJECT_ADD.subscribe(onAddObject);
        eventRegister.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
        eventRegister.ARENA_NEXT_FRAME_END.subscribe(onEndNextFrame);

        player.attachToArena(this);
        scalarFieldRegister = new ArenaScalarFieldRegister(this); // Scalar fields may be based on objects on the arena
    }

    /**
     * Constructs a newly allocated {@link ArenaInstance} object and attaches a player to it.
     * @param player The player of the arena.
     */
    public ArenaInstance(Player player) {
        eventRegister = new ArenaEventRegister();
        eventRegister.ARENA_OBJECT_ADD.subscribe(onAddObject);
        eventRegister.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
        eventRegister.ARENA_NEXT_FRAME_END.subscribe(onEndNextFrame);

        this.player = player; player.attachToArena(this);
        storage = new ArenaObjectStorage(this);
        scalarFieldRegister = new ArenaScalarFieldRegister(this); // Scalar fields may be based on objects on the arena
    }

    /**
     * Returns the player attached to the arena.
     * @return The player attached to the arena.
     */
    public Player getPlayer() { return player; }

    /**
     * Returns the event register attached to the arena.
     * @return The event register attached to the arena.
     */
    public ArenaEventRegister getEventRegister() { return eventRegister; }

    /**
     * Returns the scalar field register attached to the arena.
     * @return The scalar field register attached to the arena.
     */
    public ArenaScalarFieldRegister getScalarFieldRegister() { return scalarFieldRegister; }
    
    /**
     * Returns the storage of the arena.
     * @return The storage of the arena.
     */
    public ArenaObjectStorage getStorage() { return storage; }

    /**
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * Does not take into account constraints that are not related to location.
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     */
    public boolean canBuildTowerAt(short x, short y)
    {
        ArenaObjectGridSelector gridSelector = new ArenaObjectGridSelector(x, y);
        LinkedList<ArenaObject> objectsInGrid = storage.getQueryResult(gridSelector, EnumSet.of(StoredType.TOWER, StoredType.MONSTER));
        if (!objectsInGrid.isEmpty()) return false;

        short gridXPos = ArenaManager.getGridXPosFromCoor(x);
        if (gridXPos == ArenaManager.getStartingGridXPos() || gridXPos == ArenaManager.getEndGridXPos()) {
            return false;
        }

        short gridYPos = ArenaManager.getGridYPosFromCoor(y);
        if (gridYPos == ArenaManager.getStartingGridYPos() || gridXPos == ArenaManager.getEndGridYPos()) {
            return false;
        }
        
        return hasRoute(x, y);
    }

    /**
     * Determines whether some monster cannot go to end-zone after building a tower.
     * @param x The x-coordinate of tower to be built.
     * @param y The y-coordinate of tower to be built.
     * @return true if all monster can go to end-zone after building a tower, false otherwise.
     */
    private boolean hasRoute(short x, short y) {
        short gridXPos = ArenaManager.getGridXPosFromCoor(x);
        short gridYPos = ArenaManager.getGridYPosFromCoor(y);

        boolean[][] noTower = new boolean[ArenaManager.getMaxHorizontalGrids()][ArenaManager.getMaxVerticalGrids()];
        boolean[][] visited = new boolean[ArenaManager.getMaxHorizontalGrids()][ArenaManager.getMaxVerticalGrids()];
        for (short i = 0; i < noTower.length; i++) {
            for (short j = 0; j < noTower[0].length; j++) {
                ArenaObjectGridSelector gridSelector = new ArenaObjectGridSelector(x, y);
                LinkedList<ArenaObject> towersInGrid = storage.getQueryResult(gridSelector, EnumSet.of(StoredType.TOWER));
                noTower[i][j] = towersInGrid.isEmpty();
                
                visited[i][j] = false;
            }
        }
        noTower[gridXPos][gridYPos] = false;
        gridDFS(noTower, visited, ArenaManager.getEndGridXPos(), ArenaManager.getEndGridYPos());

        // { xPos, yPos }
        LinkedList<short[]> hasMonster = new LinkedList<>();
        hasMonster.add(new short[] { 0, 0 });
        for (ArenaObject m : storage.getQueryResult(new ArenaObjectPropertySelector<>(Monster.class, o -> true), EnumSet.of(StoredType.MONSTER))) {
            short monsterGridXPos = ArenaManager.getGridXPosFromCoor(m.getX());
            short monsterGridYPos = ArenaManager.getGridYPosFromCoor(m.getY());
            hasMonster.add(new short[] { monsterGridXPos, monsterGridYPos });
        }

        for (short[] g : hasMonster) {
            if (!visited[g[0]][g[1]])
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
    private void gridDFS(boolean[][] noTower, boolean[][] visited, short x, short y)
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
     * Spawns a wave of {@link Monster}s at the starting position of the arena.
     */
    private void spawnWave()
    {
        Random rng = new Random();
        int spawnCount = (int) (1 + difficulty * 0.2 + 2 * Math.random());
        for (int i = 0; i < spawnCount; i++) {
            double randomNumber = rng.nextDouble();

            if (randomNumber < 1.0 / 3) generateMonster(MonsterType.FOX);
            else if (randomNumber < 2.0 / 3) generateMonster(MonsterType.PENGUIN);
            else generateMonster(MonsterType.UNICORN);
        }

        difficulty += 1;
    }

    /**
     * Generate a {@link Monster} to the arena.
     * @param type specify the type of the monster.
     * @return the monster being generated.
     */
    private void generateMonster(MonsterType type)
    {
        Monster m = ArenaObjectFactory.createMonster(type, ArenaManager.STARTING_X, ArenaManager.STARTING_Y, difficulty);

        System.out.println(String.format("%s:%.2f generated", m.getDisplayName(), m.getHealth()));
    }
}