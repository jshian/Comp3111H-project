package Towers;

public class catapult extends basicTower{
    private int reload,shootLimit;

    public catapult(int x, int y){
        super(x,y);
        this.reload = 10;
        this.shootLimit = 50;
        super.shootingRange = 150;
        super.attackPower = 20;
        super.buildingCost = 20;
    }

    public void throwStone(int x,int y){

    }

    public boolean upgrade(int resource){
        if(resource >= 20){
            attackPower+=5;
            if(reload>0)reload-=1;
            return true;
        }
        return false;
    }

    //Algorithm for selecting monster
}
