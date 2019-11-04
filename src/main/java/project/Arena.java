package project;

import project.monsters.*;
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
    private static ArenaState shadowState;

    /**
     * The ArenaState of the current frame.
     */
    private static ArenaState currentState = new ArenaState();

    /**
     * The default constructor of the Arena class.
     */
    public Arena() {}

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

                    if (xMin <= x && x <= xMax && yMin <= y && y <= yMax)
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
     * Builds a Tower at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param iv ImageView of the tower
     * @param type specify the class of tower.
     */
    public static Tower buildTower(@NonNull Coordinates coordinates, ImageView iv, String type)
    {
        System.out.println("ok");
        Tower t = null;
        switch(type) {
            case "basic": t = new BasicTower(coordinates, iv); break;
            case "ice": t = new IceTower(coordinates, iv); break;
            case "catapult": t = new Catapult(coordinates, iv); break;
            case "laser": t = new LaserTower(coordinates, iv); break;
        }

        currentState.towers.add(t);

        return t;
    }

    /**
     * Destroys the specified Tower.
     * @param tower The Tower to be destroyed.
     */
    public static void destroyTower(Tower tower)
    {
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
    public static void removeProjectile(Projectile projectile)
    {
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
     */
    public static void removeMonster(Monster monster)
    {
        currentState.monsters.remove(monster);
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