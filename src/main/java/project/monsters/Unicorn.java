package project.monsters;

import project.*;

/**
 * Unicorn is the Monster with the most health.
 */
public class Unicorn extends Monster {
    /**
     * @see Monster#Monster()
     */
    public Unicorn(double difficulty, Coordinates destination) {
        super(difficulty, destination);
        this.health = 10 * difficulty;
        this.speed = 0.5 + 0.005 * difficulty;
    }
}