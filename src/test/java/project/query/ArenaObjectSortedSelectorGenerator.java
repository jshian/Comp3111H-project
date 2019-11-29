package project.query;

import java.util.List;

import project.entity.Monster;

/**
 * Interface for classes that generate {@link ArenaObjectSortedSelector}s.
 */
interface ArenaObjectSortedSelectorGenerator {

    /**
     * The number of selectors generated.
     */
    static int NUM_SELECTORS = 40;

    /**
     * Generates the argument sets for the test {@link ArenaObjectSortedSelector}s.
     */
    abstract List<Object[]> generateArgSets();

    /**
     * Generates an {@link ArenaObjectSortedSelector} for that object.
     * @param args The arguments to pass to the constructor.
     */
    abstract ArenaObjectSortedSelector<Monster> generateSortedSelector(Object... args);

    /**
     * Generates object information for an {@link ArenaObjectSortedSelector}.
     * @param args The arguments passed to the constructor of that object.
     */
    abstract String generateSelectorInfo(Object... args);

}