package project.arena.monsters;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;
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
     * @see Monster#Monster(Arena, Coordinates, Coordinates, ImageView, double)
     */
    public Penguin(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.maxHealth = 7.5 * difficulty;
        this.maxSpeed = 3 + 0.3 * difficulty;
        this.health.set(this.maxHealth);
        this.speed = this.maxSpeed;
        this.resources = (int) (difficulty * 1.25);
    }

    /**
     * @see Monster#Monster(Arena, Monster)
     */
    public Penguin(@NonNull Arena arena, @NonNull Penguin other) {
        super(arena, other);
    }
    
    @Override
    public Penguin deepCopy(@NonNull Arena arena) {
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