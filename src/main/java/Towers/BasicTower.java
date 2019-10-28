package Towers;


public class BasicTower {
    protected int attackPower, buildingCost, shootingRange, x, y;

    protected BasicTower(){

    }

    public BasicTower(int x , int y){
        this.attackPower = 10;
        this.buildingCost = 10;
        this.shootingRange = 65;
        this.x = x;
        this.y = y;
    }

    public boolean upgrade(int resource){
        if(resource >= 10){
            attackPower+=5;
            return true;
        }
        return false;
    }

    public boolean canShoot(int x, int y){
        return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) <= shootingRange * shootingRange;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getBuildingCost() {
        return buildingCost;
    }

    public int getShootingRange() {
        return shootingRange;
    }



}
