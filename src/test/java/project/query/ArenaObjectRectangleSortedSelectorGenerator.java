package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.paukov.combinatorics3.Generator;

import project.JavaFXTester;
import project.entity.Monster;

/**
 * Generates the {@link ArenaObjectRectangleSortedSelector}.
 */
public class ArenaObjectRectangleSortedSelectorGenerator implements ArenaObjectSortedSelectorGenerator {

    @Override
    public ArenaObjectSortedSelector<Monster> generateSortedSelector(Object... args) {
        short leftX = (short) args[0];
        short topY = (short) args[1];
        short width = (short) args[2];
        short height = (short) args[3];

        return new ArenaObjectRectangleSortedSelector<Monster>(leftX, topY, width, height);
    }

    @Override
    public String generateSelectorInfo(Object... args) {
        short leftX = (short) args[0];
        short topY = (short) args[1];
        short width = (short) args[2];
        short height = (short) args[3];

        return String.format("leftX = %d, topY = %d, width = %d, height = %d", leftX, topY, width, height);
    }

    @Override
    public List<Object[]> generateArgSets() {
        Object[][] randomParams = new Object[NUM_SELECTORS][];

        for (int i = 0; i < NUM_SELECTORS; i++) {
            randomParams[i] = new Object[] {
                JavaFXTester.RANDOM_X_COOR.get(),
                JavaFXTester.RANDOM_Y_COOR.get(),
                JavaFXTester.RANDOM_X_COOR.get(),
                JavaFXTester.RANDOM_Y_COOR.get()
            };
        }

        List<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));

        Generator.combination(JavaFXTester.getCoordinateLengthGenerators())
            .simple(4)
            .stream()
            .forEach((o) -> totalParams.add(o.toArray()));

        return totalParams;
    }
}