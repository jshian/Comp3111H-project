package project.entity;

import project.controller.ArenaManager;
import project.query.ArenaObjectStorage;

/**
 * Singleton that creates {@link ArenaObject}s in the currently active {@link ArenaInstance}.
 */
public final class ArenaObjectFactory {

    /**
     * Constructs a newly allocated {@link ArenaObjectFactory} object.
     */
    private ArenaObjectFactory() {}

    /**
     * An enum of supported {@link Tower} types.
     */
    public static enum TowerType {
        /**
         * The {@link BasicTower}.
         */
        BASIC,

        /**
         * The {@link Catapult}.
         */
        CATAPULT,

        /**
         * The {@link IceTower}.
         */
        ICE,

        /**
         * The {@link LaserTower}.
         */
        LASER
    }

    /**
     * Creates a Tower object and adds it to the currently active arena.
     * @param type The type of tower to create.
     * @param x The x-coordinate of the center of the tower.
     * @param y The y-coordinate of the center of the tower.
     * @return The newly-created Tower object.
     */
    public static Tower createTower(TowerType type, short x, short y) {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        switch (type) {
            case BASIC: return new BasicTower(storage, x, y);
            case CATAPULT: return new Catapult(storage, x, y);
            case ICE: return new IceTower(storage, x, y);
            case LASER: return new LaserTower(storage, x, y);
        }

        throw new IllegalArgumentException("The Tower type must be specified");
    }

    /**
     * Creates a Projectile object and adds it to the currently active arena.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     * @return The newly-created Projectile object.
     */
    public static Projectile createProjectile(Tower tower, Monster target, short deltaX, short deltaY) {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        if (tower instanceof BasicTower) {
            return new BasicProjectile(storage, (BasicTower) tower, target, deltaX, deltaY);
        } else if (tower instanceof IceTower) {
            return new IceProjectile(storage, (IceTower) tower, target, deltaX, deltaY);
        } else if (tower instanceof Catapult) {
            return new CatapultProjectile(storage, (Catapult) tower, target, deltaX, deltaY);
        } else if (tower instanceof LaserTower) {
            return new LaserProjectile(storage, (LaserTower) tower, target, deltaX, deltaY);
        } else {
            throw new IllegalArgumentException("Unknown Tower type or tower is null");
        }
    }


    /**
     * An enum of supported {@link Monster} types.
     */
    public static enum MonsterType {
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
     * Creates a Monster object and adds it to the currently active arena.
     * @param type The type of monster to create.
     * @param x The x-coordinate of the monster.
     * @param y The x-coordinate of the monster.
     * @param difficulty The difficulty of the monster.
     * @return The newly-created Monster object.
     */
    public static Monster createMonster(MonsterType type, short x, short y, double difficulty) {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        switch (type) {
            case FOX: return new Fox(storage, x, y, difficulty);
            case PENGUIN: return new Penguin(storage, x, y, difficulty);
            case UNICORN: return new Unicorn(storage, x, y, difficulty);
        }

        throw new IllegalArgumentException("The Monster type must be specified");
    }
}