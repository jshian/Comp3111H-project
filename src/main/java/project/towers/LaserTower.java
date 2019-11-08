package project.towers;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import project.*;
import project.monsters.Monster;
import project.projectiles.Projectile;

import java.awt.*;
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
     * The laser display on the arena.
     */
    private Line laserLine;


    /**
     * Constructor of laser tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of laser tower.
     */
    public LaserTower(Arena arena, Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.shootingRange = 100;
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
        this.shootingRange = 100;
        this.consume = 2;
        this.upgradeCost = 10;
    }

    /**
     * Laser tower consume player's resource to attack monster.
     * @param player The player who build the tower.
     * @return true if player has enough resources to attack, false otherwise.
     */
    public boolean consumeResource(Player player){
        if (player.hasResources(consume)) {
            player.spendResources(consume);
            return true;
        }
        return false;
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
                this.laserLine = null;
                return null;
            }
            hasAttack = true;
            laserLine = arena.drawRay(this, monster);

            PriorityQueue<Monster> monsters = arena.getMonsters();
            for (Monster m : monsters) {
                if (Geometry.isInRay(m.getX(),m.getY(), getX(),getY(),monster.getX(),monster.getY(), 3)) {
                    m.setHealth(m.getHealth() - this.attackPower);
                    System.out.println(String.format("Laser Tower@(%d,%d) -> %s@(%d,%d)", getX(), getY()
                            , m.getClassName(), m.getX(), m.getY()));
                }
            }
        } else {
            this.laserLine = null;
        }
        return null;
    }

    /**
     * Get the laserLine.
     * @return get the laserLine
     */
    public Line getLaserLine() { return this.laserLine;}


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
