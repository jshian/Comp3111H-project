package project;

import project.monsters.*;

import java.util.*;
import java.util.function.Consumer;

import org.apache.commons.lang3.*;
import project.towers.Tower;

/**
 * The area where most of the action takes place in the game.
 * Monsters spawn at the starting position and try to reach the end-zone.
 * towers can be built on the arena.
 */
public class Arena {

    private static final int STARTING_POSITION_X = 0;
    private static final int STARTING_POSITION_Y = 0;
    private static final int END_ZONE_X = 440;
    private static final int END_ZONE_Y = 0;

    // Objects in the arena
    private LinkedList<Tower> towers = new LinkedList<>();
    private LinkedList<Projectile> projectiles = new LinkedList<>();
    private LinkedList<Monster> monsters = new LinkedList<>();

    // Monster-related properties
    private int framesUntilSpawn = 0;
    private double difficulty = 0;

    /**
     * Finds the grid position corresponding to a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @return An int array that contains the x- and y- position of the grid at indices 0 and 1 respectively.
     */
    private int[] getGrid(Coordinates coordinates) {
        int[] result = new int[2];
        result[0] = coordinates.getX() / UIController.GRID_WIDTH;
        result[1] = coordinates.getY() / UIController.GRID_HEIGHT;
        return result;
    }

    /**
     * The default constructor of the Arena class.
     */
    public Arena() {

    }

    /**
     * An enum for filtering objects in the Arena according to type.
     */
    public enum TypeFilter { Tower, Projectile, Monster }

    /**
     * Finds all objects that are located at a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public LinkedList<Object> objectsAtPixel(Coordinates coordinates, EnumSet<TypeFilter> filter)
    {
        LinkedList<Object> list = new LinkedList<>();

        if (filter.contains(TypeFilter.Tower)) {
            for (Tower t : towers)
                if (coordinates.isAt(t.getCoordinates()))
                    list.add(t);
        }
        
        if (filter.contains(TypeFilter.Projectile))
            for (Projectile p : projectiles)
                if (coordinates.isAt(p.getCoordinates()))
                    list.add(p);

        if (filter.contains(TypeFilter.Monster))
            for (Monster m : monsters)
                if (coordinates.isAt(m.getCoordinates()))
                    list.add(m);

        return list;
    }

    /**
     * Finds all objects that are within range of a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public LinkedList<Object> objectsInRange(Coordinates coordinates, double range, EnumSet<TypeFilter> filter)
    {
        LinkedList<Object> list = new LinkedList<>();

        if (filter.contains(TypeFilter.Tower)) {
            for (Tower t : towers)
                if (coordinates.diagonalDistanceFrom(t.getCoordinates()) <= range)
                    list.add(t);
        }
        
        if (filter.contains(TypeFilter.Projectile))
            for (Projectile p : projectiles)
                if (coordinates.diagonalDistanceFrom(p.getCoordinates()) <= range)
                    list.add(p);

        if (filter.contains(TypeFilter.Monster))
            for (Monster m : monsters)
                if (coordinates.diagonalDistanceFrom(m.getCoordinates()) <= range)
                    list.add(m);

        return list;
    }

    /**
     * Finds all objects that are located inside the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see TypeFilter
     */
    public LinkedList<Object> objectsInGrid(Coordinates coordinates, EnumSet<TypeFilter> filter)
    {
        LinkedList<Object> list = new LinkedList<>();

        if (filter.contains(TypeFilter.Tower)) {
            for (Tower t : towers) {
                Coordinates c = t.getCoordinates();
    
                if (coordinates.isAt(c))
                    list.add(t);
                else {
                    int[] gridPosition = getGrid(c);
    
                    int xMin = gridPosition[0] * UIController.GRID_WIDTH;
                    int xMax = xMin + UIController.GRID_WIDTH;
                    int yMin = gridPosition[1] * UIController.GRID_HEIGHT;
                    int yMax = yMin + UIController.GRID_HEIGHT;
                    
                    int x = coordinates.getX();
                    int y = coordinates.getY();

                    if (xMin <= x && x <= xMax && yMin <= y && y <= yMax)
                        list.add(t);
                }
            }
        }
        
        if (filter.contains(TypeFilter.Projectile))
            for (Projectile p : projectiles)
                if (coordinates.isAt(p.getCoordinates()))
                    list.add(p);

        if (filter.contains(TypeFilter.Monster))
            for (Monster m : monsters)
                if (coordinates.isAt(m.getCoordinates()))
                    list.add(m);

        return list;
    }

    /**
     * Finds the center of the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @return Coordinates representing the center of the grid.
     */
    public Coordinates getGridCenter(Coordinates coordinates)
    {
        int[] gridPosition = getGrid(coordinates);

        return new Coordinates(coordinates.getArena(),
            (int) ((gridPosition[0] + 0.5) * UIController.GRID_WIDTH),
            (int) ((gridPosition[1] + 0.5) * UIController.GRID_HEIGHT));
    }

    /**
     * Finds the grids within a taxicab distance of one from the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @return A linked list containing a reference to the coordinates of each taxicab neighbour.
     */
    public LinkedList<Coordinates> taxicabNeighbours(Coordinates coordinates)
    {
        LinkedList<Coordinates> result = new LinkedList<>();

        int[] gridPosition = getGrid(coordinates);
        int gridX = gridPosition[0];
        int gridY = gridPosition[1];

        // Left neighbour
        if (gridX > 0)
            result.add(new Coordinates(this,
                getGridCenter(coordinates).getX() - UIController.GRID_WIDTH,
                getGridCenter(coordinates).getY()
            ));
        
        // Right neighbour
        if (gridX < UIController.MAX_H_NUM_GRID - 1)
            result.add(new Coordinates(this,
                getGridCenter(coordinates).getX() + UIController.GRID_WIDTH,
                getGridCenter(coordinates).getY()
            ));
        
        // Top neighbour
        if (gridY > 0)
            result.add(new Coordinates(this,
                getGridCenter(coordinates).getX(),
                getGridCenter(coordinates).getY() - UIController.GRID_HEIGHT
            ));

        // Bottom neighbour
        if (gridY < UIController.MAX_V_NUM_GRID - 1)
            result.add(new Coordinates(this,
                getGridCenter(coordinates).getX(),
                getGridCenter(coordinates).getY() + UIController.GRID_HEIGHT
            ));

        return result;
    }

    /**
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     */
    public boolean canBuildTower(Coordinates coordinates)
    {
        return objectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower, TypeFilter.Monster)).isEmpty();
    }

    /**
     * Builds a Tower at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     */
    public void buildTower(Coordinates coordinates)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Destroys the specified Tower.
     * @param tower The Tower to be destroyed.
     */
    public void destroyTower(Tower tower)
    {
        towers.remove(tower);
    }

    /**
     * Creates a Projectile at a specified pixel.
     * @param coordinates The coordinates of the pixel.
     */
    public void createProjectile(Coordinates coordinates)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Removes the specified Projectile from the arena.
     * @param projectile The Projectile to be removed.
     */
    public void removeProjectile(Projectile projectile)
    {
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
            if (randomNumber < 1/3)
                monsters.add(new Fox(this, difficulty, new Coordinates(this, END_ZONE_X, END_ZONE_Y)));
            else if (randomNumber < 2/3)
                monsters.add(new Penguin(this, difficulty, new Coordinates(this, END_ZONE_X, END_ZONE_Y)));
            else
                monsters.add(new Unicorn(this, difficulty, new Coordinates(this, END_ZONE_X, END_ZONE_Y)));
        }

        difficulty += 1;    // Modified by settings later
    }

    /**
     * Removes the specified Monster from the arena.
     * @param monster The Monster to be removed.
     */
    public void removeMonster(Monster monster)
    {
        monsters.remove(monster);
    }

    /**
     * Updates the arena by one frame.
     */
    public void nextFrame() {
        throw new NotImplementedException("TODO");
    }

    /**
     * Accesses the towers exist
     * @return The towers in Arena
     */
    public LinkedList<Tower> getTowers() { return towers; }

    /**
     * Accesses the monsters exist
     * @return The monsters in Arena
     */
    public LinkedList<Monster> getMonsters() { return monsters;}

    /**
     * Accesses the projectile
     * @return The projectiles in Arena
     */
    public LinkedList<Projectile> getProjectiles(){ return projectiles;}
    /**
     * Interface for objects that exist inside an Arena.
     */
    public interface ExistsInArena {
        /**
         * Updates the corresponding UI object.
         */
        public void refreshDisplay();
    
        /**
         * Accesses the coordinates of the object.
         * @return The coordinates of the object.
         */
        public Coordinates getCoordinates();
    }
    
    /**
     * Interface for objects that can move inside an Arena.
     */
    public interface MovesInArena extends ExistsInArena {
        /**
         * Moves the object by one frame.
         */
        public void MoveOneFrame();
    }
}