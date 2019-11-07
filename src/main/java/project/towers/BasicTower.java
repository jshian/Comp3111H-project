package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;
import project.projectiles.Projectile;

import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Basic tower has no any special ability.
 */
public class BasicTower extends Tower {

    /**
     * Constructor of basic tower.
     * @param coordinates The coordinate of basic tower.
     */
    public BasicTower(Coordinates coordinates){
        super(coordinates);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.shootingRange = 65;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * Constructor of basic tower.
     * @param coordinates The coordinate of basic tower.
     * @param imageView The image view of basic tower.
     */
    public BasicTower(Coordinates coordinates, ImageView imageView) {
        super(coordinates, imageView);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.shootingRange = 65;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * Basic tower increases its attack power when it upgraded.
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
     * Attack the monster closest to destination and in shooting range.
     * @return The projectile of tower attack, return null if cannot shoot any monster.
     */
    @Override
    public Projectile attackMonster(){
        if(!isReload()) {
            PriorityQueue<Monster> monsters = Arena.getMonsters();
            for (Monster m : monsters) {
                if (canShoot(m))
                    return new Projectile(coordinates, new Coordinates(m.getX(), m.getY()), attackSpeed, attackPower);
            }
        }
        return null;
    }

    /**Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("attack power: %s\nbuilding cost: %s\nshooting range: %s", this.attackPower,
                this.buildingCost, this.shootingRange);
    }
}
