package project;

import project.monsters.*;

import java.util.*;

import org.apache.commons.lang3.*;
import project.towers.BasicTower;

/**
 * The area where most of the action takes place in the game.
 * Monsters spawn at the starting position and try to reach the end-zone.
 * Towers can be built on the arena.
 */
public class Arena {

    private static final int STARTING_POSITION_X = 0;
    private static final int STARTING_POSITION_Y = 0;
    private static final int END_ZONE_X = 440;
    private static final int END_ZONE_Y = 0;

    // Objects in the arena
    private LinkedList<BasicTower> towers = new LinkedList<>();
    private LinkedList<Projectile> projectiles = new LinkedList<>();
    private LinkedList<Monster> monsters = new LinkedList<>();

    /**
     * Finds the grid position corresponding to a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @return An int array that contains the x- and y- position of the grid at indices 0 and 1 respectively.
     * @see Coordinates
     */
    private int[] getGrid(Coordinates coordinates) {
        return new int[] { coordinates.getX() / UIController.GRID_WIDTH, coordinates.getY() / UIController.GRID_HEIGHT };
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
     * @see Coordinates
     * @see TypeFilter
     */
    public LinkedList<Object> getObjectsAtPixel(Coordinates coordinates, EnumSet<TypeFilter> filter)
    {
        LinkedList<Object> list = new LinkedList<>();

        if (filter.contains(TypeFilter.Tower)) {
            for (BasicTower t : towers)
                if (coordinates.isAt(t.getCoordinates()))
                    list.add(t);
        }
        
        if (filter.contains(TypeFilter.Projectile)) {
            for (Projectile p : projectiles)
                if (coordinates.isAt(p.getCoordinates()))
                        list.add(p);
    
        }

        if (filter.contains(TypeFilter.Monster)) {
            for (Monster m : monsters)
                if (coordinates.isAt(m.getCoordinates()))
                    list.add(m);
        }

        return list;
    }

    /**
     * Finds all objects that are located inside the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @param filter Only the types that are specified will be included in the result.
     * @return A linked list containing a reference to each object that satisfies the above criteria.
     * @see Coordinates
     * @see TypeFilter
     */
    public LinkedList<Object> getObjectsInGrid(Coordinates coordinates, EnumSet<TypeFilter> filter)
    {
        LinkedList<Object> list = new LinkedList<>();

        if (filter.contains(TypeFilter.Tower)) {
            for (BasicTower t : towers) {
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
        
        if (filter.contains(TypeFilter.Projectile)) {
            for (Projectile p : projectiles)
                if (coordinates.isAt(p.getCoordinates()))
                        list.add(p);
    
        }

        if (filter.contains(TypeFilter.Monster)) {
            for (Monster m : monsters)
                if (coordinates.isAt(m.getCoordinates()))
                    list.add(m);
        }

        return list;
    }

    /**
     * Determines whether a Tower can be built at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @return Whether a Tower can be built at the grid where the specified pixel is located.
     * @see Coordinates
     */
    public boolean canBuildTower(Coordinates coordinates)
    {
        return getObjectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower, TypeFilter.Monster)).isEmpty();
    }

    /**
     * Builds a Tower at the grid where a specified pixel is located.
     * @param coordinates The coordinates of the pixel.
     * @see Coordinates
     */
    public void buildTower(Coordinates coordinates)
    {
        throw new NotImplementedException("TODO");
    }

    /**
     * Destroys the specified Tower.
     * @param tower The Tower to be destroyed.
     */
    public void destroyTower(BasicTower tower)
    {
        towers.remove(tower);
    }

    /**
     * Creates a Projectile at a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @see Coordinates
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
     * Spawns a Monster at the starting position of the arena.
     */
    public void spawnMonster()
    {
        throw new NotImplementedException("TODO");
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
     * Interface for objects that exist inside an Arena.
     */
    public interface ExistsInArena {
        /**
         * Accesses the image path of the object.
         * @return The file path to the image relative to the project root.
         */
        public String getImagePath();
    
        /**
         * Accesses the coordinates of the object.
         * @return The coordinates of the object.
         * @see Coordinates
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