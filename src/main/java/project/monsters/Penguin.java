package project.monsters;

import javax.persistence.Entity;

import javafx.scene.image.ImageView;
import org.checkerframework.checker.nullness.qual.NonNull;

import project.*;

/**
 * Penguin has the ability to regenerate.
 */
@Entity
public class Penguin extends Monster {
    /**
     * @see Monster#Monster
     */
    public Penguin(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination) {
        super(difficulty, start, destination);
        this.health = 7.5 * difficulty;
        this.speed = 0.75 + 0.0075 * difficulty;
    }

    /**
     * @see Monster#Monster
     */
    public Penguin(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination, @NonNull ImageView imageView) {
        super(difficulty, start, destination, imageView);
        this.health = 7.5 * difficulty;
        this.speed = 0.75 + 0.0075 * difficulty;
    }

    /**
     * get class name.
     * @return class name.
     */
    @Override
    public String getClassName() { return "Penguin"; }

    /**
     * get the coordinate of monster in next frame.
     * @return coordinate of monster in next frame.
     */
    @Override
    public Coordinates getNextFrame() {
        this.health += 0.1;
        return !futurePath.isEmpty() ? futurePath.removeFirst() : null;
    }

    @Override
    public void moveOneFrame() {
        super.moveOneFrame();
        this.health += 0.1;
    }
}