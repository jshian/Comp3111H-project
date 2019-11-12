package project.arena.towers;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Player;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;
import project.arena.projectiles.LaserProjectile;
import project.arena.projectiles.Projectile;

/**
 * LaserTower consume resources to attack monster.
 */
@Entity
public class LaserTower extends Tower{

    /**
     * The consumption of resources by laser tower each time.
     */
    private int consume = 1;

    /**
     * Constructor of laser tower.
     * The max shooting range of laser tower refers to the range that will start attack but not the damage range.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of laser tower.
     */
    public LaserTower(@NonNull Arena arena, @NonNull Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.maxShootingRange = 100;
        this.consume = 2;
        this.upgradeCost = 10;
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
        this.buildingCost = 20;
        this.maxShootingRange = 100;
        this.consume = 2;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(Arena, Tower)
     */
    public LaserTower(@NonNull Arena arena, @NonNull LaserTower other) {
        super(arena, other);
        this.consume = other.consume;
    }

    @Override
    public LaserTower deepCopy(@NonNull Arena arena) {
        return new LaserTower(arena, this);
    }

    /**
     * Laser tower consume player's resource to attack monster.
     * @param player The player who build the tower.
     * @return true if player has enough resources to attack, false otherwise.
     */
    private boolean consumeResource(@NonNull Player player){
        if (player.hasResources(consume)) {
            player.spendResources(consume);
            return true;
        }
        return false;
    }

    /**
     * Laser tower increases its attack power when it upgraded.
     * @param player The player who build the tower.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(@NonNull Player player){
        if(player.hasResources(upgradeCost)){
            System.out.println("Laser Tower is being upgraded.");
            player.spendResources(upgradeCost);
            if(this.attackPower+5 >= maxAttackPower)
                this.attackPower = maxAttackPower;
            else this.attackPower += 5;
            return true;
        }
        System.out.println("not enough resource to upgrade Laser Tower.");
        return false;
    }

    /**
     * Generates a projectile that attacks the target of the tower.
     * @return Always <code>null</code> because the ray generated isn't a projectile.
     */
    @Override
    public Projectile generateProjectile(){
        if(!isReload()) {
            for (Monster m : arena.getMonsters()) {
                if (canShoot(m)) {
                    if (!consumeResource(arena.getPlayer())) return null;
                    hasAttack = true;
                    this.counter = this.reload;
                    return new LaserProjectile(arena, coordinates, m, attackPower);
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
        return String.format("attack power: %d\nbuilding cost: %d\nshooting range: [%d , %d]\nconsume: %d", this.attackPower,
                this.buildingCost, this.minShootingRange, this.maxAttackPower, this.consume);
    }
}
