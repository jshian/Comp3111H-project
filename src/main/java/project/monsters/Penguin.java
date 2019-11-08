package project.monsters;

import javax.persistence.Entity;

import javafx.scene.image.ImageView;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.*;

/**
 * Penguin has the ability to regenerate.
 */
@Entity
public class Penguin extends Monster {
    /**
     * @see Monster#Monster(Arena, Coordinates, Coordinates, ImageView, double)
     */
    public Penguin(Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        super(arena, start, destination, imageView, difficulty);
        this.health = 7.5 * difficulty;
        this.speed = 0.75 + 0.0075 * difficulty;
    }

    /**
     * @see Monster#Monster(Monster)
     */
    public Penguin(Penguin other) {
        super(other);
    }
    
    @Override
    public Penguin deepCopy() {
        return new Penguin(this);
    }
    
    @Override
    public String getClassName() { return "Penguin"; }

    /**
     * get the coordinate of monster in next frame.
     * @return coordinate of monster in next frame.
     */
    @Override
    public Coordinates getNextFrame() {
        this.health += 0.1;
        return !futurePath.isEmpty() ? futurePath.removeFirst() : null;
    }

    @Override
    public void moveOneFrame() {
        super.moveOneFrame();
        this.health += 0.1;
    }
}