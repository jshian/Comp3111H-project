package project.arena;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.Geometry;
import project.UIController;
import project.arena.Arena.TypeFilter;
import project.arena.monsters.Monster;
import project.arena.projectiles.Projectile;
import project.arena.towers.Tower;

/**
 * Manages the storage of objects in the arena.
 * 
 * @see Arena
 */
class ArenaObjectStorage {

    /**
     * The arena that the class is linked to.
     */
    private Arena arena;

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
     * Contains a reference to each Monster on the arena.
     * In addition, the monsters are sorted according to how close they are from reaching the end zone.
     * The first element is closest to the end zone while the last element is furthest.
     * @see Monster
     */
    private PriorityQueue<Monster> monsters = new PriorityQueue<>();

    /**
     * Stores grid information of the arena.
     * @see Grid
     */
    private Grid[][] grids = new Grid[UIController.MAX_H_NUM_GRID][UIController.MAX_V_NUM_GRID];
    {
        for (int i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
            for (int j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
                grids[i][j] = new Grid(i, j);
            }
        }
    }

    /**
     * Constructor for ArenaObjectStorage class.
     * @param arena The arena to link this object to.
     */
    ArenaObjectStorage(Arena arena) {
        this.arena = arena;
    }

    /**
     * Constructor for making a copy of ArenaObjectStorage class that is linked to another arena.
     * @param arena The arena to link this object to.
     * @param other The object to copy from.
     */
    ArenaObjectStorage(Arena arena, ArenaObjectStorage other) {
        this.arena = arena;

        for (Tower t : other.towers) {
            Tower new_t = t.deepCopy();
            this.towers.add(new_t);
            Coordinates c = new Coordinates(new_t.getX(), new_t.getY());
            getGrid(c).addObject(new_t);
        }
        
        for (Projectile p : other.projectiles) {
            Projectile new_p = p.deepCopy();
            this.projectiles.add(new_p);
            Coordinates c = new Coordinates(new_p.getX(), new_p.getY());
            getGrid(c).addObject(new_p);
        }

        for (Monster m : other.monsters) {
            Monster new_m = m.deepCopy();
            this.monsters.add(new_m);
            Coordinates c = new Coordinates(new_m.getX(), new_m.getY());
            getGrid(c).addObject(new_m);
        }
    }

    /**
     * Accesses the grid that contains the specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @return The grid that contains the specified pixel.
     */
    Grid getGrid(@NonNull Coordinates coordinates) {
        return getGrid(Grid.findGridXPos(coordinates), Grid.findGridYPos(coordinates));
    }

    /**
     * Accesses the grid at the specified location.
     * @param xPos The x-position of the grid.
     * @param yPos The y-position of the grid.
     * @return The grid at the specified location.
     */
    Grid getGrid(int xPos, int yPos) {
        return grids[xPos][yPos];
    }
    
    /**
     * Accessor for the Towers contained in the object.
     * @return A linked list containing a reference to each Tower in the arena.
     */
    LinkedList<Tower> getTowers() { return towers; }

    /**
     * Accessor for the Projectiles contained in the object.
     * @return A linked list containing a reference to each Projectile in the arena.
     */
    LinkedList<Projectile> getProjectiles() { return projectiles; }

    /**
     * Accessor for the Monsters contained in the object.
     * @return A priority queue containing a reference to each Monster in the arena. The first element is closest to the end zone while the last element is furthest.
     */
    PriorityQueue<Monster> getMonsters() { return monsters; }

    /**
     * Finds the grids that may be within a specified distance of a specified pixel.
     * @param coordinates The coordinates of the pixel.
     * @param range The maximum allowable distance.
     * @return A linked list containing references to the conservative estimate of the grids that are within a specified distance of the specified pixel.
     */
    LinkedList<Grid> getPotentialGridsInRange(@NonNull Coordinates coordinates, double range) {
        LinkedList<Grid> result = new LinkedList<>();

        for (int i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
            for (int j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
                int x = coordinates.getX();
                int y = coordinates.getY();
                int gridX = Grid.findGridCenterX(i, j);
                int gridY = Grid.findGridCenterY(i, j);

                if (Geometry.findEuclideanDistanceToPoint(x, y, gridX, gridY)
                    <= range + Math.pow(UIController.GRID_WIDTH + UIController.GRID_HEIGHT, 2))
                    {
                        result.add(grids[i][j]);
                    }
            }
        }

        return result;
    }

    /**
     * @see Arena#findObjectsAtPixel(Coordinates, EnumSet)
     */
    LinkedList<ExistsInArena> findObjectsAtPixel(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
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
     * @see Arena#findObjectsInRange(Coordinates, double, EnumSet)
     */
    LinkedList<ExistsInArena> findObjectsInRange(@NonNull Coordinates coordinates, double range, @NonNull EnumSet<TypeFilter> filter)
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
     * @see Arena#findObjectsInGrid(int, int, EnumSet)
     */
    LinkedList<ExistsInArena> findObjectsInGrid(int xPos, int yPos, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();
        
        LinkedList<ExistsInArena> list = grids[xPos][yPos].getAllObjects();
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
     * @see Arena#findObjectsInGrid(Coordinates, EnumSet)
     */
    LinkedList<ExistsInArena> findObjectsInGrid(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        return findObjectsInGrid(Grid.findGridXPos(coordinates), Grid.findGridYPos(coordinates), filter);
    }

    /**
     * @see Arena#findObjectsOccupying(Coordinates)
     */
    LinkedList<Object> findObjectsOccupying(@NonNull Coordinates coordinates)
    {
        LinkedList<Object> result = new LinkedList<>();

        result.addAll(findObjectsAtPixel(coordinates, EnumSet.of(TypeFilter.Projectile, TypeFilter.Monster)));
        result.addAll(findObjectsInGrid(coordinates, EnumSet.of(TypeFilter.Tower)));
        
        return result;
    }

    /**
     * @see Arena#countInRangeOfTowers(Coordinates)
     */
    public int countInRangeOfTowers(@NonNull Coordinates coordinates) {
        int x = coordinates.getX();
        int y = coordinates.getY();

        int count = 0;
        for (Tower t : towers) {
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
     * Updates the object storage with respect to the addition of an object.
     * @param obj The object to add.
     * @throws IllegalArgumentException If the object type is not recognized.
     */
    void processAddObject(@NonNull ExistsInArena obj) throws IllegalArgumentException {
        if (obj instanceof Tower) {
            if (!towers.contains(obj)) {
                towers.add((Tower)obj);
            }
        } else if (obj instanceof Projectile) {
            if (!projectiles.contains(obj)) {
                projectiles.add((Projectile)obj);
            }
        } else if (obj instanceof Monster) {
            if (!monsters.contains(obj)) {
                monsters.add((Monster)obj);
            }
        } else {
            throw new IllegalArgumentException("The object type is not recognized");
        }

        Coordinates c = new Coordinates(obj.getX(), obj.getY());
        getGrid(c).addObject(obj);
    }

    /**
     * Updates the object storage with respect to the removal of an object.
     * @param obj The object to remove.
     * @throws IllegalArgumentException If the object type is not recognized.
     */
    void processRemoveObject(@NonNull ExistsInArena obj) throws IllegalArgumentException {
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
     * Updates the object storage with respect to the movement of an object.
     * @param obj The object to move.
     * @param newCoordinates The coordinates of the new location.
     */
    void processMoveObject(@NonNull ExistsInArena obj, @NonNull Coordinates newCoordinates)
    {
        Coordinates c = new Coordinates(obj.getX(), obj.getY());
        getGrid(c).removeObject(obj);

        obj.setLocation(newCoordinates);

        getGrid(newCoordinates).addObject(obj);
    }
    
    /**
     * Updates the object storage with respect to the updating of an object to the next frame.
     * @param obj The object to update.
     * @return The object that has been marked as pending removal, or <code>null</code> if none.
     */
    ExistsInArena processObjectNextFrame(@NonNull ExistsInArena obj) {
        Coordinates originalCoordinates = new Coordinates(obj.getX(), obj.getY());
        obj.nextFrame();
        Coordinates newCoordinates = new Coordinates(obj.getX(), obj.getY());

        if (!Geometry.isAt(newCoordinates.getX(), newCoordinates.getY(), originalCoordinates.getX(), originalCoordinates.getY())) {
            getGrid(originalCoordinates).removeObject(obj);
            getGrid(newCoordinates).addObject(obj);
        }
        

        if (obj instanceof Tower) {
            arena.createProjectile((Tower)obj);
        } else if (obj instanceof Projectile) {
            if (((Projectile)obj).hasReachedTarget()) {
                return obj;
            }
        } else if (obj instanceof Monster) {
            if (((Monster)obj).hasDied()) {
                return obj;
            }
        }

        return null;
    }
}