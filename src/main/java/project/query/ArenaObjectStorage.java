package project.query;

import java.util.ArrayList;
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
import project.controller.ArenaEventManager;
import project.controller.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.ArenaObjectMoveEventArgs;
import project.event.eventsets.ArenaObjectIOEvent;
import project.event.eventsets.ArenaObjectMoveEvent;

/**
 * Manages the storage of objects in the {@link Arena}.
 * @see ArenaObject
 */
@Entity
public final class ArenaObjectStorage {

    /**
     * Index for each object in the x-direction.
     */
    private ArrayList<LinkedList<ArenaObject>> objectsAtX = new ArrayList<>(ArenaManager.ARENA_WIDTH + 1);
    {
        for (int x = 0; x <= ArenaManager.ARENA_WIDTH; x++) {
            objectsAtX.add(new LinkedList<ArenaObject>());
        }
    }

    /**
     * Index for each object in the y-direction.
     */
    private ArrayList<LinkedList<ArenaObject>> objectsAtY = new ArrayList<>(ArenaManager.ARENA_HEIGHT + 1);
    {
        for (int y = 0; y <= ArenaManager.ARENA_HEIGHT; y++) {
            objectsAtY.add(new LinkedList<ArenaObject>());
        }
    }

    /**
     * Index for each {@link Tower} on the arena.
     */
    @OneToMany
    private LinkedList<Tower> towers = new LinkedList<>();

    /**
     * Index for each {@link Projectile} on the arena.
     */
    @OneToMany
    private LinkedList<Projectile> projectiles = new LinkedList<>();

    /**
     * Index for each {@link Monster} on the arena.
     */
    @OneToMany
    private LinkedList<Monster> monsters = new LinkedList<>();

    /**
     * The method invoked when an {@link ArenaObject} is added.
     */
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        // Add to position-based index
        short x = subject.getX();
        LinkedList<ArenaObject> xList = objectsAtX.get(x);
        assert (!xList.contains(subject));
        xList.add(subject);

        short y = subject.getY();
        LinkedList<ArenaObject> yList = objectsAtY.get(y);
        assert (!yList.contains(subject));
        yList.add(subject);

        // Add to type-based index
        if (subject instanceof Tower) {
            assert (!towers.contains(subject));
            towers.add((Tower) subject);
        } else if (subject instanceof Projectile) {
            assert (!projectiles.contains(subject));
            projectiles.add((Projectile) subject);
        } else if (subject instanceof Monster) {
            assert (!monsters.contains(subject));
            monsters.add((Monster) subject);
        } else {
            System.out.println("Warning: Unknown ArenaObject detected");
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is removed.
     */
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        // Remove from position-based index
        short x = subject.getX();
        LinkedList<ArenaObject> xList = objectsAtX.get(x);
        assert (xList.contains(subject));
        xList.remove(subject);

        short y = subject.getY();
        LinkedList<ArenaObject> yList = objectsAtY.get(y);
        assert (yList.contains(subject));
        yList.remove(subject);

        // Remove from type-based index
        if (subject instanceof Tower) {
            assert (towers.contains(subject));
            towers.remove((Tower) subject);
        } else if (subject instanceof Projectile) {
            assert (projectiles.contains(subject));
            projectiles.remove((Projectile) subject);
        } else if (subject instanceof Monster) {
            assert (monsters.contains(subject));
            monsters.remove((Monster) subject);
        } else {
            System.out.println("Warning: Unknown ArenaObject detected");
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is moved.
     */
    private EventHandler<ArenaObjectMoveEventArgs> onMoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;
        short oldX = args.originalPosition.getX();
        short oldY = args.originalPosition.getY();
        short newX = args.newPosition.getX();
        short newY = args.newPosition.getY();

        // Remove from position-based index
        LinkedList<ArenaObject> xList_old = objectsAtX.get(oldX);
        assert (xList_old.contains(subject));
        xList_old.remove(subject);

        LinkedList<ArenaObject> yList_old = objectsAtY.get(oldY);
        assert (yList_old.contains(subject));
        yList_old.remove(subject);

        // Add to position-based index
        LinkedList<ArenaObject> xList_new = objectsAtX.get(newX);
        assert (!xList_new.contains(subject));
        xList_new.add(subject);

        LinkedList<ArenaObject> yList_new = objectsAtY.get(newY);
        assert (!yList_new.contains(subject));
        yList_new.add(subject);
    };

    /**
     * Constructs a newly allocated ArenaObjectStorage object.
     */
    public ArenaObjectStorage() {
        ArenaEventManager.OBJECT_IO.subscribe(ArenaObjectIOEvent.ADD, onAddObject);
        ArenaEventManager.OBJECT_IO.subscribe(ArenaObjectIOEvent.REMOVE, onRemoveObject);
        ArenaEventManager.OBJECT_MOVE.subscribe(ArenaObjectMoveEvent.MOVE, onMoveObject);
    }

    /**
     * @see Arena#findObjectsAtPixel(Coordinates, EnumSet)
     */
    LinkedList<ArenaObject> findObjectsAtPixel(Coordinates coordinates, EnumSet<TypeFilter> filter)
    {
        LinkedList<ArenaObject> result = new LinkedList<>();

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
    LinkedList<ArenaObject> findObjectsInRange(Coordinates coordinates, double range, EnumSet<TypeFilter> filter)
    {
        LinkedList<ArenaObject> result = new LinkedList<>();

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
    LinkedList<ArenaObject> findObjectsInGrid(Coordinates coordinates, EnumSet<TypeFilter> filter)
    {
        LinkedList<ArenaObject> result = new LinkedList<>();

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
    void processAddObject(ArenaObject obj) throws IllegalArgumentException {
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
    void processRemoveObject(ArenaObject obj) throws IllegalArgumentException {
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
    void processMoveObject(ArenaObject obj, Coordinates newCoordinates)
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
    ArenaObject[] processObjectNextFrame(ArenaObject obj) {
        Coordinates originalCoordinates = new Coordinates(obj.getX(), obj.getY());
        obj.nextFrame();
        Coordinates newCoordinates = new Coordinates(obj.getX(), obj.getY());

        if (!Geometry.isAt(newCoordinates.getX(), newCoordinates.getY(), originalCoordinates.getX(), originalCoordinates.getY())) {
            getGrid(originalCoordinates).removeObject(obj);
            getGrid(newCoordinates).addObject(obj);
        }
        
        ArenaObject[] result = new ArenaObject[2];

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