package project.arena;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import project.Geometry;
import project.Player;
import project.controller.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Fox;
import project.entity.Monster;
import project.entity.Penguin;
import project.entity.Unicorn;
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
     * The player attached to the arena.
     */
    @NotNull
    @OneToOne
    private Player player;

    /**
     * The register of {@link EventManager}s attached to the arena.
     */
    @Transient
    private ArenaEventRegister eventRegister = new ArenaEventRegister();

    /**
     * The register of (@link ArenaScalarField}s attached to the arena.
     */
    @Transient
    private ArenaScalarFieldRegister scalarFieldRegister = new ArenaScalarFieldRegister();

    /**
     * The UI attached to the arena.
     */
    @Transient
    private AnchorPane paneArena;

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
     * Contains a reference to line, circle, and monster explosion on the arena.
     */
    private HashMap<Node, Integer> toRemove = new HashMap<>();

    /**
     * The method invoked when an {@link ArenaObject} is being added.
     */
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        ArenaObject subject = args.subject;
        ImageView imageView = subject.getImageView();

        paneArena.getChildren().add(imageView);

        if (subject instanceof Monster) {
            Tooltip tp = new Tooltip();
            StringExpression str = Bindings.format(((Monster) subject).getDisplayDetails());
            tp.textProperty().bind(str);
    
            imageView.setOnMouseEntered(e -> tp.show(imageView, e.getScreenX()+8, e.getScreenY()+7));
            imageView.setOnMouseMoved(e -> tp.show(imageView, e.getScreenX()+8, e.getScreenY()+7));
            imageView.setOnMouseExited(e -> tp.hide());
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is being removed.
     */
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        paneArena.getChildren().remove(subject.getImageView());
    };

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
     * The method invoked when the next frame has finished processing.
     * <p>
     * Invokes the game over event if a monster has reached the end-zone.
     */
    private EventHandler<EventArgs> onEndNextFrame = (sender, args) -> {
        remove();
        // add callback from monsters to add explosion
        // ImageView explosion = new ImageView(new Image("/collision.png", ArenaManager.GRID_WIDTH / 8
        //         , ArenaManager.GRID_WIDTH / 8, true, true));
        // c.bindByImage(explosion);
        // paneArena.getChildren().add(explosion);
        // toRemove.put(explosion, Monster.DEATH_DISPLAY_DURATION);

        if (currentFrame % ArenaManager.WAVE_INTERVAL == 0) spawnWave();
        currentFrame++;

        ArenaObjectRectangleSelector selector = new ArenaObjectRectangleSelector(ArenaManager.END_X, ArenaManager.END_Y, (short) 0, (short) 0);
        LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.MONSTER));
        if (!result.isEmpty()) {
            ArenaManager.getActiveEventRegister().ARENA_GAME_OVER.invoke(this, new EventArgs());
        }
    };

    /**
     * The Arena during the previous frame. Only used for saving the game.
     */
    @OneToOne
    private ArenaInstance shadowArena;

    /**
     * Constructor of the Arena class. Bind the label to resources.
     * @param resourceLabel the label to show remaining resources of player.
     * @param paneArena the arena pane of the game.
     */
    public ArenaInstance(Label resourceLabel, AnchorPane paneArena) {
        player = new Player("name", 200);
        resourceLabel.textProperty().bind(Bindings.format("Money: %d", player.resourcesProperty()));
        this.paneArena = paneArena;
        
        ArenaEventRegister register = ArenaManager.getActiveEventRegister();
        register.ARENA_OBJECT_ADD.subscribe(onAddObject);
        register.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
        register.ARENA_NEXT_FRAME_END.subscribe(onEndNextFrame);
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
     * Returns the UI attached to the arena.
     * @return The UI attached to the arena.
     */
    public AnchorPane getPane() { return paneArena; }

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

        short gridXPos = (short) (x / ArenaManager.GRID_WIDTH);
        if (gridXPos == ArenaManager.getStartingGridXPos() || gridXPos == ArenaManager.getEndGridXPos()) {
            return false;
        }

        short gridYPos = (short) (y / ArenaManager.GRID_HEIGHT);
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
        short gridXPos = (short) (x / ArenaManager.GRID_WIDTH);
        short gridYPos = (short) (y / ArenaManager.GRID_HEIGHT);

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
            short monsterGridXPos = (short) (m.getX() / ArenaManager.GRID_WIDTH);
            short monsterGridYPos = (short) (m.getY() / ArenaManager.GRID_HEIGHT);
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
     * Types of monsters that can be spawned.
     */
    enum MonsterType { FOX, PENGUIN, UNICORN; }

    /**
     * Spawns a wave of {@link Monster}s at the starting position of the arena.
     */
    private void spawnWave()
    {
        int spawnCount = (int) (1 + difficulty * 0.2 + 2 * Math.random());
        for (int i = 0; i < spawnCount; i++) {
            double randomNumber = Math.random();
            if (randomNumber < 1/3.0)
                generateMonster(MonsterType.FOX);
            else if (randomNumber < 2/3.0)
                generateMonster(MonsterType.PENGUIN);
            else
                generateMonster(MonsterType.UNICORN);
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
        // Create some randomness of spawn location
        short startX = (short) (ArenaManager.STARTING_X + Math.random() * ArenaManager.GRID_WIDTH / 2);
        if (startX < 0) startX = 0;
        if (startX >= ArenaManager.ARENA_WIDTH) startX = ArenaManager.ARENA_WIDTH;
        short startY = (short) (ArenaManager.STARTING_Y + Math.random() * ArenaManager.GRID_HEIGHT / 2);
        if (startY < 0) startY = 0;
        if (startY >= ArenaManager.ARENA_HEIGHT) startY = ArenaManager.ARENA_HEIGHT;

        Monster m = null;
        switch (type) {
            case FOX: m = new Fox(storage, startX, startY, difficulty);
            case PENGUIN: m = new Penguin(storage, startX, startY, difficulty);
            case UNICORN: m = new Unicorn(storage, startX, startY, difficulty);
        }
        assert m != null;

        System.out.println(String.format("%s:%.2f generated", m.getDisplayName(), m.getHealth()));
    }

    /**
     * Draws a ray from one point to another point, extending beyond it towards the edge of the arena.
     * @param sourceX The x-coordinate of the source point.
     * @param sourceY The y-coordinate of the source point.
     * @param targetX The x-coordinate of the target point.
     * @param targetY The y-coordinate of the target point.
     * @param duration The duration in number of frames that the ray will remain on the arena.
     */
    public void drawRay(short sourceX, short sourceY, short targetX, short targetY, int duration) {
        Point2D edgePt = Geometry.intersectBox(sourceX, sourceY, targetX, targetY,
                                                    0, 0, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT);
        
        Line ray = new Line(sourceX, sourceY, edgePt.getX(), edgePt.getY());
        ray.setStroke(javafx.scene.paint.Color.rgb(255, 255, 0));
        ray.setStrokeWidth(3);
        paneArena.getChildren().add(ray);
        toRemove.put(ray, duration);
    }

    /**
     * Draws a specific circle at a specific location..
     * @param sourceX The x-coordinate of the circle center.
     * @param sourceY The y-coordinate of the circle center.
     * @param radius The radius of the circle.
     * @param duration The duration in number of frames that the circle will remain on the arena.
     */
    public void drawCircle(short centerX, short centerY, short radius, int duration) {
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(Color.rgb(128, 64, 0));
        paneArena.getChildren().add(circle);
        toRemove.put(circle, duration);
    }
}