package project.monsters;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.*;

/**
 * Penguin has the ability to regenerate.
 */
@Entity
public class Penguin extends Monster {
    /**
     * @see Monster#Monster
     */
    public Penguin(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination) {
        super(difficulty, start, destination);
        this.health = 7.5 * difficulty;
        this.speed = 0.75 + 0.0075 * difficulty;
    }

    @Override
    public void MoveOneFrame() {
        super.MoveOneFrame();
        this.health += 0.1;
    }
}