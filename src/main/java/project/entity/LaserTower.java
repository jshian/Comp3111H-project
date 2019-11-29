package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

import javax.persistence.Entity;

import project.Player;
import project.control.ArenaManager;

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
     * Default constructor.
     */
    public LaserTower() {}

    /**
     * Constructs a newly allocated {@link LaserTower} object.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public LaserTower(short x, short y) {
        super(x, y);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void upgrade() {
        super.upgrade();
        if (this.attackPower + 5 >= maxAttackPower) {
            this.attackPower = maxAttackPower;
        } else {
            this.attackPower += 5;
        }
    }

    /**
     * {@inheritDoc}
     * Laser tower consumes currently active player's resource to attack monster.
     */
    @Override
    protected void shoot(List<Monster> validTargets) {
        if (consumeResource()) {
            super.shoot(validTargets);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayDetails() {
        return String.format("Shooting Cost: %d\nAttack Power: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.shootingCost,
            this.attackPower, this.reload,this.minRange,this.maxRange,this.upgradeCost,this.buildValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/laserTower.png", ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT, true, true));
    }
}

