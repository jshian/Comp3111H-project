package project.monsters;

import project.*;

import org.apache.commons.lang3.*;

/**
 * Fox is the fastest Monster. It also tries to follow the path where it receives the minimum number of attacks from Towers.
 */
public class Fox extends Monster {
    /**
     * @see Monster#Monster()
     */
    public Fox(double difficulty, Coordinates destination) {
        super(difficulty, destination);
        this.health = 5 * difficulty;
        this.speed = 1 + 0.01 * difficulty;
    }

    @Override
    public void recalculateFuturePath() {
        throw new NotImplementedException("TODO: A* Search");
    }
}