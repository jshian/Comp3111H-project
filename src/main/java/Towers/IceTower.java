package Towers;

public class IceTower extends BasicTower{
    private int slowDown;

    public IceTower(int x, int y){
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
