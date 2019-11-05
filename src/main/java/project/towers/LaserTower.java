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
     * @param coordinates The coordinates of laser tower.
     */
    public LaserTower(Coordinates coordinates){
        super(coordinates);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.shootingRange = 50;
        this.consume = 2;
        this.upgradeCost = 10;
    }

    /**
     * Constructor of laser tower.
     * @param coordinates The coordinates of laser tower.
     * @param imageView The image view of laser tower.
     */
    public LaserTower(Coordinates coordinates, ImageView imageView) {
        super(coordinates, imageView);
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
            for (Monster m :  Arena.getMonsters()) {
                if (canShoot(m))
                    monster = m;
            }
            if (monster == null) {
                return null;
            }
            Coordinates currentPt = new Coordinates(getX(), getY());
            Coordinates edgePt = currentPt.findEdgePt(monster);
            currentPt.drawLine(edgePt);
            int tX = getX();
            int tY = getY();
            int mX = monster.getX();
            int mY = monster.getY();

            PriorityQueue<Monster> monsters = Arena.getMonsters();
            for (Monster m : monsters) {
                for (int x = tX, y = tY; x > mX && y > mY; x += (mX - tX) * 0.01, y += (mY - tY) * 0.01)
                    if ((new Coordinates(x, y)).isInCircle(m, 3))
                        m.setHealth((int) (m.getHealth() - this.attackPower));
            }
        }
        return null;
    }

    /**Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("attack power: %s\nbuilding cost: %s\nshooting range: %s\nconsume: %s", this.attackPower,
                this.buildingCost, this.shootingRange, this.consume);
    }
}
