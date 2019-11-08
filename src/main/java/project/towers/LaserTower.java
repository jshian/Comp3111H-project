package project.towers;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
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
     * The laser display on the arena.
     */
    private Line laserLine;


    /**
     * Constructor of laser tower.
     * The max shooting range of laser tower refers to the range that will start attack but not the damage range.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of laser tower.
     */
    public LaserTower(Arena arena, Coordinates coordinates){
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
    public LaserTower(Arena arena, Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.maxShootingRange = 100;
        this.consume = 2;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(Tower)
     */
    public LaserTower(LaserTower other){
        super(other);
        this.consume = other.consume;
        this.laserLine = new Line(other.laserLine.getStartX(),other.laserLine.getStartY(),
                other.laserLine.getEndX(),other.laserLine.getEndY());
    }

    @Override
    public LaserTower deepCopy() {
        return new LaserTower(this);
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
     * @param player The player who build the tower.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(Player player){
        if(player.hasResources(upgradeCost)){
            player.spendResources(upgradeCost);
            if(this.attackPower+5 >= maxAttackPower)
                this.attackPower = maxAttackPower;
            else this.attackPower += 5;
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
            this.counter = this.reload;
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
        return String.format("attack power: %d\nbuilding cost: %d\nshooting range: [%d , %d]\nconsume: %d", this.attackPower,
                this.buildingCost, this.minShootingRange, this.maxAttackPower, this.consume);
    }
}
