package project.entity;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.query.ArenaObjectStorage;

/**
 * Unicorn is the Monster with the most health.
 */
@Entity
public class Unicorn extends Monster {
    /**
     * Constructs a newly allocated {@link Unicorn} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     * @param difficulty The difficulty rating of the monster, which should be at least <code>1</code>.
     */
    public Unicorn(ArenaObjectStorage storage, ImageView imageView, short x, short y, double difficulty) {
        super(storage, imageView, x, y, difficulty);
        this.maxHealth = 10 + 1 * difficulty;
        this.baseSpeed = 2 + 0.2 * Math.log10(difficulty);
        this.health.set(this.maxHealth);
        this.speed = this.baseSpeed;
        this.resourceValue = (int) (difficulty * 1);
    }
}