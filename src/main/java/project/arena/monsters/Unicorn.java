package project.arena.monsters;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;
import project.arena.Coordinates;

/**
 * Unicorn is the Monster with the most health.
 */
@Entity
public class Unicorn extends Monster {
    /**
     * @see Monster#Monster(Arena, Coordinates, Coordinates, ImageView, double)
     */
    public Unicorn(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.maxHealth = 10 * difficulty;
        this.maxSpeed = 2 + 0.2 * difficulty;
        this.health.set(this.maxHealth);
        this.speed = this.maxSpeed;
        this.resources = (int) (difficulty * 1);
    }

    /**
     * @see Monster#Monster(Arena, Monster)
     */
    public Unicorn(@NonNull Arena arena, @NonNull Unicorn other) {
        super(arena, other);
    }
    
    @Override
    public Unicorn deepCopy(@NonNull Arena arena) {
        return new Unicorn(arena, this);
    }

    @Override
    public String getClassName() { return "Unicorn"; }
}