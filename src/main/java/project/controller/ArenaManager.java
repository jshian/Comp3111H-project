package project.controller;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
     * The active instance.
     */
    private static ArenaInstance activeInstance;

    /**
     * Copy of an instance to facilitate undisturbed saving of the game.
     * Each object within will be automatically unsubscribed from all {@link ArenaEvent}s.
     */
    private static ArenaInstance shadowInstance;

    /**
     * Constructs a newly allocated ArenaManager object.
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
        BASIC (BasicTower.class),

        /**
         * {@link Catapult}
         */
        CATAPULT (Catapult.class),

        /**
         * {@link IceTower}
         */
        ICE (IceTower.class),

        /**
         * {@link LaserTower}
         */
        LASER (LaserTower.class);

        private final Class<? extends Tower> towerType;

        TowerType(Class<? extends Tower> towerType) {
            this.towerType = towerType;
        }

        /**
         * Returns the tower type.
         * @return The tower type.
         */
        public Class<? extends Tower> getTowerType() { return towerType; }
    }
}