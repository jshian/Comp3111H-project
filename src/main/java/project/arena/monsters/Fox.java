package project.arena.monsters;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;
import project.arena.Coordinates;

/**
 * Fox is the fastest Monster. It also tries to follow the path where it receives the minimum number of attacks from Towers.
 */
@Entity
public class Fox extends Monster {
    /**
     * @see Monster#Monster(Arena, Coordinates, Coordinates, ImageView, double)
     */
    public Fox(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.maxHealth = 5 * difficulty;
        this.maxSpeed = 5 + 0.5 * difficulty;
        this.health.set(this.maxHealth);
        this.speed = this.maxSpeed;
        this.resources = (int) (difficulty * 1.5);
    }

    /**
     * @see Monster#Monster(Monster)
     */
    public Fox(@NonNull Fox other) {
        super(other);
    }
    
    @Override
    public Fox deepCopy() {
        return new Fox(this);
    }

    @Override
    public String getClassName() { return "Fox"; }

    @Override
    public Coordinates findNextCoordinates() { return arena.findNextTowardsEnd_prioritizeAttack(coordinates); }
}