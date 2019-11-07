package project.monsters;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.*;

/**
 * Penguin has the ability to regenerate.
 */
@Entity
public class Penguin extends Monster {
    /**
     * @see Monster#Monster(Arena, Coordinates, Coordinates, ImageView, double)
     */
    public Penguin(Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.health = 7.5 * difficulty;
        this.speed = 0.75 + 0.0075 * difficulty;
    }

    @Override
    public void MoveOneFrame() {
        super.MoveOneFrame();
        this.health += 0.1;
    }
}