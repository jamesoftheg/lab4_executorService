import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Code modified from: http://www.hameister.org/JavaFX_MandelbrotSet.html
 * This is the background worker thread.
 * Needs method and interface.
 */
public class ConvergenceCalculation implements Callable {

    private GraphicsContext canvas;
    private int xStart;
    private int xEnd;
    private double cValue;
    private double precisionValue;
    private double imMin;

    /**
     * Each ConvergenceCalculation constructor will need:
     * Starting x position
     * Ending x position
     * Starting value of c - colour
     * Precision value - consistent for all threads. You can move the calculation into Start, calculate once and pass to the thread.
     */
    public ConvergenceCalculation(GraphicsContext canvas, int xStart, int xEnd, double cValue, double precisionValue, double imMin) {
        this.canvas = canvas;
        this.xStart = xStart;
        this.xEnd = xEnd;
        this.cValue = cValue;
        this.precisionValue = precisionValue;
        this.imMin = imMin;
    }

    /**
     * Method to calculate the color of each pixel on the screen for the Mandelbrot
     *
     * This needs to be adjusted to return a data structure of pixel objects.
     * Also needs the bounds of the for loop that controls c and x.
     */
    public ArrayList<Pixels> paintSet() {
        // From inside method - GraphicsContext ctx, double reMin, double reMax, double imMin, double imMax
        // double precision = Math.max((reMax - reMin) / Assignment4Starter.CANVAS_WIDTH, (imMax - imMin) / Assignment4Starter.CANVAS_HEIGHT);
        int convergenceSteps = 50;
        ArrayList<Pixels> pixelArray = new ArrayList<>();

        // GOTTA MODIFY THE BOUNDS OF THE OUTER LOOP

        // Outer for loop is controlling the x position (xR) and the convergence for the real number
        // As we move across the canvas, c is increasing.
        // For each thread, c is going to have an increased amount. A new start and end. We are slicing it up.
        // Make sure we start c at the correct value.
        for (double c = cValue, xR = xStart; xR < xEnd; c = c + precisionValue, xR++) {

            // Inner for loop is controlling the y position (yR) and the convergence for the imaginary number
            for (double ci = imMin, yR = 0; yR < Assignment4Starter.CANVAS_HEIGHT; ci = ci + precisionValue, yR++) {

                double convergenceValue = checkConvergence(ci, c, convergenceSteps);  // check how many steps have occured towards convergence
                double t1 = (double) convergenceValue / convergenceSteps;  // calculate the ratio of the current convergent step compared to the complete step (50)
                double c1 = Math.min(255 * 2 * t1, 255);  // calculate the ratio red and blue components of the color
                double c2 = Math.max(255 * (2 * t1 - 1), 0);  // calculate the ratio for the green component of the color

                // Setting the pixels
                Color color;
                // Here we set the color of the pixel object.
                if (convergenceValue != convergenceSteps) {
                    // ctx.setFill(Color.color(c2 / 255.0, c1 / 255.0, c2 / 255.0));  // Draws the outer shades of green / black
                    color = Color.color(c2 / 255.0, c1 / 255.0, c2 / 255.0);
                } else {
                    // ctx.setFill(Color.PURPLE); // Convergence Color - we have completed convergence
                    color = Color.PURPLE;
                }
                // Create a new pixel
                Pixels pixel = new Pixels(xR, yR, color);
                // Add to the array of Pixels
                pixelArray.add(pixel);

                // ctx.fillRect(xR, yR, 1, 1);  // one pixel drawn (rectangle of 1 by 1)
            }
        }
        // Returning the array of Pixels
        return pixelArray;
    }

    /**
     * Checks the convergence of a coordinate (c, ci) The convergence factor
     * determines the color of the point.
     * @param c real number current value
     * @param ci imaginary number current value
     * @param convergenceSteps number of steps to converge on
     * @return Which ever is greater of the number of steps it takes to converge or the total convergence steps
     */
    private int checkConvergence(double ci, double c, int convergenceSteps) {
        double z = 0;
        double zi = 0;
        for (int i = 0; i < convergenceSteps; i++) {
            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);
            z = zT + c;
            zi = ziT + ci;

            if (z * z + zi * zi >= 4.0) {
                return i;
            }
        }
        return convergenceSteps;
    }

    @Override
    public Object call() throws Exception {
        return paintSet();
    }
}
