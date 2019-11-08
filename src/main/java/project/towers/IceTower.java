package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;
import project.projectiles.IceProjectile;
import project.projectiles.Projectile;

import java.util.PriorityQueue;

/**
 * IceTower slow down the speed of monster without damage.
 */
public class IceTower extends Tower{

    /**
     * The maximum slow down duration of the tower.
     */
    private int maxSlowDown;

    /**
     * The current slow down duration of ice tower. It cannot go beyond {@link #maxSlowDown}.
     */
    private int slowDown;


    /**
     * Constructor of ice tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of ice tower.
     */
    public IceTower(Arena arena, Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 0;
        this.buildingCost = 15;
        this.maxShootingRange = 50;
        this.slowDown = 10;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * Constructor of ice tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of ice tower.
     * @param imageView The image view of ice tower.
     */
    public IceTower(Arena arena, Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 0;
        this.buildingCost = 15;
        this.maxShootingRange = 50;
        this.slowDown = 10;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * Ice tower increases its slow down duration when it upgraded.
     * @param player The player who build the tower.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(Player player){
        if(player.hasResources(upgradeCost)){
            player.spendResources(upgradeCost);
            if(this.slowDown+5 >= maxSlowDown){
                this.slowDown = maxSlowDown;
            }else this.slowDown+=5;
            return true;
        }
        return false;
    }

    /**
     * Attack the monster closest to destination and in shooting range.
     * @return The projectile of tower attack, return null if cannot shoot any monster.
     */
    @Override
    public Projectile attackMonster(){
        if(!isReload()) {
            PriorityQueue<Monster> monsters = arena.getMonsters();
            for (Monster m : monsters) {
                if (canShoot(m)) {
                    this.hasAttack = true;
                    this.counter = this.reload;
                    return new IceProjectile(arena, coordinates, new Coordinates(m.getX(), m.getY()), attackSpeed, slowDown);
                }
            }
        }
        return null;
    }

    /**Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("attack power: %d\nbuilding cost: %d\nshooting range: [%d , %d]\n"
                + "slow down: %s", this.attackPower, this.buildingCost, this.minShootingRange,
                this.maxShootingRange,this.slowDown);
    }
}
