package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

import java.util.LinkedList;

public class Catapult extends Tower {
    // States
    private int reload,shootLimit;

    /**
     * Default constructor for Catapult class.
     */
    public Catapult(Coordinates coordinate){
        super(coordinate);
        this.attackPower = 25;
        this.buildingCost = 20;
        this.shootingRange = 150;
        this.reload = 10;
        this.shootLimit = 50;
    }

    public Catapult(Coordinates coordinates, ImageView imageView) {
        super(coordinates, imageView);
        this.attackPower = 25;
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
    public void attackMonster(Monster monster){
        LinkedList<Monster> monsters = selectMonster(Arena.getMonsters());
        throwStone(monsters.getFirst().getCoordinates());
        for (Monster m:monsters) {
            m.setHealth((int)(m.getHealth()-this.attackPower));
        }
    }

    @Override
    public boolean upgrade(int resource){
        if(resource >= 20){
            if(reload>0)reload-=1;
            return true;
        }
        return false;
    }

    //Algorithm for selecting monster
    public LinkedList<Monster> selectMonster(LinkedList<Monster> monsters){
        LinkedList<Monster> attackedMon = new LinkedList<>();

        for (Monster m:monsters) {
            double dis = m.getCoordinates().diagonalDistanceFrom(new Coordinates(440,0));

        }
        return attackedMon;
    }
}
