package project.arena.monsters;

import javax.persistence.Entity;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;

/**
 * Unicorn is the Monster with the most health.
 */
@Entity
public class Unicorn extends Monster {
    /**
     * @see Monster#Monster(Arena, Coordinates, Coordinates, double)
     */
    public Unicorn(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, double difficulty) {
        super(arena, start, destination, difficulty);
        this.maxHealth = 10 + 1 * difficulty;
        this.maxSpeed = 2 + 0.2 * Math.log10(difficulty);
        this.health.set(this.maxHealth);
        this.speed = this.maxSpeed;
        this.resources = (int) (difficulty * 1);
        this.imageView = new ImageView(new Image("/unicorn.png", UIController.GRID_WIDTH / 4, UIController.GRID_HEIGHT / 4, true, true));
        this.coordinates.bindByImage(this.imageView);
        hoverMonsterEvent(this.arena);
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