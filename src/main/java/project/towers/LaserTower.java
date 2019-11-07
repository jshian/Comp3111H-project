package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;
import project.projectiles.Projectile;

import java.util.PriorityQueue;


/**
 * LaserTower consume resources to attack monster.
 */
public class LaserTower extends Tower{

    /**
     * The consumption of resources by laser tower each time.
     */
    private int consume;

    /**
     * Constructor of laser tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of laser tower.
     */
    public LaserTower(Arena arena, Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.shootingRange = 50;
        this.consume = 2;
        this.upgradeCost = 10;
    }

    /**
     * Constructor of laser tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of laser tower.
     * @param imageView The image view of laser tower.
     */
    public LaserTower(Arena arena, Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.shootingRange = 50;
        this.consume = 2;
        this.upgradeCost = 10;
    }

    /**
     * Laser tower consume player's resource to attack monster.
     * @param player The player who build the tower.
     */
    public void consumeResource(Player player){
        player.spendResources(consume);
    }

    /**
     * Laser tower increases its attack power when it upgraded.
     * @param resource The resources needed for tower to upgrade.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(int resource){
        if(resource >= this.upgradeCost){
            this.attackPower+=5;
            return true;
        }
        return false;
    }

    /**
     * Attack the nearest monster and the monster in the line.
     * @return null always since it is immediate attack
     */
    @Override
    public Projectile attackMonster(){
        if(!isReload()) {
            Monster monster = null;
            for (Monster m : arena.getMonsters()) {
                if (canShoot(m))
                    monster = m;
            }
            if (monster == null) {
                return null;
            }

            arena.drawRay(this.coordinates, new Coordinates(monster.getX(), monster.getY()));

            PriorityQueue<Monster> monsters = arena.getMonsters();
            for (Monster m : monsters) {
                if (Geometry.isInRay(m.getX(), m.getY(), this.getX(), this.getY(), monster.getX(), monster.getY(), 3))
                    m.setHealth(m.getHealth() - this.attackPower);
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
        return String.format("attack power: %s\nbuilding cost: %s\nshooting range: %s\nconsume: %s", this.attackPower,
                this.buildingCost, this.shootingRange, this.consume);
    }
}
