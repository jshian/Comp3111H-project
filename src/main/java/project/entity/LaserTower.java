package project.entity;

import javax.persistence.Entity;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Player;
import project.UIController;
import project.arena.ArenaInstance;
import project.arena.Coordinates;
import project.controller.ArenaManager;
import project.query.ArenaObjectStorage;

/**
 * LaserTower consume resources to attack monster.
 */
@Entity
public class LaserTower extends Tower{

    /**
     * The duration of laser of {@link LaserTower} being displayed.
     */
    static final int LASER_DISPLAY_DURATION = 2;

    /**
     * Returns the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int getBuildingCost() {
        return 20;
    }

    /**
     * The consumption of resources by laser tower each time it shoots.
     */
    private int shootingCost = 2;

    /**
     * Constructs a newly allocated {@link LaserTower} object.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public LaserTower(ArenaObjectStorage storage, ImageView imageView, short x, short y) {
        super(storage, imageView, x, y);
        this.attackPower = 30;
        this.maxRange = 100;
        this.projectileSpeed = Integer.MAX_VALUE;
        this.buildValue = getBuildingCost();
        this.upgradeCost = 10;
    }

    /**
     * Laser tower consume currently active player's resource to attack monster.
     * @return true if player has enough resources to attack, false otherwise.
     */
    private boolean consumeResource(){
        Player player = ArenaManager.getActivePlayer();
        if (player.hasResources(shootingCost)) {
            player.spendResources(shootingCost);
            return true;
        }
        return false;
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
    public void generateProjectile(Monster primaryTarget) {
        if (!consumeResource()) return; // Cannot fire if there are not enough resources

        new LaserProjectile(storage, imageView, this, primaryTarget, (short) 0, (short) 0);
    }

    /**
     * Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("Shooting Cost: %d\nAttack Power: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.shootingCost,
            this.attackPower, this.reload,this.minRange,this.maxRange,this.upgradeCost,this.buildValue);
    }
}

