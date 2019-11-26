package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.controller.ArenaManager;
import project.query.ArenaObjectStorage;

/**
 * Penguin has the ability to regenerate.
 */
@Entity
public class Penguin extends Monster {

    /**
     * The regeneration rate of the Penguin in terms of health per frame.
     */
    protected double regenerationRate;

    // Penguin can regenerate health every frame
    {
        onNextFrame = (sender, args) -> {
            moveMonsterOneFrame();
            updateStatusEffects();

            this.health.set(getHealth() + regenerationRate);
        };
    }
    
    /**
     * Constructs a newly allocated {@link Penguin} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     * @param difficulty The difficulty rating of the monster, which should be at least <code>1</code>.
     */
    public Penguin(ArenaObjectStorage storage, short x, short y, double difficulty) {
        super(storage, x, y, difficulty);
        this.maxHealth = 7.5 + 3 * difficulty;
        this.baseSpeed = 3 + 0.3 * Math.log10(difficulty);
        this.health.set(this.maxHealth);
        this.speed = this.baseSpeed;
        this.resourceValue = (int) (difficulty * 1.25);
        this.regenerationRate = maxHealth * 0.05;
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/penguin.png", ArenaManager.GRID_WIDTH / 4, ArenaManager.GRID_HEIGHT / 4, true, true));
    }
}