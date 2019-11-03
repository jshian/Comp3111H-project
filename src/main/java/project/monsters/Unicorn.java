package project.monsters;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.*;

/**
 * Unicorn is the Monster with the most health.
 */
public class Unicorn extends Monster {
    /**
     * @see Monster#Monster()
     */
    public Unicorn(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination) {
        super(difficulty, start, destination);
        this.health = 10 * difficulty;
        this.speed = 0.5 + 0.005 * difficulty;
    }
}