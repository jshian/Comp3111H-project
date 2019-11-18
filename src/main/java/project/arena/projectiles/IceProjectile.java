package project.arena.projectiles;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;
import project.arena.monsters.StatusEffect;

@Entity
public class IceProjectile extends Projectile {
    /**
     * The slow down duration to monsters of projectile.
     */
    private int slowDownTime;

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the ice projectile is initially located.
     * @param target The monster that the projectile will pursue.
     * @param speed The speed of the ice projectile.
     * @param slowDown The cold down time of the ice projectile.
     */
    public IceProjectile(@NonNull Arena arena, @NonNull Coordinates coordinates, @NonNull Monster target, double speed, int slowDown){
        super(arena,coordinates,target,speed,0);
        this.slowDownTime=slowDown;
    }

    @Override
    protected void setupImage() {
        this.imageView = new ImageView(new Image("/iceProjectile.png", UIController.GRID_WIDTH / 8,
                UIController.GRID_HEIGHT / 8, true, true));

        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * @see Projectile#Projectile(Arena, Monster, Projectile)
     */
    public IceProjectile(@NonNull Arena arena, @NonNull Monster target, @NonNull IceProjectile other){
        super(arena, target, other);
        this.slowDownTime = other.slowDownTime;
    }

    @Override
    public IceProjectile deepCopy(@NonNull Arena arena, @NonNull Monster target) {
        return new IceProjectile(arena, target, this);
    }

    @Override
    protected String getTowerClassName() { return "Ice Tower"; }

    /**
     * get slow down time of projectile.
     * @return slow down time of projectile.
     */
    public final int getSlowDownTime() { return slowDownTime; }

    @Override
    protected void damageTarget() {
        super.damageTarget();

        if (!target.hasDied()) {
            target.addStatusEffect(new StatusEffect(StatusEffect.EffectType.Slow, slowDownTime));
        }
    }
}
