package project.entity;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.controller.ArenaManager;
import project.query.ArenaObjectStorage;

/**
 * Fox is the fastest Monster. It also tries to follow the path where it receives the minimum number of attacks from Towers.
 */
@Entity
public class Fox extends Monster {

    // Fox is very smart and will try to not be attacked
    {
        gradientDescentField = ArenaManager.getActiveScalarFieldRegister().MONSTER_ATTACKS_TO_END;
    }

    /**
     * Constructs a newly allocated {@link Fox} object.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     * @param difficulty The difficulty rating of the monster, which should be at least <code>1</code>.
     */
    public Fox(ArenaObjectStorage storage, ImageView imageView, short x, short y, double difficulty) {
        super(storage, imageView, x, y, difficulty);
        this.maxHealth = 5 + 2 * difficulty;
        this.baseSpeed = 5 + 0.5 * Math.log10(difficulty);
        this.health.set(this.maxHealth);
        this.speed = this.baseSpeed;
        this.resourceValue = (int) (difficulty * 1.5);
    }
}