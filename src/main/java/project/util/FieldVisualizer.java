package project.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import project.control.ArenaManager;
import project.field.ArenaScalarField;

/**
 * Helper class to visualize numerical 2D arrays.
 * For debugging only.
 */
public final class FieldVisualizer {
    private static double[][] openArray;

    private static JFrame openFrame;

    private FieldVisualizer() { }

    private static void display(BufferedImage img) {
        if (openFrame != null) openFrame.dispose();

        JFrame frame = new JFrame("Heat Map");
        JLabel label = new JLabel(new ImageIcon(img));
        for (int i = 0; i < openArray.length; i += 40) {
            for (int j = 0; j < openArray[i].length; j += 40) {
                img.setRGB(i, j, Color.BLACK.getRGB());
                JLabel temp = new JLabel(String.format("%.2f", openArray[i][j]));
                temp.setSize(40, 20);
                temp.setFont(temp.getFont().deriveFont(8.0f));
                label.add(temp);
                temp.setLocation(i, j);
            }
        }
        frame.add(label);
        frame.pack();
        frame.setVisible(true);

        openFrame = frame;
    }

    /**
     * Visualizes a scalar field.
     * @param <T> The type of number stored in the scalar field.
     * @param type The type of number stored in the scalar field.
     * @param field The scalar field to visualize.
     */
    public static <T extends Number & Comparable<T>> void visualizeArenaScalarField(Class<T> type, ArenaScalarField<T> field) {
        Number minValue = null;
        Number maxValue = null;

        if (Short.class.isAssignableFrom(type)) {
            minValue = Short.MIN_VALUE;
            maxValue = Short.MAX_VALUE;
        } else if (Integer.class.isAssignableFrom(type)) {
            minValue = Integer.MIN_VALUE;
            maxValue = Integer.MAX_VALUE;
        } else if (Float.class.isAssignableFrom(type)) {
            minValue = Float.NEGATIVE_INFINITY;
            maxValue = Float.POSITIVE_INFINITY;
        } else if (Double.class.isAssignableFrom(type)) {
            minValue = Double.NEGATIVE_INFINITY;
            maxValue = Double.POSITIVE_INFINITY;
        } else {
            System.err.println("The input type is not supported");
            System.err.println("Currently supported types: short, int, float, double");
            return;
        }

        double[][] doubles = new double[ArenaManager.ARENA_WIDTH + 1][ArenaManager.ARENA_HEIGHT + 1];
        
        for (short x = 0; x < ArenaManager.ARENA_WIDTH + 1; x++) {
            for (short y = 0; y < ArenaManager.ARENA_HEIGHT + 1; y++) {
                T value = field.getValueAt(x, y);

                if (value == minValue) {
                    doubles[x][y] = Double.NEGATIVE_INFINITY;
                } else if (value == maxValue) {
                    doubles[x][y] = Double.POSITIVE_INFINITY;
                } else {
                    doubles[x][y] = value.doubleValue();
                }
            }
        }

        visualizeDoubleArray(doubles);
    }

    /**
     * Visualizes an array of doubles as a heat map. Colors range from blue (smallest value) to red (largest value).
     * @param arr The array to visualize.
     */
    public static void visualizeDoubleArray(double[][] arr) {
        double maxValue = 0;
        double minValue = 0;
        int width = arr.length;
        int height = 0;

        // Get max and min values
        for (int i = 0; i < width; i++) {
            if (arr[i].length > height) height = arr[i].length;

            for (int j = 0; j < height; j++) {
                double value = arr[i][j];

                if (value != Double.POSITIVE_INFINITY && value != Double.NEGATIVE_INFINITY) {
                    if (value < minValue) minValue = value;
                    if (value > maxValue) maxValue = value;
                }
            }
        }

        // Initialize the display array
        openArray = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                openArray[i][j] = Double.NEGATIVE_INFINITY;
            }
        }

        // Create the heat map
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                Color color;
                double value = arr[i][j];
                openArray[i][j] = value;

                if (value == Double.POSITIVE_INFINITY) {
                    color = Color.WHITE;
                } else if (value == Double.NEGATIVE_INFINITY) {
                    color = Color.BLACK;
                } else {
                    // The +0.001 is to avoid division by zero
                    color = Color.getHSBColor(2f / 3f - (float)(value - minValue) / (float)(maxValue - minValue + 0.001) * 2 / 3, 1, 1);
                }

                img.setRGB(i, j, color.getRGB());
            }
        }

        // Display the heat map
        display(img);
    }
}