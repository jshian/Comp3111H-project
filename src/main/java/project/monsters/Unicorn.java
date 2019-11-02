package project.monsters;

import project.*;

/**
 * Unicorn is the Monster with the most health.
 */
public class Unicorn extends Monster {
    public Unicorn(Arena arena, double difficulty, Coordinates destination) {
        super(arena, difficulty, destination);
        this.health = 10 * difficulty;
        this.speed = 0.5 + 0.005 * difficulty;
    }
}