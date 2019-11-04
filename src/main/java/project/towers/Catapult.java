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
    public boolean canShoot(Monster monster){
        double dis = coordinates.diagonalDistanceFrom(monster);
        return dis <= shootingRange && dis >= shootLimit;
    }

    @Override
    protected void attackMonster(Monster monster){
        monster.setHealth((int)(monster.getHealth()-this.attackPower));
    }

    public void attackMonsters(){
        LinkedList<Monster> monsters = new LinkedList<>();
        Coordinates coordinate= selectMonster(Arena.getMonsters(),monsters);
        throwStone(coordinate);
        for (Monster m:monsters) {
            attackMonster(m);
        }
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(reload*1000);
                } catch (InterruptedException e) { }
            }
        }.start();
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
    public Coordinates selectMonster(LinkedList<Monster> monsters,LinkedList<Monster> attackedMon){
        int count=0;
        for (Monster m:monsters) {
            if(canShoot(m)){
                
            }
        }
        return new Coordinates();
    }
}
