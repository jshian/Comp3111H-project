package project.monsters;

import javax.persistence.Entity;

import javafx.scene.image.ImageView;
import org.checkerframework.checker.nullness.qual.NonNull;

import project.*;

/**
 * Unicorn is the Monster with the most health.
 */
@Entity
public class Unicorn extends Monster {
    /**
     * @see Monster#Monster
     */
    public Unicorn(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination) {
        super(difficulty, start, destination);
        this.health = 10 * difficulty;
        this.speed = 0.5 + 0.005 * difficulty;
    }

    /**
     * @see Monster#Monster
     */
    public Unicorn(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination, @NonNull ImageView imageView) {
        super(difficulty, start, destination, imageView);
        this.health = 10 * difficulty;
        this.speed = 0.5 + 0.005 * difficulty;
    }

    /**
     * get class name.
     * @return class name.
     */
    public String getClassName() { return "Unicorn"; }
}