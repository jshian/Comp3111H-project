package project.arena;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

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
@Entity
final class ArenaObjectStorage {

    /**
     * The arena that the class is linked to.
     */
    @OneToOne(mappedBy = "arena")
    private Arena arena;

    /**
     * Contains a reference to each Tower on the arena.
     * @see Tower
     */
    @OneToMany
    private LinkedList<Tower> towers = new LinkedList<>();

    /**
     * Contains a reference to each Projectile on the arena.
     * @see Projectile
     */
    @OneToMany
    private LinkedList<Projectile> projectiles = new LinkedList<>();

    /**
     * Contains a reference to each Monster on the arena.
     * In addition, the monsters are sorted according to how close they are from reaching the end zone.
     * The first element is closest to the end zone while the last element is furthest.
     * @see Monster
     */
    @OneToMany
    private PriorityQueue<Monster> monsters = new PriorityQueue<>();

    /**
     * Stores grid information of the arena.
     * @see Grid
     */
    @Transient
    private Grid[][] grids = new Grid[UIController.MAX_H_NUM_GRID][UIController.MAX_V_NUM_GRID];
    {
        for (short i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
            for (short j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
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
            Tower new_t = t.deepCopy(arena);
            this.towers.add(new_t);
            Coordinates c = new Coordinates(new_t.getX(), new_t.getY());
            getGrid(c).addObject(new_t);
        }
        
        // Make sure projectiles are bound to the copied target monster
        HashMap<Monster, Monster> targetMap = new HashMap<>(); // Original -> Copy

        for (Projectile p : other.projectiles) {
            Monster originalTarget = p.getTarget();

            Projectile new_p;
            if (targetMap.containsKey(originalTarget)) {
                new_p = p.deepCopy(arena, targetMap.get(originalTarget));
            } else {
                Monster copiedTarget = originalTarget.deepCopy(arena);
                targetMap.put(originalTarget, copiedTarget);
                new_p = p.deepCopy(arena, copiedTarget);
            }

            this.projectiles.add(new_p);

            Coordinates c = new Coordinates(new_p.getX(), new_p.getY());
            getGrid(c).addObject(new_p);
        }

        for (Monster m : other.monsters) {
            Monster new_m;
            if (targetMap.containsKey(m)) {
                new_m = targetMap.get(m);
            } else {
                new_m = m.deepCopy(arena);
            }

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
    Grid getGrid(short xPos, short yPos) {
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

        for (short i = 0; i < UIController.MAX_H_NUM_GRID; i++) {
            for (short j = 0; j < UIController.MAX_V_NUM_GRID; j++) {
                short x = coordinates.getX();
                short y = coordinates.getY();
                short gridX = Grid.findGridCenterX(i, j);
                short gridY = Grid.findGridCenterY(i, j);

                if (Geometry.findEuclideanDistance(x, y, gridX, gridY)
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

        Grid grid = grids[Grid.findGridXPos(coordinates)][Grid.findGridYPos(coordinates)];

        if (filter.contains(TypeFilter.Tower)) {
            for (Tower t : grid.getTowers()) {
                if (Geometry.findTaxicabDistance(coordinates.getX(), coordinates.getY(), t.getX(), t.getY()) == 0) {
                    result.add(t);
                }
            }
        }

        if (filter.contains(TypeFilter.Projectile)) {
            for (Projectile p : grid.getProjectiles()) {
                if (Geometry.findTaxicabDistance(coordinates.getX(), coordinates.getY(), p.getX(), p.getY()) == 0) {
                    result.add(p);
                }
            }
        }

        if (filter.contains(TypeFilter.Monster)) {
            for (Monster m : grid.getMonsters()) {
                if (Geometry.findTaxicabDistance(coordinates.getX(), coordinates.getY(), m.getX(), m.getY()) == 0) {
                    result.add(m);
                }
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
            if (filter.contains(TypeFilter.Tower)) {
                for (Tower t : grid.getTowers()) {
                    if (Geometry.findEuclideanDistance(coordinates.getX(), coordinates.getY(), t.getX(), t.getY()) <= range) {
                        result.add(t);
                    }
                }
            }
    
            if (filter.contains(TypeFilter.Projectile)) {
                for (Projectile p : grid.getProjectiles()) {
                    if (Geometry.findEuclideanDistance(coordinates.getX(), coordinates.getY(), p.getX(), p.getY()) <= range) {
                        result.add(p);
                    }
                }
            }
    
            if (filter.contains(TypeFilter.Monster)) {
                for (Monster m : grid.getMonsters()) {
                    if (Geometry.findEuclideanDistance(coordinates.getX(), coordinates.getY(), m.getX(), m.getY()) <= range) {
                        result.add(m);
                    }
                }
            }
        }

        return result;
    }

    /**
     * @see Arena#findObjectsInGrid(Coordinates, EnumSet)
     */
    LinkedList<ExistsInArena> findObjectsInGrid(@NonNull Coordinates coordinates, @NonNull EnumSet<TypeFilter> filter)
    {
        LinkedList<ExistsInArena> result = new LinkedList<>();

        Grid grid = grids[Grid.findGridXPos(coordinates)][Grid.findGridYPos(coordinates)];

        if (filter.contains(TypeFilter.Tower)) {
            result.addAll(grid.getTowers());
        }

        if (filter.contains(TypeFilter.Projectile)) {
            result.addAll(grid.getProjectiles());
        }

        if (filter.contains(TypeFilter.Monster)) {
            result.addAll(grid.getMonsters());
        }

        return result;
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
     * @return The objects that has been marked as pending { add, remove }.
     */
    ExistsInArena[] processObjectNextFrame(@NonNull ExistsInArena obj) {
        Coordinates originalCoordinates = new Coordinates(obj.getX(), obj.getY());
        obj.nextFrame();
        Coordinates newCoordinates = new Coordinates(obj.getX(), obj.getY());

        if (!Geometry.isAt(newCoordinates.getX(), newCoordinates.getY(), originalCoordinates.getX(), originalCoordinates.getY())) {
            getGrid(originalCoordinates).removeObject(obj);
            getGrid(newCoordinates).addObject(obj);
        }
        
        ExistsInArena[] result = new ExistsInArena[2];

        if (obj instanceof Tower) {
            result[0] = ((Tower)obj).generateProjectile();
        } else if (obj instanceof Projectile) {
            if (((Projectile)obj).hasReachedTarget()) {
                result[1] = obj;
            }
        } else if (obj instanceof Monster) {
            if (((Monster)obj).hasDied()) {
                result[1] = obj;
            }
        }

        return result;
    }
}