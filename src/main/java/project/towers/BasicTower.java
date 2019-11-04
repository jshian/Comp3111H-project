package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

public class BasicTower extends Tower {

    /**
     * Default constructor for BasicTower class.
     */
    public BasicTower(Coordinates coordinate){
        super(coordinate);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.shootingRange = 65;
    }

    public BasicTower(Coordinates coordinates, ImageView imageView) {
        super(coordinates, imageView);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.shootingRange = 65;
    }

    @Override
    public void attackMonster(Monster monster){
        if(canShoot(monster))
            monster.setHealth((int)(monster.getHealth()-this.attackPower));
    }

    @Override
    public boolean upgrade(int resource){
        if(resource >= 10){
            this.attackPower+=5;
            return true;
        }
        return false;
    }

    @Override
    public String getInformation() {
        return String.format("attack power: %s\nbuilding cost: %s\nshooting range: %s", this.attackPower,
                this.buildingCost, this.shootingRange);
    }
}
