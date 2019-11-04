package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

public class IceTower extends Tower{
    // State
    private double slowDown;

    /**
     * Default constructor for IceTower class.
     */
    public IceTower(Coordinates coordinate){
        super(coordinate);
        this.attackPower = 5;
        this.buildingCost = 15;
        this.shootingRange = 50;
        this.slowDown = 10;
    }

    public IceTower(Coordinates coordinates, ImageView imageView) {
        super(coordinates, imageView);
        this.attackPower = 5;
        this.buildingCost = 15;
        this.shootingRange = 50;
        this.slowDown = 10;
    }

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

    @Override
    public String getInformation() {
        return String.format("attack power: %s\nbuilding cost: %s\nshooting range: %s\n"
                + "slow down: %s", this.attackPower, this.buildingCost, this.shootingRange,
                this.slowDown);
    }
}
