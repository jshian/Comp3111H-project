package project.monsters;

import javafx.scene.image.ImageView;
import project.*;

import javax.persistence.Entity;

import org.apache.commons.lang3.*;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Fox is the fastest Monster. It also tries to follow the path where it receives the minimum number of attacks from Towers.
 */
@Entity
public class Fox extends Monster {
    /**
     * @see Monster#Monster
     */
    public Fox(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination) {
        super(difficulty, start, destination);
        this.health = 5 * difficulty;
        this.speed = 1 + 0.01 * difficulty;
    }

    /**
     * @see Monster#Monster
     */
    public Fox(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination, @NonNull ImageView imageView) {
        super(difficulty, start, destination, imageView);
        this.health = 5 * difficulty;
        this.speed = 1 + 0.01 * difficulty;
    }
    /**
     * get class name.
     * @return class name.
     */
    @Override
    public String getClassName() { return "Fox"; }

    @Override
    public void recalculateFuturePath() {
        throw new NotImplementedException("TODO: A* Search");
    }
}