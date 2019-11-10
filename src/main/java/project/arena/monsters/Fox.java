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
        this.health = 5 * difficulty;
        this.speed = 5 + 0.5 * difficulty;
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