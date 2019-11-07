package project.monsters;

import project.*;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;

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

    @Override
    public void recalculateFuturePath() {
        futurePath = arena.findPathToEndZone(new Coordinates(getX(), getY()), false);
    }
}