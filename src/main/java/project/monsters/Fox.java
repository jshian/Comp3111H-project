package project.monsters;

import javafx.scene.image.ImageView;
import project.*;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;


/**
 * Fox is the fastest Monster. It also tries to follow the path where it receives the minimum number of attacks from Towers.
 */
@Entity
public class Fox extends Monster {
    /**
     * @see Monster#Monster(Arena, Coordinates, Coordinates, ImageView, double)
     */
    public Fox(Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.health = 5 * difficulty;
        this.speed = 1 + 0.01 * difficulty;
    }

    /**
     * @see Monster#Monster(Monster)
     */
    public Fox(Fox other) {
        super(other);
    }
    
    @Override
    public Fox deepCopy() {
        return new Fox(this);
    }

    // Inferface implementation
    @Override
    public void nextFrame() {
        Coordinates nextCoordinates = arena.findNextTowardsEnd(coordinates, false);
        if (nextCoordinates != null) coordinates.update(nextCoordinates);
    }

    @Override
    public String getClassName() { return "Fox"; }


}