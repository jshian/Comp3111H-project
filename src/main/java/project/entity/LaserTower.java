package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.Player;
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
     * Constructs a newly allocated {@link LaserTower} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public LaserTower(ArenaObjectStorage storage, short x, short y) {
        super(storage, x, y);
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

        new LaserProjectile(storage,  this, primaryTarget, (short) 0, (short) 0);
    }

    @Override
    public String getDisplayDetails() {
        return String.format("Shooting Cost: %d\nAttack Power: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.shootingCost,
            this.attackPower, this.reload,this.minRange,this.maxRange,this.upgradeCost,this.buildValue);
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/laserTower.png", ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT, true, true));
    }
}

