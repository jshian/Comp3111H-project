package project.control;

import project.Player;
import project.arena.ArenaEventRegister;
import project.arena.ArenaInstance;
import project.arena.ArenaScalarFieldRegister;
import project.entity.Monster;
import project.query.ArenaObjectStorage;
import project.ui.UIController;

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
    public static int WAVE_INTERVAL = 50;

    /**
     * The active UI controller.
     */
    private static UIController activeUIController;

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
     * Returns the active UI controller.
     * @return The active UI controller.
     */
    public static UIController getActiveUIController() { return activeUIController; }

    /**
     * Returns the active arena instance.
     * @return The active arena instance.
     */
    public static ArenaInstance getActiveArenaInstance() { return activeArenaInstance; }

    /**
     * Returns the active player.
     * @return The active player.
     */
    public static Player getActivePlayer() {
        if (getActiveArenaInstance() == null) {
            throw new NullPointerException("The ArenaManager has not set up an active arena yet");
        }

        return activeArenaInstance.getPlayer();
    }

    /**
     * Returns the active event register.
     * @return The active event register.
     */
    public static ArenaEventRegister getActiveEventRegister() {
        if (getActiveArenaInstance() == null) {
            throw new NullPointerException("The ArenaManager has not set up an active arena yet");
        }

        return activeArenaInstance.getEventRegister();
    }

    /**
     * Returns the active scalar field register.
     * @return The active scalar field register.
     */
    public static ArenaScalarFieldRegister getActiveScalarFieldRegister() {
        if (getActiveArenaInstance() == null) {
            throw new NullPointerException("The ArenaManager has not set up an active arena yet");
        }

        return activeArenaInstance.getScalarFieldRegister();
    }

    /**
     * Returns the active arena instance.
     * @return The active arena instance.
     */
    public static ArenaObjectStorage getActiveObjectStorage() {
        if (getActiveArenaInstance() == null) {
            throw new NullPointerException("The ArenaManager has not set up an active arena yet");
        }
        
        return activeArenaInstance.getStorage();
    }
    
    /**
     * Loads a brand new arena instance.
     * @param ui The UI controller of the arena instance.
     * @param player The player of the arena instance.
     */
    public static void loadNew(UIController ui, Player player) {
        activeUIController = ui;
        activeArenaInstance = new ArenaInstance(player);
    }

    // TODO
    /**
     * Loads an arena instance.
     * @param ui The UI controller of the arena instance.
     * @param player The player of the arena instance.
     * @param arenaInstance The arena instance.
     */
    public static void load(UIController ui, Player player, ArenaInstance arenaInstance) {
        activeUIController = ui;
    }

    // TODO
    // Create a shadow instance of the currently active arena instance (deep copy).
    // As it is not accessible from outside, it will not receive any events.
    // You can use another thread safely at this point.
    /**
     * Saves the currently active arena instance.
     * @param player The player of the arena instance.
     */
    public static void save(Player player) {
        Manager.save(activeArenaInstance);
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
        return (short) Math.min(STARTING_X / GRID_WIDTH, getMaxHorizontalGrids() - 1);
    }

      /**
     * Returns the y-position of the starting grid.
     * @return The y-position of the starting grid.
     */
    public static short getStartingGridYPos() {
        return (short) Math.min(STARTING_Y / GRID_HEIGHT, getMaxVerticalGrids() - 1);
    }

    /**
     * Returns the x-position of the end grid.
     * @return The x-position of the end grid.
     */
    public static short getEndGridXPos() {
        return (short) Math.min(END_X / GRID_WIDTH, getMaxHorizontalGrids() - 1);
    }

    /**
     * Returns the y-position of the end grid.
     * @return The y-position of the end grid.
     */
    public static short getEndGridYPos() {
        return (short) Math.min(END_Y / GRID_HEIGHT, getMaxVerticalGrids() - 1);
    }

    /**
     * Returns the x-position of the grid containing a point.
     * @param x The x-coordinate of the point.
     * @return The x-position of the grid containing a point.
     */
    public static short getGridXPosFromCoor(short x) {
        return (short) Math.min(x / GRID_WIDTH, getMaxHorizontalGrids() - 1);
    }

    /**
     * Returns the y-position of the grid containing a point.
     * @param y The y-coordinate of the point.
     * @return The y-position of the grid containing a point.
     */
    public static short getGridYPosFromCoor(short y) {
        return (short) Math.min(y / GRID_HEIGHT, getMaxVerticalGrids() - 1);
    }

    /**
     * Returns the x-coordinate of the left edge of the grid containing a point.
     * @param x The x-coordinate of the point.
     * @return The x-coordinate of the left edge of the grid containing a point.
     */
    public static short getGridLeftXFromCoor(short x) {
        return (short) (getGridXPosFromCoor(x) * GRID_WIDTH);
    }

    /**
     * Returns the x-coordinate of the center of the grid containing a point.
     * @param x The x-coordinate of the point.
     * @return The x-coordinate of the center of the grid containing a point.
     */
    public static short getGridCenterXFromCoor(short x) {
        return (short) ((getGridXPosFromCoor(x) + 0.5) * GRID_WIDTH);
    }

    /**
     * Returns the x-coordinate of the center of a grid.
     * @param xPos The x-position of the grid.
     * @return The x-coordinate of the center of the gridt.
     */
    public static short getGridCenterXFromPos(short xPos) {
        return (short) ((xPos + 0.5) * GRID_WIDTH);
    }

    /**
     * Returns the y-coordinate of the top edge of the grid containing a point.
     * @param y The y-coordinate of the point.
     * @return The y-coordinate of the top edge of the grid containing a point.
     */
    public static short getGridTopYFromCoor(short y) {
        return (short) (getGridXPosFromCoor(y) * GRID_HEIGHT);
    }

    /**
     * Returns the y-coordinate of the center of the grid containing a point.
     * @param y The y-coordinate of the point.
     * @return The y-coordinate of the center of the grid containing a point.
     */
    public static short getGridCenterYFromCoor(short y) {
        return (short) ((getGridXPosFromCoor(y) + 0.5) * GRID_HEIGHT);
    }

    /**
     * Returns the y-coordinate of the center of a grid.
     * @param yPos The y-position of the grid.
     * @return The y-coordinate of the center of the gridt.
     */
    public static short getGridCenterYFromPos(short yPos) {
        return (short) ((yPos + 0.5) * GRID_HEIGHT);
    }
}