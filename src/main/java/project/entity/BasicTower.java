package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.control.ArenaManager;

/**
 * Basic tower has no any special ability.
 */
@Entity
public class BasicTower extends Tower {

    /**
     * Returns the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int getBuildingCost() {
        return 10;
    }

    /**
     * Constructs a newly allocated {@link BasicTower} object.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public BasicTower(short x, short y) {
        super(x, y);
        this.attackPower = 10;
        this.maxRange = 65;
        this.projectileSpeed = 5;
        this.buildValue = getBuildingCost();
        this.upgradeCost = 10;
    }

    @Override
    protected void upgrade() {
        super.upgrade();
        if (this.attackPower + 5 >= maxAttackPower) {
            this.attackPower = maxAttackPower;
        } else {
            this.attackPower += 5;
        }
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/basicTower.png", ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT, true, true));
    }
}
