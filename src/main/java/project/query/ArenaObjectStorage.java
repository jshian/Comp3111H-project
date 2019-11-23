package project.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import project.controller.ArenaEventManager;
import project.controller.ArenaManager;
import project.entity.ArenaComparableObject;
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
 * Manages the storage of objects in the {@link ArenaInstance}.
 * @see ArenaObject
 */
@Entity
public final class ArenaObjectStorage {

    /**
     * Index for each object in the x-direction.
     */
    @Transient
    private ArrayList<LinkedList<ArenaObject>> objectsAtX = new ArrayList<>(ArenaManager.ARENA_WIDTH + 1);
    {
        for (int x = 0; x <= ArenaManager.ARENA_WIDTH; x++) {
            objectsAtX.add(new LinkedList<ArenaObject>());
        }
    }

    /**
     * Index for each object in the y-direction.
     */
    @Transient
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
     * Enum of the stored types of {@link ArenaObject} inside the storage.
     */
    public enum StoredType {

        /**
         * Refers to {@link Tower}.
         */
        TOWER (Tower.class),

        /**
         * Refers to {@link Projectile}.
         */
        PROJECTILE (Projectile.class),

        /**
         * Refers to all types of {@link Monster}.
         */
        MONSTER (Monster.class);
        

        private final Class<? extends ArenaObject> clazz;

        StoredType(Class<? extends ArenaObject> clazz) {
            this.clazz = clazz;
        }

        /**
         * Returns the class of the supported object type.
         * @return The class of the supported object type.
         */
        public Class<? extends ArenaObject> getObjectClass() { return clazz; }
    }

    /**
     * Enum of the stored types of {@link ArenaComparableObject} inside the storage.
     */
    public enum StoredComparableType {

        /**
         * Refers to all types of {@link Monster}.
         */
        MONSTER (Monster.class);
        

        private final Class<? extends ArenaComparableObject> clazz;

        StoredComparableType(Class<? extends ArenaComparableObject> clazz) {
            this.clazz = clazz;
        }

        /**
         * Returns the class of the supported object type.
         * @return The class of the supported object type.
         */
        public Class<? extends ArenaComparableObject> getObjectClass() { return clazz; }
    }

    /**
     * Sorting option for a sorted query.
     */
    public enum SortOption {

        /**
         * Results are in ascending order.
         */
        ASCENDING,

        /**
         * Results are in descending order.
         */
        DESCENDING;

    }

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
            System.err.println("The type of ArenaObject is unsupported by the storage");
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
            System.err.println("The type of ArenaObject is unsupported by the storage");
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
     * Constructs a newly allocated {@link ArenaObjectStorage} object.
     */
    public ArenaObjectStorage() {
        ArenaEventManager manager = ArenaManager.getActiveEventManager();
        manager.OBJECT_IO.subscribe(ArenaObjectIOEvent.ADD, onAddObject);
        manager.OBJECT_IO.subscribe(ArenaObjectIOEvent.REMOVE, onRemoveObject);
        manager.OBJECT_MOVE.subscribe(ArenaObjectMoveEvent.MOVE, onMoveObject);
    }

    /**
     * Returns the index for x-coordinate.
     * @return The index for x-coordinate.
     */
    ArrayList<LinkedList<ArenaObject>> getXIndex() {
        return objectsAtX;
    }

    /**
     * Returns the index for y-coordinate.
     * @return The index for y-coordinate.
     */
    ArrayList<LinkedList<ArenaObject>> getYIndex() {
        return objectsAtY;
    }

    /**
     * Returns the index for a supported object type.
     * @param type The supported object type.
     * @return The index for a supported object type.
     */
    LinkedList<? extends ArenaObject> getIndexFor(StoredType type) {
        switch (type) {
            case TOWER: return towers;
            case PROJECTILE: return projectiles;
            case MONSTER: return monsters;
        }

        return null;
    }

    /**
     * Returns the index for a supported object type.
     * @param type The supported object type.
     * @return The index for a supported object type.
     */
    LinkedList<? extends ArenaComparableObject> getIndexFor(StoredComparableType type) {
        switch (type) {
            case MONSTER: return monsters;
        }

        return null;
    }

    /**
     * Returns every stored {@link ArenaObject}.
     * @return Every stored {@link ArenaObject}.
     */
    LinkedList<ArenaObject> getAllObjects() {
        LinkedList<ArenaObject> result = new LinkedList<>();

        for (StoredType type : EnumSet.allOf(StoredType.class)) {
            result.addAll(getIndexFor(type));
        }

        return result;
    }

    /**
     * Returns every stored {@link ArenaComparableObject}.
     * @return Every stored {@link ArenaComparableObject}.
     */
    LinkedList<ArenaComparableObject> getComparableObjects() {
        LinkedList<ArenaComparableObject> result = new LinkedList<>();

        for (StoredComparableType type : EnumSet.allOf(StoredComparableType.class)) {
            result.addAll(getIndexFor(type));
        }

        return result;
    }

    /**
     * Returns the fraction of {@link ArenaObject}s that are {@link ArenaComparableObject}s.
     * @return The fraction of {@link ArenaObject}s that are {@link ArenaComparableObject}s.
     */
    float getComparableFraction() { return ((float) monsters.size()) / (towers.size() + monsters.size()); }

    /**
     * Runs a query on the storage.
     * @param selector The selector for the query.
     * @param types The types of {@link ArenaObject} to select.
     * @return The query result.
     */
    public LinkedList<ArenaObject> getQueryResult(ArenaObjectSelector selector, EnumSet<StoredType> types) {
        ArenaObjectQuery query = new ArenaObjectQuery(selector);
        return query.run(this, types);
    }

    /**
     * Runs a query on the storage.
     * @param selectors The list of selectors for the query.
     * @param types The types of {@link ArenaObject} to select.
     * @return The query result.
     */
    public LinkedList<ArenaObject> getQueryResult(LinkedList<ArenaObjectSelector> selectors, EnumSet<StoredType> types) {
        ArenaObjectQuery query = new ArenaObjectQuery(selectors);
        return query.run(this, types);
    }

    /**
     * Runs a sorted query on the storage.
     * @param selector The selector for the query.
     * @param types The types of {@link ArenaComparableObject} to select.
     * @param option The sorting option.
     * @return The query result.
     */
    public PriorityQueue<ArenaComparableObject> getSortedQueryResult(ArenaObjectSortedSelector selector, EnumSet<StoredComparableType> types, SortOption option) {
        ArenaObjectSortedQuery query = new ArenaObjectSortedQuery(selector);
        return query.run(this, types, option);
    }

    /**
     * Runs a sorted query on the storage.
     * @param selectors The list of selectors for the query.
     * @param types The types of {@link ArenaComparableObject} to select.
     * @param option The sorting option.
     * @return The query result.
     */
    public PriorityQueue<ArenaComparableObject> getSortedQueryResult(LinkedList<ArenaObjectSortedSelector> selectors, EnumSet<StoredComparableType> types, SortOption option) {
        ArenaObjectSortedQuery query = new ArenaObjectSortedQuery(selectors);
        return query.run(this, types, option);
    }
}