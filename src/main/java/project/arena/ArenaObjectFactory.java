package project.arena;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.towers.*;
import project.arena.projectiles.*;
import project.arena.monsters.*;

/**
 * Manages the creation of objects in the {@link Arena}.
 * 
 * @see ArenaObject
 */
public final class ArenaObjectFactory {

    /**
     * The arena that the class is linked to.
     */
    private Arena arena;

    /**
     * Constructor for the ArenaObjectFactory class.
     * @param arena The arena to link this object to.
     */
    ArenaObjectFactory(Arena arena) {
        this.arena = arena;
    }

    /**
     * An enum of supported {@link Tower} types.
     */
    public static enum TowerType {
        /**
         * The {@link BasicTower}.
         */
        BasicTower,

        /**
         * The {@link Catapult}.
         */
        Catapult,

        /**
         * The {@link IceTower}.
         */
        IceTower,

        /**
         * The {@link LaserTower}.
         */
        LaserTower
    }

    /**
     * Creates a Tower object.
     * @param type The type of tower to create.
     * @param center The coordinates of the center of the tower.
     * @return The newly-created Tower object.
     */
    Tower createTower(@NonNull TowerType type, @NonNull Coordinates center) {
        ImageView iv;
        switch (type) {
            case BasicTower:
                return new BasicTower(arena, center);
            case IceTower:
                return new IceTower(arena, center);
            case Catapult:
                return new Catapult(arena, center);
            case LaserTower:
                return new LaserTower(arena, center);
        }

        throw new IllegalArgumentException("The Tower type must be specified");
    }

    /**
     * Creates a Projectile object.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     * @return The newly-created Projectile object.
     */
    Projectile createProjectile(@NonNull Tower tower, @NonNull Monster target, short deltaX, short deltaY) {
        if (tower instanceof BasicTower) {
            return new BasicProjectile(arena, (BasicTower)tower, target, deltaX, deltaY);
        } else if (tower instanceof IceTower) {
            return new IceProjectile(arena, (IceTower)tower, target, deltaX, deltaY);
        } else if (tower instanceof Catapult) {
            return new CatapultProjectile(arena, (Catapult)tower, target, deltaX, deltaY);
        } else if (tower instanceof LaserTower) {
            return new LaserProjectile(arena, (LaserTower)tower, target, deltaX, deltaY);
        } else {
            throw new IllegalArgumentException("Unknown Tower type");
        }
    }


    /**
     * An enum of supported {@link Monster} types.
     */
    public static enum MonsterType {
        /**
         * The {@link Fox}.
         */
        Fox,

        /**
         * The {@link Penguin}.
         */
        Penguin,

        /**
         * The {@link Unicorn}.
         */
        Unicorn
    }

    /**
     * Creates a Monster object.
     * @param type The type of monster to create.
     * @param start The starting location of the monster.
     * @param destination The destination of the monster.
     * @param difficulty The difficulty of the monster.
     * @return The newly-created Monster object.
     */
    Monster createMonster(@NonNull MonsterType type, @NonNull Coordinates start, @NonNull Coordinates destination, double difficulty) {
        switch (type) {
            case Fox:
                return new Fox(arena, start, destination, difficulty);
            case Penguin:
                return new Penguin(arena, start, destination, difficulty);
            case Unicorn:
                return new Unicorn(arena, start, destination, difficulty);
        }

        throw new IllegalArgumentException("The Monster type must be specified");
    }
}