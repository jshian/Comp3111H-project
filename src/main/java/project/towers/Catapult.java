package project.towers;

import project.*;

public class Catapult extends Tower {
    // States
    private int reload,shootLimit;

    /**
     * Constructor for Catapult class.
     * @param arena The arena in which the catapult exists.
     */
    public Catapult(Arena arena){
        super(arena);
        this.attackPower = 20;
        this.buildingCost = 20;
        this.shootingRange = 150;
        this.reload = 10;
        this.shootLimit = 50;
    }


    /**
     * Throw stone to certain position.
     * @param cor The coordinate of the target.
     */
    public void throwStone(Coordinates cor){

    }

    @Override
    public boolean upgrade(int resource){
        if(resource >= 20){
            attackPower+=5;
            if(reload>0)reload-=1;
            return true;
        }
        return false;
    }

    //Algorithm for selecting monster
    public void selectMonster(Arena arena){}
}
