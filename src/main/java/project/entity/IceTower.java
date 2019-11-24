package project.entity;

import javax.persistence.Entity;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.ArenaInstance;
import project.arena.Coordinates;
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
     * Constructs a newly allocated {@link IceTower} object.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public IceTower(ArenaObjectStorage storage, ImageView imageView, short x, short y) {
        super(storage, imageView, x, y);
        this.attackPower = 0;
        this.maxRange = 50;
        this.projectileSpeed = 10;
        this.slowDownTime = 10;
        this.buildValue = getBuildingCost();
        this.upgradeCost = 10;
    }

    /**
     * Constructor of ice tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of ice tower.
     * @param imageView The image view of ice tower.
     */
    public IceTower(ArenaInstance arena, Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 0;
        this.buildValue = 15;
        this.maxRange = 50;
        this.slowDownTime = 10;
        this.projectileSpeed = 10;
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
        new IceProjectile(storage, imageView, this, primaryTarget, (short) 0, (short) 0);
    }

    /**
     * Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("Slow Duration: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.slowDownTime,
            this.reload,this.minRange,this.maxRange,this.upgradeCost,this.buildValue);
    }
}
