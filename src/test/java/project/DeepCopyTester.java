package project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Helper class to test deep copying of objects.
 */
public final class DeepCopyTester {
    private DeepCopyTester() { }

    /**
     * Tests whether two objects are deep copies of each other.
     * Recursively accesses each collection to also check the objects inside them.
     * @param <T> The type of the objects.
     * @param o1 The first object.
     * @param o2 The second object.
     * @param n The number of layers of objects in collections to check deep copies of. For example, a value of <code>0</code> means to only check the collection itself. A value of <code>1</code> means to check the objects in the collection, but if those objects are themselves collections, it will not further check the objects inside them. A negative value is equivalent to <code>0</code>.
     */
    public static <T> void testDeepCopy(T o1, T o2) {
        testDeepCopy(o1, o2, Short.MAX_VALUE);
    }

    /**
     * Tests whether two objects are deep copies of each other.
     * Recursively accesses each collection to also check the objects inside them.
     * @param <T> The type of the objects.
     * @param o1 The first object.
     * @param o2 The second object.
     * @param n The maximum number of layers of recursions to perform. For example, a value of <code>0</code> means to only check the collection itself. A value of <code>1</code> means to check the objects in the collection, but if those objects are themselves collections, it will not further check the objects inside them. A negative value is equivalent to <code>0</code>.
     */
    public static <T> void testDeepCopy(T o1, T o2, short n) {
        try {
            Field[] fields = o1.getClass().getDeclaredFields();
            HashMap<Field, Object> fieldValueMap_original = new HashMap<>();
            for (Field f : fields) {
                f.setAccessible(true);
                fieldValueMap_original.put(f, f.get(o1));
            }
            
            for (Field f : fieldValueMap_original.keySet()) {
                if (f.getType().isPrimitive()) {
                    assertEquals(String.format("Primitive field '%s' should be the same after deep copying", 
                        f.getName()), f.get(o1), f.get(o2));
                } else {
                    if (n > 0 && Iterator.class.isAssignableFrom(f.getType())) {
                        for (Object i : (Iterable<?>)f.get(o1)) {
                            for (Object j : (Iterable<?>)f.get(o2)) {
                                testDeepCopy(i, j, (short) (n - 1));
                            }
                        }
                    } else {
                        assertNotSame(String.format("Object field '%s' should be different after deep copying", 
                        f.getName()), f.get(o1), f.get(o2));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            fail("Illegal access of field detected");
        }
    }
}