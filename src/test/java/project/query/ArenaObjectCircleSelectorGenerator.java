package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.paukov.combinatorics3.Generator;

import project.JavaFXTester;

/**
 * Generates the {@link ArenaObjectCircleSelector}.
 */
public class ArenaObjectCircleSelectorGenerator implements ArenaObjectSelectorGenerator {

    @Override
    public ArenaObjectSelector generateSortedSelector(Object... args) {
        short centerX = (short) args[0];
        short centerY = (short) args[1];
        short radius = (short) args[2];

        return new ArenaObjectCircleSelector(centerX, centerY, radius);
    }

    @Override
    public String generateSelectorInfo(Object... args) {
        short centerX = (short) args[0];
        short centerY = (short) args[1];
        short radius = (short) args[2];

        return String.format("centerX = %d, centerY = %d, radius = %d", centerX, centerY, radius);
    }

    @Override
    public List<Object[]> generateArgSets() {
        Object[][] randomParams = new Object[NUM_SELECTORS][];

        for (int i = 0; i < NUM_SELECTORS; i++) {
            randomParams[i] = new Object[] {
                JavaFXTester.RANDOM_X_COOR.get(),
                JavaFXTester.RANDOM_Y_COOR.get(),
                JavaFXTester.RANDOM_RADIUS.get(),
            };
        }

        List<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));

        Generator.combination(JavaFXTester.getCoordinateRadiusGenerators())
            .simple(3)
            .stream()
            .forEach((o) -> totalParams.add(o.toArray()));

        return totalParams;
    }
}