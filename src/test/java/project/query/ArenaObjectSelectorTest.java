package project.query;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;

/**
 * Tests the classes that implement {@link ArenaObjectSelector} and {@link ArenaObjectSortedSelector}.
 */
public class ArenaObjectSelectorTest extends JavaFXTester {
    // The objects to be tested
    private List<Tower> expectedTowers = new LinkedList<>();
    private List<Monster> expectedMonsters = new LinkedList<>();
    private List<Projectile> expectedProjectiles = new LinkedList<>();

    // Number of random test cases
    private static int NUM_RANDOM_TEST_CASES = 10;
    private static int NUM_RANDOM_ADDITIONS_PER_RUN = 100;

    private void reset() {
        System.out.println("Resetting...");
        expectedTowers.clear();
        expectedMonsters.clear();
        expectedProjectiles.clear();
        ArenaManager.getActiveObjectStorage().clear();
        System.out.println("Reset complete");
    }

    @Test
    public void testGeneralCases() {
        ArenaObjectStorageHelper.disableScalarFieldUpdates();
        
        ArenaObjectCircleSelector circleSelector;
        ArenaObjectCircleSortedSelector<Monster> circleSortedSelector;

        ArenaObjectGridSelector gridSelector;
        ArenaObjectGridSortedSelector<Monster> gridSortedSelector;

        ArenaObjectPropertySelector<Tower> propertySelector;
        ArenaObjectPropertySortedSelector<Monster> propertySortedSelector;

        ArenaObjectRectangleSelector rectangleSelector;
        ArenaObjectRectangleSortedSelector<Monster> rectangleSortedSelector;

        ArenaObjectRingSelector ringSelector;
        ArenaObjectRingSortedSelector<Monster> ringSortedSelector;

        List<Tower> towers = new LinkedList<>();
        List<Monster> monsters = new LinkedList<>();
        List<Projectile> projectiles = new LinkedList<>();
        for (int n = 0; n < NUM_RANDOM_ADDITIONS_PER_RUN; n++) {
            Tower t = ArenaObjectStorageHelper.addTower(this);
            
            Monster m = ArenaObjectStorageHelper.addMonster(this);

            Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m);
        }
    }
}