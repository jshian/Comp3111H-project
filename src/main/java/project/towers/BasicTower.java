package project.towers;

import javafx.scene.image.ImageView;
import org.checkerframework.checker.nullness.qual.NonNull;
import project.*;
import project.monsters.Monster;
import project.projectiles.BasicProjectile;
import project.projectiles.Projectile;


/**
 * Basic tower has no any special ability.
 */
public class BasicTower extends Tower {

    /**
     * Constructor of basic tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinate of basic tower.
     */
    public BasicTower(Arena arena,@NonNull Coordinates coordinates){
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
    public BasicTower(Arena arena,@NonNull Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.maxShootingRange = 65;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(Tower)
     */
    public BasicTower(BasicTower other){
        super(other);
    }

    @Override
    public BasicTower deepCopy() {
        return new BasicTower(this);
    }

    /**
     * Basic tower increases its attack power when it upgraded.
     * @param player The player who build the tower.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(@NonNull Player player){
        if(player.hasResources(upgradeCost)){
            player.spendResources(upgradeCost);
            if(this.attackPower+5>=maxAttackPower)
                this.attackPower = maxAttackPower;
            else this.attackPower += 5;
            return true;
        }
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
                    return new BasicProjectile(arena, coordinates, new Coordinates(m.getX(), m.getY()), attackSpeed, attackPower);
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
