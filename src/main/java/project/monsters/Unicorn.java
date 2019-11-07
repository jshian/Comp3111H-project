package project.monsters;

import javax.persistence.Entity;

import javafx.scene.image.ImageView;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.*;

/**
 * Unicorn is the Monster with the most health.
 */
@Entity
public class Unicorn extends Monster {
    /**
     * @see Monster#Monster(Arena, Coordinates, Coordinates, ImageView, double)
     */
    public Unicorn(Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.health = 10 * difficulty;
        this.speed = 0.5 + 0.005 * difficulty;
    }

    /**
     * get class name.
     * @return class name.
     */
    public String getClassName() { return "Unicorn"; }
}