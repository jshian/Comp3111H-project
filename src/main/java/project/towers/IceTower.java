package project.towers;

import project.*;
import project.monsters.Monster;

public class IceTower extends Tower{
    // State
    private double slowDown;

    /**
     * Constructor for IceTower class.
     * @param arena The arena in which the ice tower exists.
     */
    public IceTower(Arena arena){
        super(arena);
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
        if(canShoot(monster.getCoordinates())){
            monster.setSpeed(monster.getSpeed()-this.slowDown);
            monster.setHealth(monster.getHealth()-this.attackPower);
        }
    }
}
