package project.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import project.entity.ArenaObject;

/**
 * Helper class for comparing collections.
 */
public final class CollectionComparator {

    private CollectionComparator() {}

    /**
     * Returns whether the set of elements of the two collections is equivalent.
     * @param <T> The type of the elements.
     * @param l1 The first collection.
     * @param l2 The second collection.
     * @return Whether the set of elements of the two collections is equivalent.
     */
    public static <T> boolean isElementSetEqual(Collection<T> l1, Collection<T> l2) {
        HashSet<T> h1 = new HashSet<T>(l1);
        HashSet<T> h2 = new HashSet<T>(l2);

        boolean isEquals = new HashSet<T>(h1).equals(new HashSet<T>(h2));
        if (!isEquals) {
            HashSet<T> h1_cpy = new HashSet<>(h1);
            HashSet<T> h2_cpy = new HashSet<>(h2);

            for (T o1 : h1) h2_cpy.remove(o1);
            for (T o2 : h2) h1_cpy.remove(o2);
            
            for (T o1 : h1_cpy) {
                if (o1 instanceof ArenaObject) {
                    System.err.println(String.format("Found extra element in l1: x = %d, y = %d", ((ArenaObject) o1).getX(), ((ArenaObject) o1).getY()));
                }
            }
            for (T o2 : h2_cpy) {
                if (o2 instanceof ArenaObject) {
                    System.err.println(String.format("Found extra element in l2: x = %d, y = %d", ((ArenaObject) o2).getX(), ((ArenaObject) o2).getY()));
                }
            }
        }

        return isEquals;
    }

    /**
     * Returns whether the set of elements of the two queues is equivalent and sorted in the same order.
     * Permits different ordering of elements that evaluate {@link Comparable#compareTo(Object)} as <code>0</code>.
     * @param <T> The type of the elements.
     * @param l1 The first collection.
     * @param l2 The second collection.
     * @return Whether the set of elements of the two queues is equivalent and sorted in the same order.
     */
    public static <T extends Comparable<T>> boolean isElementSetAndOrderEqual(List<T> q1, List<T> q2) {
        if (!isElementSetEqual(q1, q2)) return false;

        List<T> q1_cpy = new LinkedList<>(q1);
        List<T> q2_cpy = new LinkedList<>(q2);

        boolean isEqual = true;
        while (!q1_cpy.isEmpty()) {
            T e1 = q1_cpy.remove(0);
            T e2 = q2_cpy.remove(0);
            if (e1 != e2 && e1.compareTo(e2) != 0) {
                isEqual = false;
            }
        }

        if (!isEqual) {
            System.out.println("The ordering of elements is not equivalent");
            for (int i = 0; i < q1.size(); i++) {
                T e1 = q1.get(i);
                T e2 = q2.get(i);

                if (e1 instanceof ArenaObject && e2 instanceof ArenaObject) {
                    System.out.println(String.format("e1: (%d, %d); e2: (%d, %d)", ((ArenaObject) e1).getX(), ((ArenaObject) e1).getY(), ((ArenaObject) e2).getX(), ((ArenaObject) e2).getY()));
                }
            }
        }

        return isEqual;
    }

}