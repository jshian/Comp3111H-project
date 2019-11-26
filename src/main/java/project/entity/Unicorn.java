package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.control.ArenaManager;

/**
 * Unicorn is the Monster with the most health.
 */
@Entity
public class Unicorn extends Monster {
    /**
     * Constructs a newly allocated {@link Unicorn} object and adds it to the currently active arena.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     * @param difficulty The difficulty rating of the monster, which should be at least <code>1</code>.
     */
    public Unicorn(short x, short y, double difficulty) {
        super(x, y, difficulty);
        this.maxHealth = 10 + 3 * difficulty;
        this.baseSpeed = 2 + 0.2 * Math.log10(difficulty);
        this.healthProperty.set(this.maxHealth);
        this.health = this.healthProperty.get();
        this.speed = this.baseSpeed;
        this.resourceValue = (int) (difficulty * 1);
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/unicorn.png", ArenaManager.GRID_WIDTH / 4, ArenaManager.GRID_HEIGHT / 4, true, true));
    }
}