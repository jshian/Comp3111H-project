package project.query;

import java.util.List;

/**
 * Interface for classes that generate {@link ArenaObjectSelector}s.
 */
interface ArenaObjectSelectorGenerator {

    /**
     * The number of selectors generated.
     */
    static int NUM_SELECTORS = 40;

    /**
     * Generates the argument sets for the test {@link ArenaObjectSelector}.
     */
    abstract List<Object[]> generateArgSets();

    /**
     * Generates an {@link ArenaObjectSelector}.
     * @param args The arguments to pass to the constructor.
     */
    abstract ArenaObjectSelector generateSortedSelector(Object... args);

    /**
     * Generates object information for an {@link ArenaObjectSelector}.
     * @param args The arguments passed to the constructor of that object.
     */
    abstract String generateSelectorInfo(Object... args);

}