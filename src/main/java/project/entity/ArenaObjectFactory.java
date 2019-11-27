package project.entity;

import project.control.ArenaManager;
import project.event.eventargs.ArenaObjectEventArgs;

/**
 * Singleton that creates {@link ArenaObject}s in the currently active arena.
 */
public final class ArenaObjectFactory {

    /**
     * Constructs a newly allocated {@link ArenaObjectFactory} object.
     */
    private ArenaObjectFactory() {}

    /**
     * Adds an {@link ArenaObject} to the currently active arena on the creator's behalf.
     * @param creator The object which creates the ArenaObject object.
     * @param o The object to be added.
     * @throws IllegalStateException If the object is already attached to an arena.
     */
    private static void addToArena(Object creator, ArenaObject o) throws IllegalStateException {
        if (!o.subscribeEvents()) {
            throw new IllegalStateException("The object is already attached to an arena");
        }

        ArenaManager.getActiveEventRegister().ARENA_OBJECT_ADD.invoke(creator,
                new ArenaObjectEventArgs() {
                    { subject = o; }
                }
        );
    }

    /**
     * An enum of supported {@link Tower} types.
     */
    public enum TowerType {
        /**
         * The {@link BasicTower}.
         */
        BASIC (BasicTower.class.getSimpleName(), BasicTower.getBuildingCost()),

        /**
         * The {@link Catapult}.
         */
        CATAPULT (Catapult.class.getSimpleName(), Catapult.getBuildingCost()),

        /**
         * The {@link IceTower}.
         */
        ICE (IceTower.class.getSimpleName(), IceTower.getBuildingCost()),

        /**
         * The {@link LaserTower}.
         */
        LASER (LaserTower.class.getSimpleName(), LaserTower.getBuildingCost());

        private final String defaultName;
        private final int buildingCost;

        private TowerType(String defaultName, int buildingCost) {
            this.defaultName = defaultName;
            this.buildingCost = buildingCost;
        }

        /**
         * Returns the default name of the tower type.
         * @return The default name of the tower type.
         */
        public String getDefaultName() { return defaultName; }

        /**
         * Returns the building cost of the tower type.
         * @return The building cost of the tower type.
         */
        public int getBuildingCost() { return buildingCost; }
    }

    /**
     * Creates a {@link Tower} object and adds it to the currently active arena on the creator's behalf.
     * @param creator The object which creates the Tower object.
     * @param type The type of tower to create.
     * @param x The x-coordinate of the center of the tower.
     * @param y The y-coordinate of the center of the tower.
     * @return The newly-created Tower object.
     */
    public static Tower createTower(Object creator, TowerType type, short x, short y) {
        Tower t;
        switch (type) {
            case BASIC: t = new BasicTower(x, y); addToArena(creator, t); return t;
            case CATAPULT: t = new Catapult(x, y); addToArena(creator, t); return t;
            case ICE: t = new IceTower(x, y); addToArena(creator, t); return t;
            case LASER: t = new LaserTower(x, y); addToArena(creator, t); return t;
        }

        throw new IllegalArgumentException("The Tower type must be specified");
    }

    /**
     * Creates a {@link Projectile} object and adds it to the currently active arena on the creator's behalf.
     * @param creator The object which creates the Projectile object.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     * @return The newly-created Projectile object.
     */
    public static Projectile createProjectile(Object creator, Tower tower, Monster target, short deltaX, short deltaY) {
        Projectile p;
        if (tower instanceof BasicTower) {
            p = new BasicProjectile((BasicTower) tower, target, deltaX, deltaY);
            addToArena(creator, p);
            return p;
        } else if (tower instanceof IceTower) {
            p = new IceProjectile((IceTower) tower, target, deltaX, deltaY);
            addToArena(creator, p);
            return p;
        } else if (tower instanceof Catapult) {
            p = new CatapultProjectile((Catapult) tower, target, deltaX, deltaY);
            addToArena(creator, p);
            return p;
        } else if (tower instanceof LaserTower) {
            p = new LaserProjectile((LaserTower) tower, target, deltaX, deltaY);
            addToArena(creator, p);
            return p;
        } else {
            throw new IllegalArgumentException("Unknown Tower type or tower is null");
        }
    }


    /**
     * An enum of supported {@link Monster} types.
     */
    public enum MonsterType {
        /**
         * The {@link Fox}.
         */
        FOX,

        /**
         * The {@link Penguin}.
         */
        PENGUIN,

        /**
         * The {@link Unicorn}.
         */
        UNICORN
    }

    /**
     * Creates a {@link Monster} object and adds it to the currently active arena on the creator's behalf.
     * @param creator The object which creates the Monster object.
     * @param type The type of monster to create.
     * @param x The x-coordinate of the monster.
     * @param y The x-coordinate of the monster.
     * @param difficulty The difficulty of the monster.
     * @return The newly-created Monster object.
     */
    public static Monster createMonster(Object creator, MonsterType type, short x, short y, double difficulty) {
        Monster m;
        switch (type) {
            case FOX: m = new Fox(x, y, difficulty); addToArena(creator, m); return m;
            case PENGUIN: m = new Penguin(x, y, difficulty); addToArena(creator, m); return m;
            case UNICORN: m = new Unicorn(x, y, difficulty); addToArena(creator, m); return m;
        }

        throw new IllegalArgumentException("The Monster type must be specified");
    }

    /**
     * Removes an {@link ArenaObject} from the currently active arena on the disposer's behalf.
     * @param disposer The object which removes the ArenaObject object.
     * @param o The object to be removed.
     * @throws IllegalStateException If the object is not attached to an arena.
     */
    public static void removeObject(Object disposer, ArenaObject o) throws IllegalStateException {
        if (!o.unsubscribeEvents()) {
            throw new IllegalStateException("The object is not attached to an arena");
        }
        
        ArenaManager.getActiveEventRegister().ARENA_OBJECT_REMOVE.invoke(disposer,
            new ArenaObjectEventArgs() {
                { subject = o; }
            }
        );
    }
}