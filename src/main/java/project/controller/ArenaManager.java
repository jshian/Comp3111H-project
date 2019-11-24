package project.controller;

import org.checkerframework.checker.nullness.qual.Nullable;

import project.Player;
import project.arena.ArenaInstance;
import project.entity.BasicTower;
import project.entity.Catapult;
import project.entity.IceTower;
import project.entity.LaserTower;
import project.entity.Tower;

/**
 * Manager of the arena.
 */
public final class ArenaManager {

    /**
     * Width of the arena.
     */
    public static short ARENA_WIDTH = 480;

    /**
     * Height of the arena.
     */
    public static short ARENA_HEIGHT = 480;

    /**
     * Width of each grid.
     */
    public static short GRID_WIDTH = 40;

    /**
     * Height of each grid.
     */
    public static short GRID_HEIGHT = 40;

    /**
     * x-coordinate of starting location for each spawned {@link Monster}.
     */
    public static short STARTING_X = 0;

    /**
     * y-coordinate of starting location for each spawned {@link Monster}.
     */
    public static short STARTING_Y = 0;

    /**
     * x-coordinate of end zone for each spawned {@link Monster}.
     */
    public static short END_X = 480;

    /**
     * y-coordinate of end zone for each spawned {@link Monster}.
     */
    public static short END_Y = 0;

    /**
     * Interval between {@link Monster} spawning, in terms of number of frames.
     */
    static int WAVE_INTERVAL = 50;

    /**
     * The active player.
     */
    @Nullable
    private static Player activePlayer = null;

    /**
     * The active event register.
     */
    private static ArenaEventRegister activeEventRegister = new ArenaEventRegister();

    /**
     * The active scalar field register.
     */
    private static ArenaScalarFieldRegister activeScalarFieldRegister = new ArenaScalarFieldRegister();

    /**
     * The active arena instance.
     */
    private static ArenaInstance activeArenaInstance;

    /**
     * Copy of an arena instance to facilitate undisturbed saving of the game.
     * Each object within will be automatically unsubscribed from all {@link ArenaEvent}s.
     */
    private static ArenaInstance shadowArenaInstance;

    /**
     * Constructs a newly allocated {@link ArenaManager} object.
     */
    private ArenaManager() {
        assert ARENA_WIDTH % GRID_WIDTH == 0;
        assert ARENA_HEIGHT % GRID_HEIGHT == 0;
        assert STARTING_X >= 0 && STARTING_X <= ARENA_WIDTH;
        assert STARTING_Y >= 0 && STARTING_Y <= ARENA_HEIGHT;
        assert END_X >= 0 && END_X <= ARENA_WIDTH;
        assert END_Y >= 0 && END_Y <= ARENA_HEIGHT;
    }

    /**
     * Archives the currently active event register, setting the active instance to a new one.
     */
    private static void archive() {
        activeEventRegister = new ArenaEventRegister();
    }

    // TODO
    public static void save() {}

    // TODO
    public static void load() {}

    /**
     * Enum of available {@link Tower} types for the arena.
     */
    enum TowerType {
        /**
         * {@link BasicTower}
         */
        BASIC (BasicTower.class, BasicTower.getBuildingCost()),

        /**
         * {@link Catapult}
         */
        CATAPULT (Catapult.class, Catapult.getBuildingCost()),

        /**
         * {@link IceTower}
         */
        ICE (IceTower.class, IceTower.getBuildingCost()),

        /**
         * {@link LaserTower}
         */
        LASER (LaserTower.class, LaserTower.getBuildingCost());

        private final Class<? extends Tower> towerType;
        private final int buildingCost;

        TowerType(Class<? extends Tower> towerType, int buildingCost) {
            this.towerType = towerType;
            this.buildingCost = buildingCost;
        }

        /**
         * Returns the tower type.
         * @return The tower type.
         */
        public Class<? extends Tower> getTowerType() { return towerType; }

        /**
         * Returns the building cost.
         * @return The building cost.
         */
        public int getBuildingCost() { return buildingCost; }
    }

    /**
     * Returns the active player.
     * @return The active player.
     */
    public static Player getActivePlayer() { return activePlayer; }

    /**
     * Returns the active event register.
     * @return The active event register.
     */
    public static ArenaEventRegister getActiveEventRegister() { return activeEventRegister; }

    /**
     * Returns the active scalar field register.
     * @return The active scalar field register.
     */
    public static ArenaScalarFieldRegister getActiveScalarFieldRegister() { return activeScalarFieldRegister; }

    /**
     * Returns the active arena instance.
     * @return The active arena instance.
     */
    public static ArenaInstance getActiveArenaInstance() { return activeArenaInstance; }

    /**
     * Sets the active player.
     * @param player The new active player.
     */
    public static Player setActivePlayer(Player player) { return activePlayer; }

}