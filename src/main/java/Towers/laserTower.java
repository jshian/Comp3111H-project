package Towers;

public class laserTower extends basicTower{
    private int consume;

    public laserTower(int x, int y){
        super(x,y);
        super.attackPower = 30;
        super.buildingCost = 20;
        this.consume = 2;
    }

    public void drawLine(int x, int y){

    }

    public void consumeResource(int resource){
        resource-=consume;
    }

    public void killTower(int x,int y){

    }

    public boolean upgrade(int resource){
        if(resource >= 10){
            attackPower+=5;
            return true;
        }
        return false;
    }
}
