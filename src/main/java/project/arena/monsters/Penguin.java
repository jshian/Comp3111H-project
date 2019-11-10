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
     * @see Monster#Monster(Arena, Coordinates, Coordinates, ImageView, double)
     */
    public Penguin(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.maxHealth = 7.5 * difficulty;
        this.maxSpeed = 3 + 0.3 * difficulty;
        this.health = this.maxHealth;
        this.speed = this.maxSpeed;
    }

    /**
     * @see Monster#Monster(Monster)
     */
    public Penguin(@NonNull Penguin other) {
        super(other);
    }
    
    @Override
    public Penguin deepCopy() {
        return new Penguin(this);
    }

    // Interface implementation
    @Override
    public void nextFrame() {
        super.nextFrame();
        this.health += 0.1;
    }
    
    @Override
    public String getClassName() { return "Penguin"; }
}