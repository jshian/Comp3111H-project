package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.paukov.combinatorics3.Generator;

import project.entity.Monster;

/**
 * Tests the {@link ArenaObjectGridSortedSelector} class.
 */
public class ArenaObjectGridSortedSelectorTest extends ArenaObjectSortedSelectorTest {

    @Override
    protected ArenaObjectSortedSelector<Monster> createObject(Object... args) {
        short x = (short) args[0];
        short y = (short) args[1];

        return new ArenaObjectGridSortedSelector<Monster>(x, y);
    }

    @Override
    protected String createObjectInfo(Object... args) {
        short x = (short) args[0];
        short y = (short) args[1];

        return String.format("x = %d, y = %d", x, y);
    }

    @Override
    protected List<Object[]> generateArgSets() {
        Object[][] randomParams = new Object[NUM_RANDOM_TEST_CASES][];

        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            randomParams[i] = new Object[] {
                RANDOM_X_COOR.get(),
                RANDOM_Y_COOR.get()
            };
        }

        List<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));

        Generator.combination(getCoordinateLengthGenerators())
            .simple(2)
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