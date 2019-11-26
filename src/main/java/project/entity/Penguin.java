package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.control.ArenaManager;

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

            this.health.set(Math.min(getHealth() + regenerationRate, maxHealth));
        };
    }
    
    /**
     * Constructs a newly allocated {@link Penguin} object and adds it to the currently active arena.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     * @param difficulty The difficulty rating of the monster, which should be at least <code>1</code>.
     */
    public Penguin(short x, short y, double difficulty) {
        super(x, y, difficulty);
        this.maxHealth = 7.5 + 2.5 * difficulty;
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