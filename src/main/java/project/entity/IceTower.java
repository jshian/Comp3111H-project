package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.controller.ArenaManager;
import project.query.ArenaObjectStorage;

/**
 * IceTower slow down the speed of monster without damage.
 */
@Entity
public class IceTower extends Tower {

    /**
     * Returns the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int getBuildingCost() {
        return 15;
    }

    /**
     * The current slow down duration of ice tower. It cannot go beyond {@link #maxSlowDownTime}.
     */
    private int slowDownTime = 1;

    /**
     * The maximum slow down duration of the tower.
     */
    private final int maxSlowDownTime = 100;

    /**
     * Constructs a newly allocated {@link IceTower} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public IceTower(ArenaObjectStorage storage, short x, short y) {
        super(storage, x, y);
        this.attackPower = 0;
        this.maxRange = 50;
        this.projectileSpeed = 10;
        this.slowDownTime = 10;
        this.buildValue = getBuildingCost();
        this.upgradeCost = 10;
    }

    /**
     * Returns the slow down time of tower.
     * @return the slow down time of tower.
     */
    public final int getSlowDownTime() { return slowDownTime; }

    @Override
    protected void upgrade() {
        super.upgrade();
        if (this.slowDownTime + 5 >= maxSlowDownTime) {
            this.slowDownTime = maxSlowDownTime;
        } else {
            this.slowDownTime += 5;
        }
    }

    @Override
    public void generateProjectile(Monster primaryTarget) {
        new IceProjectile(storage, this, primaryTarget, (short) 0, (short) 0);
    }

    @Override
    public String getDisplayDetails() {
        return String.format("Slow Duration: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.slowDownTime,
            this.reload,this.minRange,this.maxRange,this.upgradeCost,this.buildValue);
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/iceTower.png", ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT, true, true));
    }
}
