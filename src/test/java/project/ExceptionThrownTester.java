package project;

import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Helper class to test whether an exception is thrown.
 */
public final class ExceptionThrownTester {
    private ExceptionThrownTester() { }

    /**
     * Tests whether a specified exception is thrown when invoking a constructor. If not, an {@link AssertionError} is thrown.
     * @param exceptionType The type of the exception that should be thrown.
     * @param objType The type of the object to invoke the constructor on.
     * @param argTypes The types of each argument to pass to the constructor, in order.
     * @param args The arguments to pass to the constructor, in order.
     */
    public static void assertExceptionThrown_constructor(Class<?> exceptionType, Class<?> objType, Class<?>[] argTypes, Object[] args) {
        if (argTypes.length != args.length) {
            throw new IllegalArgumentException("The argTypes and args array lengths do not match.");
        }

        Constructor<?> c = null;

        try {
            c = objType.getDeclaredConstructor(argTypes);
            c.setAccessible(true);
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException) {
                fail(String.format("The constructor with arguments %s does not exist.", Arrays.toString(args)));
                return;
            }

            if (e instanceof SecurityException) {
                fail("A security violation was made.");
                return;
            }
        }

        try {
            c.newInstance(args);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                if (e.getCause().getClass() == exceptionType) {
                    return;
                }
            }

            fail(String.format("The incorrect exception was thrown for constructor with arguments %s", Arrays.toString(args)));
            return;
        }

        fail(String.format("No exception was thrown for the constructor with arguments %s", Arrays.toString(args)));
    }

    /**
     * Tests whether a specified exception is thrown when invoking a constructor. If not, an {@link AssertionError} is thrown.
     * <b>Only use this for methods that do not accept any primitive arguments.</b>
     * @param exceptionType The type of the exception that should be thrown.
     * @param objType The type of the object to invoke the constructor on.
     * @param args The arguments to pass to the constructor, in order.
     */
    public static void assertExceptionThrown_constructor_objectOnly(Class<?> exceptionType, Class<?> objType, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) argTypes[i] = args[i].getClass();

        assertExceptionThrown_constructor(exceptionType, objType, argTypes, args);
    }

    /**
     * Tests whether a specified exception is thrown when invoking a method. If not, an {@link AssertionError} is thrown.
     * @param exceptionType The type of the exception that should be thrown.
     * @param obj The object to invoke the method on.
     * @param methodName The name of the method to invoke.
     * @param argTypes The types of each argument to pass to the method, in order.
     * @param args The arguments to pass to the method, in order.
     */
    public static void assertExceptionThrown_method(Class<?> exceptionType, Object obj, String methodName, Class<?>[] argTypes, Object[] args) {
        Method m = null;

        try {
            m = obj.getClass().getDeclaredMethod(methodName, argTypes);
            m.setAccessible(true);
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException) {
                fail(String.format("The method '%s' with arguments %s does not exist.", methodName, Arrays.toString(args)));
                return;
            }

            if (e instanceof NullPointerException) {
                fail("The methodName is null.");
                return;
            }

            if (e instanceof SecurityException) {
                fail("A security violation was made.");
                return;
            }
        }

        try {
            m.invoke(obj, args);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                if (e.getCause().getClass() == exceptionType) {
                    return;
                }
            }
            
            fail(String.format("The incorrect exception was thrown for constructor with arguments %s", Arrays.toString(args)));
            return;
        }

        fail(String.format("No exception was thrown for the method '%s' with arguments %s", methodName, Arrays.toString(args)));
    }

    /**
     * Tests whether a specified exception is thrown when invoking a method. If not, an {@link AssertionError} is thrown.
     * <b>Only use this for methods that do not accept any primitive arguments.</b>
     * @param exceptionType The type of the exception that should be thrown.
     * @param obj The object to invoke the method on.
     * @param methodName The name of the method to invoke.
     * @param args The arguments to pass to the method, in order.
     */
    public static void assertExceptionThrown_method_objectOnly(Class<?> exceptionType, Object obj, String methodName, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) argTypes[i] = args[i].getClass();

        assertExceptionThrown_method(exceptionType, obj, methodName, argTypes, args);
    }
}