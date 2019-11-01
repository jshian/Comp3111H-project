package project.towers;

import project.*;
import project.monsters.Monster;

public class BasicTower extends Tower {

    /**
     * Constructor for BasicTower class.
     * @param arena The arena in which the basic tower exists.
     */
    public BasicTower(Arena arena){
        super(arena);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.shootingRange = 65;
    }

    @Override
    public void attackMonster(Monster monster){
        if(canShoot(monster.getCoordinates()))
            monster.setHealth(monster.getHealth()-this.attackPower);
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
