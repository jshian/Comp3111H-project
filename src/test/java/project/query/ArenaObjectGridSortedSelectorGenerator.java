package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.paukov.combinatorics3.Generator;

import project.JavaFXTester;
import project.entity.Monster;

/**
 * Generates the {@link ArenaObjectGridSortedSelector}.
 */
public class ArenaObjectGridSortedSelectorGenerator implements ArenaObjectSortedSelectorGenerator {

    /**
     * {@inheritDoc}
     */
    @Override
    public ArenaObjectSortedSelector<Monster> generateSortedSelector(Object... args) {
        short x = (short) args[0];
        short y = (short) args[1];

        return new ArenaObjectGridSortedSelector<Monster>(x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateSelectorInfo(Object... args) {
        short x = (short) args[0];
        short y = (short) args[1];

        return String.format("x = %d, y = %d", x, y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> generateArgSets() {
        Object[][] randomParams = new Object[NUM_SELECTORS][];

        for (int i = 0; i < NUM_SELECTORS; i++) {
            randomParams[i] = new Object[] {
                JavaFXTester.RANDOM_X_COOR.get(),
                JavaFXTester.RANDOM_Y_COOR.get()
            };
        }

        List<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));

        Generator.combination(JavaFXTester.getCoordinateLengthGenerators())
            .simple(2)
            .stream()
            .forEach((o) -> totalParams.add(o.toArray()));

        return totalParams;
    }
}