package Towers;

public class iceTower extends basicTower{
    private int slowDown;

    public iceTower(int x, int y){
        super(x,y);
        super.buildingCost=20;
        this.slowDown = 10;
    }

    public boolean upgrade(int resource){
        if(resource >= 20){
            slowDown+=5;
            return true;
        }
        return false;
    }
}
