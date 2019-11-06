package project.monsters;

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
     * @see Monster#Monster(double, Coordinates, Coordinates)
     */
    public Fox(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination) {
        super(difficulty, start, destination);
        this.health = 5 * difficulty;
        this.speed = 1 + 0.01 * difficulty;
    }

    @Override
    public void recalculateFuturePath() {
        futurePath = Arena.findPathToEndZone(new Coordinates(getX(), getY()), false);
    }
}