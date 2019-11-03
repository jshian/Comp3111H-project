package project.towers;

import project.*;
import project.monsters.Monster;

public class BasicTower extends Tower {

    /**
     * Default constructor for BasicTower class.
     */
    public BasicTower(){
        super();
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
}
