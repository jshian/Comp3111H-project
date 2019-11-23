package project.entity;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.ArenaInstance;
import project.arena.Coordinates;

/**
 * Fox is the fastest Monster. It also tries to follow the path where it receives the minimum number of attacks from Towers.
 */
@Entity
public class Fox extends Monster {
    /**
     * @see Monster#Monster(ArenaInstance, Coordinates, Coordinates, ImageView, double)
     */
    public Fox(ArenaInstance arena, Coordinates start, Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.maxHealth = 5 + 2 * difficulty;
        this.maxSpeed = 5 + 0.5 * Math.log10(difficulty);
        this.health.set(this.maxHealth);
        this.speed = this.maxSpeed;
        this.resources = (int) (difficulty * 1.5);
    }

    /**
     * @see Monster#Monster(ArenaInstance, Monster)
     */
    public Fox(ArenaInstance arena, Fox other) {
        super(arena, other);
    }
    
    @Override
    public Fox deepCopy(ArenaInstance arena) {
        return new Fox(arena, this);
    }

    @Override
    public String getClassName() { return "Fox"; }

    @Override
    public Coordinates findNextCoordinates() { return arena.findNextTowardsEnd_prioritizeAttack(coordinates); }
}