package project.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Helper class to visualize numerical 2D arrays, as well as nested lists.
 * For debugging only.
 */
public final class ArrayVisualizer2D {
    private static double[][] openArray;

    private static JFrame openFrame;

    private ArrayVisualizer2D() { }

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
     * Visualizes a nested list of shorts as a heat map. Colors range from blue (smallest value) to red (largest value).
     * @param nestedList The nested list to visualize.
     */
    public static void visualizeShortNestedList(ArrayList<ArrayList<Short>> nestedList) {
        short[][] arr = new short[nestedList.size()][];

        for (int i = 0; i < nestedList.size(); i++) {
            arr[i] = new short[nestedList.get(i).size()];
            for (int j = 0; j < nestedList.get(i).size(); j++) {
                arr[i][j] = nestedList.get(i).get(j);
            }
        }

        visualizeShortArray(arr);
    }

    /**
     * Visualizes an array of shorts as a heat map. Colors range from blue (smallest value) to red (largest value).
     * @param arr The array to visualize.
     */
    public static void visualizeShortArray(short[][] arr) {
        int width = arr.length;
        double[][] doubles = new double[width][];

        // Get max and min values
        for (int i = 0; i < width; i++) {
            int height = arr[i].length;
            doubles[i] = new double[height];

            for (int j = 0; j < height; j++) {
                if (arr[i][j] == Short.MAX_VALUE) {
                    doubles[i][j] = Double.POSITIVE_INFINITY;
                } else if (arr[i][j] == Short.MIN_VALUE) {
                    doubles[i][j] = Double.NEGATIVE_INFINITY;
                } else {
                    doubles[i][j] = arr[i][j];
                }
            }
        }
        
        visualizeDoubleArray(doubles);
    }

    /**
     * Visualizes a nested list of integers as a heat map. Colors range from blue (smallest value) to red (largest value).
     * @param nestedList The nested list to visualize.
     */
    public static void visualizeIntNestedList(ArrayList<ArrayList<Integer>> nestedList) {
        int[][] arr = new int[nestedList.size()][];

        for (int i = 0; i < nestedList.size(); i++) {
            arr[i] = new int[nestedList.get(i).size()];
            for (int j = 0; j < nestedList.get(i).size(); j++) {
                arr[i][j] = nestedList.get(i).get(j);
            }
        }

        visualizeIntArray(arr);
    }

    /**
     * Visualizes an array of integers as a heat map. Colors range from blue (smallest value) to red (largest value).
     * @param arr The array to visualize.
     */
    public static void visualizeIntArray(int[][] arr) {
        int width = arr.length;
        double[][] doubles = new double[width][];

        // Get max and min values
        for (int i = 0; i < width; i++) {
            int height = arr[i].length;
            doubles[i] = new double[height];

            for (int j = 0; j < height; j++) {
                if (arr[i][j] == Integer.MAX_VALUE) {
                    doubles[i][j] = Double.POSITIVE_INFINITY;
                } else if (arr[i][j] == Integer.MIN_VALUE) {
                    doubles[i][j] = Double.NEGATIVE_INFINITY;
                } else {
                    doubles[i][j] = arr[i][j];
                }
            }
        }
        
        visualizeDoubleArray(doubles);
    }
    
    /**
     * Visualizes a nested list of doubles as a heat map. Colors range from blue (smallest value) to red (largest value).
     * @param nestedList The nested list to visualize.
     */
    public static void visualizeDoubleNestedList(ArrayList<ArrayList<Double>> nestedList) {
        double[][] arr = new double[nestedList.size()][];

        for (int i = 0; i < nestedList.size(); i++) {
            arr[i] = new double[nestedList.get(i).size()];
            for (int j = 0; j < nestedList.get(i).size(); j++) {
                arr[i][j] = nestedList.get(i).get(j);
            }
        }

        visualizeDoubleArray(arr);
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