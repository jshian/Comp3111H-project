package project.entity;

/**
 * Singleton that creates {@link ArenaObject}s in the currently active arena.
 */
public final class ArenaObjectFactory {

    /**
     * Constructs a newly allocated {@link ArenaObjectFactory} object.
     */
    private ArenaObjectFactory() {}

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
     * Creates a Tower object and adds it to the currently active arena.
     * @param type The type of tower to create.
     * @param x The x-coordinate of the center of the tower.
     * @param y The y-coordinate of the center of the tower.
     * @return The newly-created Tower object.
     */
    public static Tower createTower(TowerType type, short x, short y) {
        switch (type) {
            case BASIC: return new BasicTower(x, y);
            case CATAPULT: return new Catapult(x, y);
            case ICE: return new IceTower(x, y);
            case LASER: return new LaserTower(x, y);
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
        if (tower instanceof BasicTower) {
            return new BasicProjectile((BasicTower) tower, target, deltaX, deltaY);
        } else if (tower instanceof IceTower) {
            return new IceProjectile((IceTower) tower, target, deltaX, deltaY);
        } else if (tower instanceof Catapult) {
            return new CatapultProjectile((Catapult) tower, target, deltaX, deltaY);
        } else if (tower instanceof LaserTower) {
            return new LaserProjectile((LaserTower) tower, target, deltaX, deltaY);
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
     * Creates a Monster object and adds it to the currently active arena.
     * @param type The type of monster to create.
     * @param x The x-coordinate of the monster.
     * @param y The x-coordinate of the monster.
     * @param difficulty The difficulty of the monster.
     * @return The newly-created Monster object.
     */
    public static Monster createMonster(MonsterType type, short x, short y, double difficulty) {
        switch (type) {
            case FOX: return new Fox(x, y, difficulty);
            case PENGUIN: return new Penguin(x, y, difficulty);
            case UNICORN: return new Unicorn(x, y, difficulty);
        }

        throw new IllegalArgumentException("The Monster type must be specified");
    }
}