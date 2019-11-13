package project.arena.towers;

import java.util.LinkedList;
import java.util.PriorityQueue;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Geometry;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.ExistsInArena;
import project.arena.monsters.Monster;
import project.arena.projectiles.CatapultProjectile;

/**
 * Catapult can attack many monsters at the same time and has high shooting range.
 */
@Entity
public class Catapult extends Tower {

    /**
     * The damaging range of Catapult which default is 25.
     */
    private final short damageRange = 25;

    /**
     * The least reload time Catapult can have which default is 2 frame.
     */
    private final int minReloadTime = 2;

    /**
     * Constructor of catapult.
     * @param arena The arena to attach the catapult to.
     * @param coordinates The coordinate of catapult.
     */
    public Catapult(@NonNull Arena arena, @NonNull Coordinates coordinates){
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
    public Catapult(@NonNull Arena arena, @NonNull Coordinates coordinates, ImageView imageView) {
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
     * @see Tower#Tower(Arena, Tower)
     */
    public Catapult(@NonNull Arena arena, @NonNull Catapult other) {
        super(arena, other);
    }

    @Override
    public Catapult deepCopy(@NonNull Arena arena) {
        return new Catapult(arena, this);
    }

    @Override
    protected String getClassName() { return "Catapult"; }

    @Override
    protected void upgrade() {
        super.upgrade();
        if (this.reload <= minReloadTime) {
            this.reload = minReloadTime;
        } else {
            this.reload -= 1;
        }
    }

    /**
     * Generates a projectile that attacks the target of the tower.
     * The target coordinates is set such that the splash effect hits the monster that is closest to the end-zone and in shooting range; and hits the most monsters.
     * @return The projectile that attacks the target of the tower, or <code>null</code> if either there is no valid target or the tower is reloading.
     */
    @Override
    public CatapultProjectile generateProjectile(){
        if(!isReload()) {
            LinkedList<ExistsInArena> selectList = new LinkedList<>();
            Coordinates targetCoordinates = selectMonster(arena.getMonsters(), selectList);
            if (targetCoordinates != null) {
                short closestDistance = Short.MAX_VALUE;
                double leastDelta = Double.POSITIVE_INFINITY;
                Monster targetMonster = null;
                for (Monster m : arena.getMonsters()) {
                    Coordinates c = new Coordinates(m.getX(), m.getY());
                    if (canShoot(m) && closestDistance == Short.MAX_VALUE) {
                        closestDistance = arena.getDistanceToEndZone(c);

                        double delta = Geometry.findEuclideanDistance(m.getX(), m.getY(), targetCoordinates.getX(), targetCoordinates.getY());
                        if (delta < leastDelta) {
                            leastDelta = delta;
                            targetMonster = m;
                        }
                    }
                    if (arena.getDistanceToEndZone(c) > closestDistance) break;
                }

                hasAttack = true;
                this.counter = this.reload;

                short deltaX = (short) (targetCoordinates.getX() - targetMonster.getX());
                short deltaY = (short) (targetCoordinates.getY() - targetMonster.getY());
                return new CatapultProjectile(arena, coordinates, targetMonster, deltaX, deltaY, attackSpeed, attackPower, damageRange);
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
    public Coordinates selectMonster(PriorityQueue<Monster> monsters, LinkedList<ExistsInArena> selectList){
        LinkedList<Monster> nearestMon=new LinkedList<>();
        short nearest = 0;
        //find nearest to destination monster in shooting range
        for (Monster m:monsters) {
            if(canShoot(m)){
                nearest = arena.getDistanceToEndZone(new Coordinates(m.getX(),m.getY()));//distance;
                break;
            }
        }
        //find all monster have the nearest distance and can be shoot
        for (Monster m :monsters) {
            Coordinates c = new Coordinates(m.getX(),m.getY());
            if(arena.getDistanceToEndZone(c)==nearest && canShoot(m))
                nearestMon.add(m);
        }
        //find the target coordinate to attack
        short radius = damageRange;
        Coordinates target = null;
        for (Monster m :nearestMon) {//every nearest monster as a center of a circle
            int count=0;//count number of monster in the circle

            for (short i = (short) (m.getX()-radius); i < m.getX()+radius; i++) {//square width
                for (short j = (short) (m.getY()-radius); j < m.getY()+radius; j++) {//square length
                    if (i < 0 || i > UIController.ARENA_WIDTH) continue;
                    if (j < 0 || j > UIController.ARENA_HEIGHT) continue;
                    Coordinates c = new Coordinates(i,j);//tested coordinate

                    if (isInRange(c) && Geometry.isInCircle(i,j,m.getX(),m.getY(),radius)){//damage range in current point
                        LinkedList<Monster> monInCircle = new LinkedList<>();

                        for (Monster testMon:arena.getMonsters()){//find the monsters in the range
                            if(Geometry.isInCircle(testMon.getX(),testMon.getY(),i,j,radius)){
                                monInCircle.add(testMon);
                            }
                        }
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
        return String.format("attack power: %d\nupgrade cost: %d\nshooting range: [%d ,  %d]\n"
                + "reload time: %d\ndamage range: %d", this.attackPower,
                this.upgradeCost, this.minShootingRange, this.maxShootingRange, this.reload, this.damageRange);
    }

    /**
     * Accesses the damage range of catapult.
     * @return The damage range of catapult.
     */
    public short getDamageRange() {
        return damageRange;
    }
}
