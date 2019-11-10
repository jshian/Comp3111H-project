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
        this.health = 10 * difficulty;
        this.speed = 2 + 0.2 * difficulty;
    }

    /**
     * @see Monster#Monster(Monster)
     */
    public Unicorn(@NonNull Unicorn other) {
        super(other);
    }
    
    @Override
    public Unicorn deepCopy() {
        return new Unicorn(this);
    }

    @Override
    public String getClassName() { return "Unicorn"; }
}