package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

/**
 * IceTower slow down the speed of monster without damage.
 */
public class IceTower extends Tower{

    /**
     * The slow down duration of ice tower.
     */
    private double slowDown;


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

    @Override
    public void attackMonster(Monster monster){
        if(canShoot(monster)){
            monster.setSpeed(monster.getSpeed()-this.slowDown);
            monster.setHealth((int)(monster.getHealth()-this.attackPower));
        }
    }
}
