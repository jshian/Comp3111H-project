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
    
    // TODO
    /**
     * Loads an arena instance.
     * @param arenaInstance The arena instance. If <code>null</codel>, generates a new instance.
     */
    public static void load(@Nullable ArenaInstance arenaInstance) {

    }

    // TODO
    // First disconnect the currently active event register such that nextFrame will be called on a new register, effectively deactiving the old instance.
    // Then create shadow instance by deep copying (the new objects will be automatically connected to the new event register), and save it using a different thread.
    public static void save() {
        
    }

    /**
     * Returns the maximum number of grids in the x-direction.
     * @return The maximum number of grids in the x-direction.
     */
    public static short getMaxHorizontalGrids() {
        return (short) (ARENA_WIDTH / GRID_WIDTH);
    }

    /**
     * Returns the maximum number of grids in the y-direction.
     * @return The maximum number of grids in the y-direction.
     */
    public static short getMaxVerticalGrids() {
        return (short) (ARENA_HEIGHT / GRID_HEIGHT);
    }

    /**
     * Returns the x-position of the starting grid.
     * @return The x-position of the starting grid.
     */
    public static short getStartingGridXPos() {
        return (short) ((STARTING_X / GRID_WIDTH) * GRID_WIDTH);
    }

      /**
     * Returns the y-position of the starting grid.
     * @return The y-position of the starting grid.
     */
    public static short getStartingGridYPos() {
        return (short) ((STARTING_Y / GRID_HEIGHT) * GRID_HEIGHT);
    }

    /**
     * Returns the x-position of the end grid.
     * @return The x-position of the end grid.
     */
    public static short getEndGridXPos() {
        return (short) ((END_X / GRID_WIDTH) * GRID_WIDTH);
    }

      /**
     * Returns the y-position of the end grid.
     * @return The y-position of the end grid.
     */
    public static short getEndGridYPos() {
        return (short) ((END_Y / GRID_HEIGHT) * GRID_HEIGHT);
    }

    /**
     * Sets the active player.
     * @param player The new active player.
     */
    public static Player setActivePlayer(Player player) { return activePlayer; }

}