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
import org.checkerframework.checker.nullness.qual.Nullable;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import project.Geometry;
import project.Player;
import project.UIController;
import project.arena.ArenaObjectFactory.MonsterType;
import project.arena.ArenaObjectFactory.TowerType;
import project.controller.ArenaEventRegister;
import project.controller.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.event.EventHandler;
import project.event.EventManager;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.EventArgs;
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
     * The factory to create the objects in the arena.
     */
    @Transient
    private ArenaObjectFactory arenaObjectFactory = new ArenaObjectFactory(this);

    /**
     * The objects stored in this arena.
     */
    @NotNull
    @OneToOne
    private ArenaObjectStorage arenaObjectStorage;

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
     * The method invoked when the next frame has finished processing.
     * 
     * Broadcasts the {@link ArenaEventRegister#ARENA_GAME_OVER} event if a monster has
     * reached the end-zone.
     */
    private EventHandler<EventArgs> onEndNextFrame = (sender, args) -> {
        ArenaObjectRectangleSelector selector = new ArenaObjectRectangleSelector(ArenaManager.END_X, ArenaManager.END_Y, (short) 0, (short) 0);
        LinkedList<ArenaObject> result = arenaObjectStorage.getQueryResult(selector, EnumSet.of(StoredType.MONSTER));
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
     * Returns the storage of the arena.
     * @return The storage of the arena.
     */
    public ArenaObjectStorage getArenaObjectStorage() { return arenaObjectStorage; }

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
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param type type of the tower.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     */
    public boolean canBuildTower(Coordinates coordinates, TowerType type)
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
    private boolean hasRoute(Coordinates coordinates) {
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
     * Builds a Tower at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param type specify the class of tower.
     * @return the tower being built, or null if not enough resources
     */
    public @Nullable Tower buildTower(Coordinates coordinates, TowerType type)
    {
        Coordinates center = Grid.findGridCenter(coordinates);
        Tower t = arenaObjectFactory.createTower(type, center);
        int cost = t.getBuildValue();

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
    public boolean upgradeTower(Tower t) {
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
    public Monster generateMonster(MonsterType type)
    {
        // Create some randomness of spawn location
        short startX = (short) (STARTING_COORDINATES.getX() + Math.random() * UIController.GRID_WIDTH / 2);
        if (startX < 0) startX = 0;
        if (startX >= UIController.ARENA_WIDTH) startX = UIController.ARENA_WIDTH;
        short startY = (short) (STARTING_COORDINATES.getY() + Math.random() * UIController.GRID_HEIGHT / 2);
        if (startY < 0) startY = 0;
        if (startY >= UIController.ARENA_HEIGHT) startY = UIController.ARENA_HEIGHT;
        Coordinates start = new Coordinates(startX, startY);

        // The end zone is always the same
        Coordinates destination = Grid.findGridCenter(END_COORDINATES);

        Monster m = arenaObjectFactory.createMonster(type, start, destination, difficulty);
        addObject(m);
        System.out.println(String.format("%s:%.2f generated", type, m.getHealth()));

        return m;
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
        ArrayList<ArenaObject> objectsToBeAdded = new ArrayList<>();
        ArrayList<ArenaObject> objectsToBeRemoved = new ArrayList<>();

        // update projectile
        for (Projectile p : getProjectiles()) {
            ArenaObject toRemove = objectNextFrame(p)[1];
            if (toRemove != null) {
                objectsToBeRemoved.add(toRemove);
            }
        }

        // towers attack monsters
        for (Tower t : getTowers()) {
            ArenaObject toAdd = objectNextFrame(t)[0];
            if (toAdd != null) {
                objectsToBeAdded.add(toAdd);
            }
        }

        // update monsters
        for (Monster m : getMonsters()) {
            ArenaObject toRemove = objectNextFrame(m)[1];
            if (toRemove != null) {
                objectsToBeRemoved.add(toRemove);
            }
        }

        // remove objects
        for (ArenaObject e : objectsToBeRemoved) {
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
        for (ArenaObject e : objectsToBeAdded) {
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


        // Now update currentState
        remove();
        nextFrameForAllObjects();
        if (currentFrame % WAVE_INTERVAL == 0)
            spawnWave();
        currentFrame++;

        return false;
    }

    /**
     * Draws a ray from one object to another object, extending towards the edge of the arena.
     * @param source The origin of the ray.
     * @param target The target of the ray.
     */
    public void drawRay(ArenaObject source, ArenaObject target) {
        drawRay(new Coordinates(source.getX(), source.getY()), new Coordinates(target.getX(), target.getY()));
    }

    /**
     * Draws a ray from one point to another point, extending towards the edge of the arena.
     * @param source The origin of the ray.
     * @param target The target of the ray.
     */
    public void drawRay(Coordinates source, Coordinates target) {
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
    public void drawCircle(Coordinates source, short damageRange){
        Circle circle = new Circle();
        circle.setCenterX(source.getX());
        circle.setCenterY(source.getY());
        circle.setRadius(damageRange);
        circle.setFill(Color.rgb(128, 64, 0));
        paneArena.getChildren().add(circle);
        toRemove.put(circle,1);
    }
}