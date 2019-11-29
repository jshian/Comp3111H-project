package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.paukov.combinatorics3.Generator;

import project.JavaFXTester;

/**
 * Generates the {@link ArenaObjectRingSelector}.
 */
public class ArenaObjectRingSelectorGenerator implements ArenaObjectSelectorGenerator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ArenaObjectSelector generateSortedSelector(Object... args) {
        short centerX = (short) args[0];
        short centerY = (short) args[1];
        short minRadius = (short) args[2];
        short maxRadius = (short) args[3];

        return new ArenaObjectRingSelector(centerX, centerY, minRadius, maxRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateSelectorInfo(Object... args) {
        short centerX = (short) args[0];
        short centerY = (short) args[1];
        short minRadius = (short) args[2];
        short maxRadius = (short) args[3];

        return String.format("centerX = %d, centerY = %d, minRadius = %d, maxRadius = %d", centerX, centerY, minRadius, maxRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> generateArgSets() {
        Object[][] randomParams = new Object[NUM_SELECTORS][];

        for (int i = 0; i < NUM_SELECTORS; i++) {
            short r1 = JavaFXTester.RANDOM_RADIUS.get();
            short r2 = JavaFXTester.RANDOM_RADIUS.get();
            randomParams[i] = new Object[] {
                JavaFXTester.RANDOM_X_COOR.get(),
                JavaFXTester.RANDOM_Y_COOR.get(),
                (short) Math.min(r1, r2),
                (short) Math.max(r1, r2)
            };
        }

        List<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));

        Generator.combination(JavaFXTester.getCoordinateRadiusGenerators())
            .simple(4)
            .stream()
            .forEach((o) -> totalParams.add(o.toArray()));

        return totalParams;
    }
}