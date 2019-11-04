package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

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
    }

    /**
     * Basic tower increases its attack power when it upgraded.
     * @param resource The resources needed for tower to upgrade.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(int resource){
        if(resource >= 10){
            this.attackPower+=5;
            return true;
        }
        return false;
    }

    @Override
    public void attackMonster(Monster monster){
        if(canShoot(monster))
            monster.setHealth((int)(monster.getHealth()-this.attackPower));
    }
}
