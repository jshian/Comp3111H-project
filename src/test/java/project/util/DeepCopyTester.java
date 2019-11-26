package project.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Helper class to test deep copying of objects.
 */
public final class DeepCopyTester {
    private DeepCopyTester() { }

    /**
     * Tests whether two objects are deep copies of each other. If not, an {@link AssertionError} is thrown.
     * Recursively accesses each collection to also check the objects inside them.
     * @param <T> The type of the objects.
     * @param o1 The first object.
     * @param o2 The second object.
     */
    public static <T> void assertDeepCopy(T o1, T o2) {
        assertDeepCopy(o1, o2, Short.MAX_VALUE);
    }

    /**
     * Tests whether two objects are deep copies of each other. If not, an {@link AssertionError} is thrown.
     * Recursively accesses each collection to also check the objects inside them.
     * @param <T> The type of the objects.
     * @param o1 The first object.
     * @param o2 The second object.
     * @param n The maximum number of layers of recursions to perform. For example, a value of <code>0</code> means to only check the collection itself. A value of <code>1</code> means to check the objects in the collection, but if those objects are themselves collections, it will not further check the objects inside them. A negative value is equivalent to <code>0</code>.
     */
    public static <T> void assertDeepCopy(T o1, T o2, short n) {
        assertDeepCopy(o1, o2, n, new LinkedList<Object>());
    }

    /**
     * Tests whether two objects are deep copies of each other. If not, an {@link AssertionError} is thrown.
     * Recursively accesses each collection to also check the objects inside them.
     * @param <T> The type of the objects.
     * @param o1 The first object.
     * @param o2 The second object.
     * @param n The maximum number of layers of recursions to perform. For example, a value of <code>0</code> means to only check the collection itself. A value of <code>1</code> means to check the objects in the collection, but if those objects are themselves collections, it will not further check the objects inside them. A negative value is equivalent to <code>0</code>.
     * @param seen A linked list containing a reference to all seen objects.
     */
    private static <T> void assertDeepCopy(T o1, T o2, short n, LinkedList<Object> seen) {
        try {
            // Prevent circular reference causing infinite loop
            if (seen.contains(o1) || seen.contains(o2)) return;
            seen.add(o1);
            seen.add(o2);

            Field[] fields = o1.getClass().getDeclaredFields();
            HashMap<Field, Object> fieldValueMap_original = new HashMap<>();
            for (Field f : fields) {
            	if (f.isSynthetic()) continue; // Only declared fields are considered
            	
                f.setAccessible(true);
                fieldValueMap_original.put(f, f.get(o1));
            }
            
            for (Field f : fieldValueMap_original.keySet()) {
                if (f.getType().isPrimitive()) {
                    assertEquals(String.format("Primitive field '%s' should be the same after deep copying", 
                        f.getName()), f.get(o1), f.get(o2));
                } else {
                    assertNotSame(String.format("Object field '%s' should be different after deep copying", 
                    f.getName()), f.get(o1), f.get(o2));
                    
                    if (n > 0 && Iterator.class.isAssignableFrom(f.getType())) {
                        for (Object i : (Iterable<?>)f.get(o1)) {
                            for (Object j : (Iterable<?>)f.get(o2)) {
                                LinkedList<Object> seen_copy = new LinkedList<>();
                                for (Object o : seen) seen_copy.add(o);
                                
                                assertDeepCopy(i, j, (short) (n - 1), seen_copy);
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            fail("Illegal access of field detected");
        }
    }
}