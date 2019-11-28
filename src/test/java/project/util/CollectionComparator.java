package project.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import project.entity.ArenaObject;

/**
 * Helper class for comparing collections.
 */
public class CollectionComparator {

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
                    System.out.println(String.format("Found extra element in l1: x = %d, y = %d", ((ArenaObject) o1).getX(), ((ArenaObject) o1).getY()));
                }
            }
            for (T o2 : h2_cpy) {
                if (o2 instanceof ArenaObject) {
                    System.out.println(String.format("Found extra element in l2: x = %d, y = %d", ((ArenaObject) o2).getX(), ((ArenaObject) o2).getY()));
                }
            }
        }

        return isEquals;
    }

    /**
     * Returns whether the set of elements of the two queues is equivalent and sorted in the same order.
     * @param <T> The type of the elements.
     * @param l1 The first collection.
     * @param l2 The second collection.
     * @return Whether the set of elements of the two queues is equivalent and sorted in the same order.
     */
    public static <T> boolean isElementSetAndOrderEqual(Queue<T> q1, Queue<T> q2) {
        LinkedList<T> q1_cpy = new LinkedList<>(q1);
        LinkedList<T> q2_cpy = new LinkedList<>(q2);

        while (!q1_cpy.isEmpty()) {
            T e1 = q1_cpy.poll();
            if (q2_cpy.poll() != e1) return false;
        }

        return q2_cpy.isEmpty();
    }

}