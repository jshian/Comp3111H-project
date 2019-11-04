package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;
import project.projectiles.IceProjectile;
import project.projectiles.Projectile;

import java.util.LinkedList;

/**
 * IceTower slow down the speed of monster without damage.
 */
public class IceTower extends Tower{

    /**
     * The slow down duration of ice tower.
     */
    private int slowDown;


    /**
     * Constructor of ice tower.
     * @param coordinates The coordinates of ice tower.
     */
    public IceTower(Coordinates coordinates){
        super(coordinates);
        this.attackPower = 5;
        this.buildingCost = 15;
        this.shootingRange = 50;
        this.slowDown = 10;
        this.attackSpeed = 5;
    }

    /**
     * Constructor of ice tower.
     * @param coordinates The coordinates of ice tower.
     * @param imageView The image view of ice tower.
     */
    public IceTower(Coordinates coordinates, ImageView imageView) {
        super(coordinates, imageView);
        this.attackPower = 5;
        this.buildingCost = 15;
        this.shootingRange = 50;
        this.slowDown = 10;
        this.attackSpeed = 5;
    }

    /**
     * Ice tower increases its slow down duration when it upgraded.
     * @param resource The resources needed for tower to upgrade.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(int resource){
        if(resource >= 20){
            this.slowDown+=5;
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
        LinkedList<Monster> monsters = Arena.getMonsters();
        for (Monster m : monsters) {
            if(canShoot(m))
                return new IceProjectile(coordinates,new Coordinates(m.getX(),m.getY()),attackSpeed,slowDown);
        }
        return null;
    }
}
