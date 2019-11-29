package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.paukov.combinatorics3.Generator;

import project.entity.Monster;

/**
 * Tests the {@link ArenaObjectRectangleSortedSelector} class.
 */
public class ArenaObjectRectangleSortedSelectorTest extends ArenaObjectSortedSelectorTest {

    @Override
    protected ArenaObjectSortedSelector<Monster> createObject(Object... args) {
        short leftX = (short) args[0];
        short topY = (short) args[1];
        short width = (short) args[2];
        short height = (short) args[3];

        return new ArenaObjectRectangleSortedSelector<Monster>(leftX, topY, width, height);
    }

    @Override
    protected String createObjectInfo(Object... args) {
        short leftX = (short) args[0];
        short topY = (short) args[1];
        short width = (short) args[2];
        short height = (short) args[3];

        return String.format("leftX = %d, topY = %d, width = %d, height = %d", leftX, topY, width, height);
    }

    @Override
    protected List<Object[]> generateArgSets() {
        Object[][] randomParams = new Object[NUM_RANDOM_TEST_CASES][];

        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            randomParams[i] = new Object[] {
                RANDOM_X_COOR.get(),
                RANDOM_Y_COOR.get(),
                RANDOM_X_COOR.get(),
                RANDOM_Y_COOR.get()
            };
        }

        List<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));

        Generator.combination(getCoordinateLengthGenerators())
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