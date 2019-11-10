package project.arena.towers;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Player;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;
import project.arena.projectiles.IceProjectile;
import project.arena.projectiles.Projectile;

/**
 * IceTower slow down the speed of monster without damage.
 */
public class IceTower extends Tower {

    /**
     * The maximum slow down duration of the tower.
     */
    private final int maxSlowDownTime = 100;

    /**
     * The current slow down duration of ice tower. It cannot go beyond {@link #maxSlowDownTime}.
     */
    private int slowDownTime;


    /**
     * Constructor of ice tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of ice tower.
     */
    public IceTower(@NonNull Arena arena, @NonNull Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 0;
        this.buildingCost = 15;
        this.maxShootingRange = 50;
        this.slowDownTime = 10;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * Constructor of ice tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of ice tower.
     * @param imageView The image view of ice tower.
     */
    public IceTower(@NonNull Arena arena, @NonNull Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 0;
        this.buildingCost = 15;
        this.maxShootingRange = 50;
        this.slowDownTime = 10;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(Tower)
     */
    public IceTower(@NonNull IceTower other){
        super(other);
        this.slowDownTime = other.slowDownTime;
    }

    @Override
    public IceTower deepCopy() {
        return new IceTower(this);
    }

    /**
     * Ice tower increases its slow down duration when it upgraded.
     * @param player The player who build the tower.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(@NonNull Player player){
        if(player.hasResources(upgradeCost)){
            player.spendResources(upgradeCost);
            if(this.slowDownTime+5 >= maxSlowDownTime){
                this.slowDownTime = maxSlowDownTime;
            }else this.slowDownTime+=5;
            return true;
        }
        return false;
    }

    @Override
    public Projectile generateProjectile(){
        if(!isReload()) {
            for (Monster m : arena.getMonsters()) {
                if (canShoot(m)) {
                    this.hasAttack = true;
                    this.counter = this.reload;
                    return new IceProjectile(arena, coordinates, new Coordinates(m.getX(), m.getY()), attackSpeed, slowDownTime);
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
        return String.format("attack power: %d\nbuilding cost: %d\nshooting range: [%d , %d]\n"
                + "slow down time: %d", this.attackPower, this.buildingCost, this.minShootingRange,
                this.maxShootingRange,this.slowDownTime);
    }
}