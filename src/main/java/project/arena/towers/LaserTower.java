package project.arena.towers;

import javax.persistence.Entity;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Player;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;
import project.arena.projectiles.Projectile;

/**
 * LaserTower consume resources to attack monster.
 */
@Entity
public class LaserTower extends Tower{

    /**
     * Finds the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int findInitialBuildingCost() {
        return 20;
    }

    /**
     * The consumption of resources by laser tower each time it shoots.
     */
    private int shootingCost = 1;

    /**
     * Constructor of laser tower.
     * The max shooting range of laser tower refers to the range that will start attack but not the damage range.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of laser tower.
     */
    public LaserTower(@NonNull Arena arena, @NonNull Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 30;
        this.buildValue = findInitialBuildingCost();
        this.maxShootingRange = 100;
        this.projectileSpeed = Integer.MAX_VALUE;
        this.shootingCost = 2;
        this.upgradeCost = 10;
        this.imageView = new ImageView(new Image("/laserTower.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Constructor of laser tower.
     * The max shooting range of laser tower refers to the range that will start attack but not the damage range.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of laser tower.
     * @param imageView The image view of laser tower.
     */
    public LaserTower(@NonNull Arena arena, @NonNull Coordinates coordinates, @NonNull ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 30;
        this.buildValue = 20;
        this.maxShootingRange = 100;
        this.projectileSpeed = Integer.MAX_VALUE;
        this.shootingCost = 2;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(Arena, Tower)
     */
    public LaserTower(@NonNull Arena arena, @NonNull LaserTower other) {
        super(arena, other);
        this.shootingCost = other.shootingCost;
    }

    @Override
    public LaserTower deepCopy(@NonNull Arena arena) {
        return new LaserTower(arena, this);
    }

    @Override
    protected String getClassName() { return "Laser Tower"; }

    /**
     * Laser tower consume player's resource to attack monster.
     * @param player The player who build the tower.
     * @return true if player has enough resources to attack, false otherwise.
     */
    private boolean consumeResource(@NonNull Player player){
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

    /**
     * Generates a projectile that attacks the target of the tower.
     * @return Always <code>null</code> because the ray generated isn't a projectile.
     */
    @Override
    public Projectile generateProjectile(){
        if(!isReload()) {
            for (Monster m : arena.getMonsters()) {
                if (isValidTarget(m)) {
                    if (!consumeResource(arena.getPlayer())) return null;
                    hasAttack = true;
                    this.counter = this.reload;
                    return arena.createProjectile(this, m, (short) 0, (short) 0);
                }
            }
        }
        return null;
    }

    /**
     * Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("Shooting Cost: %d\nAttack Power: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.shootingCost,
            this.attackPower, this.reload,this.minShootingRange,this.maxShootingRange,this.upgradeCost,this.buildValue);
    }
}

