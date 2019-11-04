package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Catapult extends Tower {
    // States
    private int reload,shootLimit,counter;

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
        this.counter = reload;
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

    /**
     * Attack the monsters selected by seleteMonster function and with a reload second delay
     */
    public void attackMonsters(){
        if(counter==0) {
            LinkedList<Monster> monsters = new LinkedList<>();
            Coordinates coordinate = selectMonster(Arena.getMonsters(), monsters);
            throwStone(coordinate);
            for (Monster m : monsters) {
                attackMonster(m);
            }
            counter = 10;
        }else counter--;
    }

    @Override
    public boolean upgrade(int resource){
        if(resource >= 20){
            if(reload>0)reload-=1;
            return true;
        }
        return false;
    }


    /**
     * Find a coordinate as the center of a circle with radius 25px that contains most monster
     * The monster nearest to the end zone and in Catapult's shooting range will be include in the circle
     * @param monsters The monsters exist in Arena
     * @param attackedMon The monsters will be attacked
     * @return The coordinate that will be attacked by Catapult
     */
    public Coordinates selectMonster(LinkedList<Monster> monsters,LinkedList<Monster> attackedMon){
        LinkedList<Monster> nearestMon=new LinkedList<>();
        double nearest=0;
        //find nearest distance
        for (Monster m:monsters) {
            if(canShoot(m)){
                nearest=m.distanceToDestination();
            }
        }
        //find all monster have the nearest distance and can be shoot
        for (Monster m :monsters) {
            if(m.distanceToDestination()==nearest && canShoot(m))
                nearestMon.add(m);
        }
        //find the target coordinate to attack
        int radius = 25;
        Coordinates target = null;
        for (Monster m :nearestMon) {
            int count=0;//count number of monster in the circle
            for (int i = m.getX()-radius; i < m.getX()+radius; i++) {
                for (int j = m.getY()-radius; j < m.getY()+radius; j++) {
                    Coordinates c = new Coordinates(i,j);
                    if (canShoot(c)){
                        LinkedList monInCircle = c.monsterInCircle(radius);
                        if(count < monInCircle.size()){
                            count=monInCircle.size();
                            target = c;
                        }
                    }
                }
            }
        }
        return target;
    }
}
