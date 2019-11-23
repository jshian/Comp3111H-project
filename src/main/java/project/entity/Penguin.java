package project.entity;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.ArenaInstance;
import project.arena.Coordinates;

/**
 * Penguin has the ability to regenerate.
 */
@Entity
public class Penguin extends Monster {
    /**
     * The regeneration rate of the Penguin in terms of health per frame.
     */
    private static double REGENERATION_RATE = 0.1;
    
    /**
     * @see Monster#Monster(ArenaInstance, Coordinates, Coordinates, ImageView, double)
     */
    public Penguin(ArenaInstance arena, Coordinates start, Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.maxHealth = 7.5 + 3 * difficulty;
        this.maxSpeed = 3 + 0.3 * Math.log10(difficulty);
        this.health.set(this.maxHealth);
        this.speed = this.maxSpeed;
        this.resources = (int) (difficulty * 1.25);
    }

    /**
     * @see Monster#Monster(ArenaInstance, Monster)
     */
    public Penguin(ArenaInstance arena, Penguin other) {
        super(arena, other);
    }
    
    @Override
    public Penguin deepCopy(ArenaInstance arena) {
        return new Penguin(arena, this);
    }

    // Interface implementation
    @Override
    public void nextFrame() {
        super.nextFrame();
        this.health.set(getHealth() + REGENERATION_RATE);
    }
    
    @Override
    public String getClassName() { return "Penguin"; }
}