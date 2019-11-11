package project.debug;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Helper class to visualize numerical arrays.
 * For debugging only.
 */
public final class FieldVisualizer {
    private static JFrame openFrame;

    private FieldVisualizer() { }

    /**
     * Visualizes an array of integers as a heat map. Colors range from blue (smallest value) to red (largest value).
     * @param arr The array to visualize.
     */
    public static void visualizeIntArray(int[][] arr) {
        int maxValue = 0;
        int minValue = 0;
        int width = arr.length;
        int height = 0;

        // Get max and min values
        for (int i = 0; i < width; i++) {
            if (arr[i].length > height) height = arr[i].length;

            for (int j = 0; j < height; j++) {
                int value = arr[i][j];

                if (value != Integer.MAX_VALUE && value != Integer.MIN_VALUE) {
                    if (value < minValue) minValue = value;
                    if (value > maxValue) maxValue = value;
                }
            }
        }

        // Create the heat map
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color;
                int value = arr[i][j];

                if (value == Integer.MAX_VALUE) {
                    color = Color.WHITE;
                } else if (value == Integer.MIN_VALUE) {
                    color = Color.BLACK;
                } else {
                    // The +1 is to avoid division by zero
                    color = Color.getHSBColor(2f / 3f - (float)(value - minValue) / (float)(maxValue - minValue + 1) * 2 / 3, 1, 1);
                }

                img.setRGB(i, j, color.getRGB());
            }
        }

        if (openFrame != null) openFrame.dispose();

        // Display the heat map
        JFrame f = new JFrame("Heat Map");
        f.add(new JLabel(new ImageIcon(img)));
        f.pack();
        f.setVisible(true);

        openFrame = f;
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

                if (value != Integer.MAX_VALUE && value != Integer.MIN_VALUE) {
                    if (value < minValue) minValue = value;
                    if (value > maxValue) maxValue = value;
                }
            }
        }

        // Create the heat map
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color;
                double value = arr[i][j];

                if (value == Integer.MAX_VALUE) {
                    color = Color.WHITE;
                } else if (value == Integer.MIN_VALUE) {
                    color = Color.BLACK;
                } else {
                    // The +0.001 is to avoid division by zero
                    color = Color.getHSBColor(2f / 3f - (float)(value - minValue) / (float)(maxValue - minValue + 0.001) * 2 / 3, 1, 1);
                }

                img.setRGB(i, j, color.getRGB());
            }
        }

        if (openFrame != null) openFrame.dispose();

        // Display the heat map
        JFrame f = new JFrame("Heat Map");
        f.add(new JLabel(new ImageIcon(img)));
        f.pack();
        f.setVisible(true);

        openFrame = f;
    }
}