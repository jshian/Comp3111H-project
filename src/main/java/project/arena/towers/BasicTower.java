package project.arena.towers;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Player;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;
import project.arena.projectiles.BasicProjectile;
import project.arena.projectiles.Projectile;

/**
 * Basic tower has no any special ability.
 */
@Entity
public class BasicTower extends Tower {

    /**
     * Constructor of basic tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinate of basic tower.
     */
    public BasicTower(@NonNull Arena arena, @NonNull Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.maxShootingRange = 65;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * Constructor of basic tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of the tower.
     * @param imageView The image view of the tower.
     */
    public BasicTower(@NonNull Arena arena, @NonNull Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.maxShootingRange = 65;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(Arena, Tower)
     */
    public BasicTower(@NonNull Arena arena, @NonNull BasicTower other) {
        super(arena, other);
    }

    @Override
    public BasicTower deepCopy(@NonNull Arena arena) {
        return new BasicTower(arena, this);
    }

    /**
     * Basic tower increases its attack power when it upgraded.
     * @param player The player who build the tower.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(@NonNull Player player){
        if(player.hasResources(upgradeCost)){
            System.out.println("Basic Tower is being upgraded.");
            player.spendResources(upgradeCost);
            if(this.attackPower+5>=maxAttackPower)
                this.attackPower = maxAttackPower;
            else this.attackPower += 5;
            return true;
        }
        System.out.println("not enough resource to upgrade Basic Tower.");
        return false;
    }

    @Override
    public Projectile generateProjectile(){
        if(!isReload()) {
//            PriorityQueue<Monster> monsters = arena.getMonsters();
            for (Monster m : arena.getMonsters()) {
                if (canShoot(m)) {
                    this.hasAttack = true;
                    this.counter = this.reload;
                    return new BasicProjectile(arena, coordinates, m, attackSpeed, attackPower);
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
        return String.format("attack power: %d\nbuilding cost: %d\nshooting range: [%d , %d]", this.attackPower,
                this.buildingCost, this.minShootingRange,this.maxShootingRange);
    }
}
