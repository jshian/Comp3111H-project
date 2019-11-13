package project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Helper class to test deep copying of objects.
 */
public final class DeepCopyTester {
    private DeepCopyTester() { }

    /**
     * Tests whether two objects are deep copies of each other.
     * @param <T> The type of the objects.
     * @param o1 The first object.
     * @param o2 The second object.
     */
    public static <T> void testDeepCopy(T o1, T o2) {
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
                    assertNotSame(String.format("Object field '%s' should be different after deep copying", 
                        f.getName()), f.get(o1), f.get(o2));
                }
            }
        } catch (IllegalAccessException e) {
            fail("Illegal access of field detected");
        }
    }
}