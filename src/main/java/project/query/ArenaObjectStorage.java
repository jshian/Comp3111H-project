package project.query;

import java.util.*;

import javax.persistence.*;

import project.arena.ArenaEventRegister;
import project.arena.ArenaInstance;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;

/**
 * Manages the storage of objects in the {@link ArenaInstance}.
 * 
 * Add or remove objects from this storage by invoking the events in {@link ArenaManager#getActiveEventRegister()}.
 */
@Entity(name="ArenaObjectStorage")
public final class ArenaObjectStorage {

    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @JoinTable(name="ArenaObjectStorageTowers")
    private List<Tower> towers = new LinkedList<>();

    /**
     * Index for each {@link Projectile} on the arena.
     */
    @OneToMany
    @JoinTable(name="ArenaObjectStorageProjectiles")
    private List<Projectile> projectiles = new LinkedList<>();

    /**
     * Index for each {@link Monster} on the arena.
     */
    @OneToMany
    @JoinTable(name="ArenaObjectStorageMonsters")
    private List<Monster> monsters = new LinkedList<>();

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
     * Enum of the stored types of comparable {@link ArenaObject} inside the storage.
     */
    public enum StoredComparableType {

        /**
         * Refers to all types of {@link Monster}.
         */
        MONSTER (Monster.class);
        

        private final Class<?> clazz;

        StoredComparableType(Class<?> clazz) {
            this.clazz = clazz;
        }

        /**
         * Returns the class of the supported object type.
         * @param <T> The type of comparable {@link ArenaObject}.
         * @return The class of the supported object type.
         */
        @SuppressWarnings("unchecked")
        public <T extends ArenaObject & Comparable <T>> Class<T> getObjectClass() {
            return (Class<T>) clazz;
        }
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
     * The method invoked when an {@link ArenaObject} is being added.
     */
    @Transient
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
     * The method invoked when an {@link ArenaObject} is being removed.
     */
    @Transient
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
     * The method invoked when an {@link ArenaObject} is scheduled to be moved.
     */
    @Transient
    private EventHandler<ArenaObjectEventArgs> onStartMoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;
        short x = subject.getX();
        short y = subject.getY();

        // Remove from position-based index
        LinkedList<ArenaObject> xList_old = objectsAtX.get(x);
        assert (xList_old.contains(subject));
        xList_old.remove(subject);

        LinkedList<ArenaObject> yList_old = objectsAtY.get(y);
        assert (yList_old.contains(subject));
        yList_old.remove(subject);
    };

    /**
     * The method invoked when an {@link ArenaObject} has been moved.
     */
    @Transient
    private EventHandler<ArenaObjectEventArgs> onEndMoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;
        short x = subject.getX();
        short y = subject.getY();

        // Add to position-based index
        LinkedList<ArenaObject> xList_new = objectsAtX.get(x);
        assert (!xList_new.contains(subject));
        xList_new.add(subject);

        LinkedList<ArenaObject> yList_new = objectsAtY.get(y);
        assert (!yList_new.contains(subject));
        yList_new.add(subject);
    };

    /**
     * Default constructor.
     */
    public ArenaObjectStorage() {}

    @PostLoad
    public void registerMoves() {
        ArenaEventRegister register = ArenaManager.getActiveEventRegister();
        register.ARENA_OBJECT_ADD.subscribe(onAddObject);
        register.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
        register.ARENA_OBJECT_MOVE_START.subscribe(onStartMoveObject);
        register.ARENA_OBJECT_MOVE_END.subscribe(onEndMoveObject);
    }

    /**
     * Constructs a newly allocated {@link ArenaObjectStorage} object and attaches it to an arena instance.
     * @param arenaInstance The arena instance.
     */
    public ArenaObjectStorage(ArenaInstance arenaInstance) {
        ArenaEventRegister register = arenaInstance.getEventRegister();
        register.ARENA_OBJECT_ADD.subscribe(onAddObject);
        register.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
        register.ARENA_OBJECT_MOVE_START.subscribe(onStartMoveObject);
        register.ARENA_OBJECT_MOVE_END.subscribe(onEndMoveObject);
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
     * Returns the index for a supported {@link ArenaObject} type.
     * @param type The supported object type.
     * @return The index for a supported object type.
     */
    LinkedList<? extends ArenaObject> getIndexFor(StoredType type) {
        switch (type) {
            case TOWER: return (LinkedList<Tower>) towers;
            case PROJECTILE: return (LinkedList<Projectile>) projectiles;
            case MONSTER: return (LinkedList<Monster>) monsters;
        }

        return null;
    }

    /**
     * Returns the index for a supported compoarable {@link ArenaObject} type.
     * @param type The supported comparable object type.
     * @return The index for a supported comparable object type.
     */
    @SuppressWarnings("unchecked")
    <T extends ArenaObject & Comparable <T>> LinkedList<T> getIndexFor(StoredComparableType type) {
        switch (type) {
            //case MONSTER: return (LinkedList<T>) monsters;
        }

        return null;
    }

    /**
     * Returns every stored {@link ArenaObject}.
     * @return Every stored {@link ArenaObject}.
     */
    private LinkedList<ArenaObject> getAllObjects() {
        LinkedList<ArenaObject> result = new LinkedList<>();

        for (StoredType type : EnumSet.allOf(StoredType.class)) {
            result.addAll(getIndexFor(type));
        }

        return result;
    }

    /**
     * Returns every stored comparable {@link ArenaObject}.
     * @return Every stored comparable {@link ArenaObject}.
     */
    private LinkedList<ArenaObject> getComparableObjects() {
        LinkedList<ArenaObject> result = new LinkedList<>();

        for (StoredComparableType type : EnumSet.allOf(StoredComparableType.class)) {
            result.addAll(getIndexFor(type));
        }

        return result;
    }

    /**
     * Returns the fraction of {@link ArenaObject}s that are {@link ComparableArenaObject}s.
     * @return The fraction of {@link ArenaObject}s that are {@link ComparableArenaObject}s.
     */
    float getComparableFraction() { return ((float) getComparableObjects().size()) / (getAllObjects().size()); }

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
     * @param <T> The type of comparable {@link ArenaObject}.
     * @param selector The selector for the query.
     * @param type The type of comparable {@link ArenaObject} to select.
     * @param option The sorting option.
     * @return The query result.
     */
    public <T extends ArenaObject & Comparable<T>> PriorityQueue<T> getSortedQueryResult(ArenaObjectSortedSelector<T> selector, StoredComparableType type, SortOption option) {
        ArenaObjectSortedQuery<T> query = new ArenaObjectSortedQuery<>(selector);
        return query.run(this, type, option);
    }

    /**
     * Runs a sorted query on the storage.
     * @param <T> The type of comparable {@link ArenaObject}.
     * @param selectors The list of selectors for the query.
     * @param type The type of comparable {@link ArenaObject} to select.
     * @param option The sorting option.
     * @return The query result.
     */
    public <T extends ArenaObject & Comparable<T>> PriorityQueue<T> getSortedQueryResult(LinkedList<ArenaObjectSortedSelector<T>> selectors, StoredComparableType type, SortOption option) {
        ArenaObjectSortedQuery<T> query = new ArenaObjectSortedQuery<>(selectors);
        return query.run(this, type, option);
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }
}