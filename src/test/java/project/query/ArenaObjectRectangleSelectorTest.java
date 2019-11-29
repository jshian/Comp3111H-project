package project.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import project.control.ArenaManager;

/**
 * Tests the {@link ArenaObjectRectangleSelector} class.
 */
public class ArenaObjectRectangleSelectorTest extends ArenaObjectSelectorTest {

    @Override
    protected ArenaObjectSelector createObject(Object... args) {
        short leftX = (short) args[0];
        short topY = (short) args[1];
        short width = (short) args[2];
        short height = (short) args[3];

        return new ArenaObjectRectangleSelector(leftX, topY, width, height);
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

        Random rng = new Random();
        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            randomParams[i] = new Object[] {
                (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
                (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1),
                (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
                (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1)
            };
        }

        LinkedList<Object[]> boundaryParams = new LinkedList<>();
        boundaryParams.add(new Object[] { ZERO, ZERO, ZERO, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO, ZERO, ZERO });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT, ZERO, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, ZERO, ZERO });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ZERO, ZERO });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO, ZERO, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ZERO, ZERO });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT, ZERO, ZERO });
        boundaryParams.add(new Object[] { ZERO, ZERO, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ZERO, ZERO, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, ZERO, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });

        LinkedList<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));
        totalParams.addAll(boundaryParams);

        return totalParams;
    }

    @Override
    @Test
    public void test() {
        super.test();
    }
}