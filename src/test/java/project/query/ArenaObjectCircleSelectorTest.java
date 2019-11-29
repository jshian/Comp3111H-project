package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.paukov.combinatorics3.Generator;

/**
 * Tests the {@link ArenaObjectCircleSelector} class.
 */
public class ArenaObjectCircleSelectorTest extends ArenaObjectSelectorTest {

    @Override
    protected ArenaObjectSelector createObject(Object... args) {
        short centerX = (short) args[0];
        short centerY = (short) args[1];
        short radius = (short) args[2];

        return new ArenaObjectCircleSelector(centerX, centerY, radius);
    }

    @Override
    protected String createObjectInfo(Object... args) {
        short centerX = (short) args[0];
        short centerY = (short) args[1];
        short radius = (short) args[2];

        return String.format("centerX = %d, centerY = %d, radius = %d", centerX, centerY, radius);
    }

    @Override
    protected List<Object[]> generateArgSets() {
        Object[][] randomParams = new Object[NUM_RANDOM_TEST_CASES][];

        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            randomParams[i] = new Object[] {
                RANDOM_X_COOR.get(),
                RANDOM_Y_COOR.get(),
                RANDOM_RADIUS.get(),
            };
        }

        List<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));

        Generator.combination(getCoordinateRadiusGenerators())
            .simple(3)
            .stream()
            .forEach((o) -> totalParams.add(o.toArray()));

        return totalParams;
    }

    @Override
    @Test
    public void test() {
        super.test();
    }
}