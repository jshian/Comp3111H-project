package project.arena.monsters;

import javax.persistence.Entity;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;

/**
 * Fox is the fastest Monster. It also tries to follow the path where it receives the minimum number of attacks from Towers.
 */
@Entity
public class Fox extends Monster {
    /**
     * @see Monster#Monster(Arena, Coordinates, Coordinates, double)
     */
    public Fox(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, double difficulty) {
        super(arena, start, destination, difficulty);
        this.maxHealth = 5 + 2 * difficulty;
        this.maxSpeed = 5 + 0.5 * Math.log10(difficulty);
        this.health.set(this.maxHealth);
        this.speed = this.maxSpeed;
        this.resources = (int) (difficulty * 1.5);
        this.imageView = new ImageView(new Image("/fox.png", UIController.GRID_WIDTH / 4, UIController.GRID_HEIGHT / 4, true, true));
        this.coordinates.bindByImage(this.imageView);
        hoverMonsterEvent(this.arena);
    }

    /**
     * @see Monster#Monster(Arena, Monster)
     */
    public Fox(@NonNull Arena arena, @NonNull Fox other) {
        super(arena, other);
    }
    
    @Override
    public Fox deepCopy(@NonNull Arena arena) {
        return new Fox(arena, this);
    }

    @Override
    public String getClassName() { return "Fox"; }

    @Override
    public Coordinates findNextCoordinates() { return arena.findNextTowardsEnd_prioritizeAttack(coordinates); }
}