package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.paukov.combinatorics3.Generator;

import project.entity.Monster;

/**
 * Tests the {@link ArenaObjectRingSortedSelector} class.
 */
public class ArenaObjectRingSortedSelectorTest extends ArenaObjectSortedSelectorTest {

    @Override
    protected ArenaObjectSortedSelector<Monster> createObject(Object... args) {
        short centerX = (short) args[0];
        short centerY = (short) args[1];
        short minRadius = (short) args[2];
        short maxRadius = (short) args[3];

        return new ArenaObjectRingSortedSelector<Monster>(centerX, centerY, minRadius, maxRadius);
    }

    @Override
    protected String createObjectInfo(Object... args) {
        short centerX = (short) args[0];
        short centerY = (short) args[1];
        short minRadius = (short) args[2];
        short maxRadius = (short) args[3];

        return String.format("centerX = %d, centerY = %d, minRadius = %d, maxRadius = %d", centerX, centerY, minRadius, maxRadius);
    }

    @Override
    protected List<Object[]> generateArgSets() {
        Object[][] randomParams = new Object[NUM_RANDOM_TEST_CASES][];

        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            short r1 = RANDOM_RADIUS.get();
            short r2 = RANDOM_RADIUS.get();
            randomParams[i] = new Object[] {
                RANDOM_X_COOR.get(),
                RANDOM_Y_COOR.get(),
                (short) Math.min(r1, r2),
                (short) Math.max(r1, r2)
            };
        }

        List<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));

        Generator.combination(getCoordinateRadiusGenerators())
            .simple(4)
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