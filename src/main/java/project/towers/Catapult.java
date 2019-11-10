package project.towers;

import javafx.scene.image.ImageView;
import org.checkerframework.checker.nullness.qual.NonNull;
import project.*;
import project.Arena.ExistsInArena;
import project.monsters.Monster;
import project.projectiles.CatapultProjectile;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Catapult can attack many monsters at the same time and has high shooting range.
 */
public class Catapult extends Tower {

    /**
     * The damaging range of Catapult which default is 25.
     */
    private final int damageRange = 25;


    /**
     * The least reload time Catapult can have which default is 2 frame.
     */
    private final int minReloadTime = 2;

    /**
     * Constructor of catapult.
     * @param arena The arena to attach the catapult to.
     * @param coordinates The coordinate of catapult.
     */
    public Catapult(Arena arena,@NonNull Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 25;
        this.buildingCost = 20;
        this.minShootingRange = 50;
        this.maxShootingRange = 150;
        this.reload = 20;
        this.counter = 0;
        this.attackSpeed = 50;
        this.upgradeCost = 20;
    }

    /**
     * Constructor of catapult.
     * @param arena The arena to attach the catapult to.
     * @param coordinates The coordinate of catapult.
     * @param imageView The image view of catapult.
     */
    public Catapult(Arena arena,@NonNull Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 25;
        this.buildingCost = 20;
        this.minShootingRange = 50;
        this.maxShootingRange = 150;
        this.reload = 20;
        this.counter = 0;
        this.attackSpeed = 50;
        this.upgradeCost = 20;
    }

    /**
     * @see Tower#Tower(Tower)
     */
    public Catapult(Catapult other){
        super(other);
    }

    @Override
    public Catapult deepCopy() {
        return new Catapult(this);
    }

    /**
     * Catapult decreases its reload time when it upgraded.
     * @param player The player who build the tower.
     * @return True if upgrade is successful, otherwise false.
     */
    @Override
    public boolean upgrade(@NonNull Player player){
        if(player.hasResources(upgradeCost)){
            player.spendResources(upgradeCost);
            if(this.reload <= minReloadTime){
                this.reload = minReloadTime;
            }else this.reload-=1;
            return true;
        }
        return false;
    }

    /**
     * The shooting range of catapult is smaller than shooting range but higher than shooting limit.
     * @param monster the monster who to be shoot.
     * @return True if it is in the shooting range otherwise false.
     */
    @Override
    public boolean canShoot(Monster monster){
        double dis = Geometry.findEuclideanDistance(getX(), getY(), monster.getX(), monster.getY());
        return dis <= maxShootingRange && dis >= minShootingRange;
    }

    /**
     * Generates a projectile that attacks the target of the tower.
     * The target coordinates is set such that the splash effect hits the monster that is closest to the end-zone and in shooting range; and hits the most monsters.
     * @return The projectile that attacks the target of the tower, or <code>null</code> if either there is no valid target or the tower is reloading.
     */
    @Override
    public CatapultProjectile generateProjectile(){
        if(!isReload()) {
            LinkedList<Arena.ExistsInArena> selectList = new LinkedList<>();
            Coordinates coordinate = selectMonster(arena.getMonsters(),selectList);
            if (coordinate != null) {
                hasAttack = true;
                this.counter = this.reload;
                return new CatapultProjectile(arena, this.coordinates,coordinate,attackSpeed,attackPower,damageRange);
            }
        }
        return null;
    }

    /**
     * Find a coordinate as the center of a circle with radius 25px that contains most monster.
     * The monster nearest to the end zone and in Catapult's shooting range will be include in the circle.
     * @param monsters The monsters exist in Arena.
     * @param selectList The list of attacked monster in current position (used for testing).
     * @return The coordinate that will be attacked by Catapult.
     */
    public Coordinates selectMonster(PriorityQueue<Monster> monsters, LinkedList<Arena.ExistsInArena> selectList){
        LinkedList<Monster> nearestMon=new LinkedList<>();
        double nearest=0;
        //find nearest to destination monster in shooting range
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
        int radius = damageRange;
        Coordinates target = null;
        for (Monster m :nearestMon) {//every nearest monster as a center of a circle
            int count=0;//count number of monster in the circle

            for (int i = m.getX()-radius; i < m.getX()+radius; i++) {//square width
                for (int j = m.getY()-radius; j < m.getY()+radius; j++) {//square length
                    Coordinates c = new Coordinates(i,j);//tested coordinate
                    if (canShoot(c) && Math.pow(radius,2)>=Math.pow(i-m.getX(),2)+Math.pow(j-m.getY(),2)){//damage range in current point
                        LinkedList<ExistsInArena> monInCircle = arena.findObjectsInRange(c, radius, EnumSet.of(Arena.TypeFilter.Monster));
                        if(count < monInCircle.size()){
                            selectList.clear();
                            count=monInCircle.size();
                            target = c;
                            selectList.addAll(monInCircle);
                        }
                    }
                }
            }
        }
        return target;
    }

    /**
     * Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("attack power: %d\nbuilding cost: %d\nshooting range: [%d ,  %d]\n"
                + "reload time: %d\ndamage range: %d", this.attackPower,
                this.buildingCost, this.minShootingRange, this.maxShootingRange, this.reload, this.damageRange);
    }

    /**
     * Accesses the damage range of catapult.
     * @return The damage range of catapult.
     */
    public int getDamageRange() {
        return damageRange;
    }
}
