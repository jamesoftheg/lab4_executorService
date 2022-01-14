import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Code modified from: http://www.hameister.org/JavaFX_MandelbrotSet.html
 */


public class Assignment4Starter  extends Application {
    // Size of the canvas for the Mandelbrot set
    public static final int CANVAS_WIDTH = 700;
    public static final int CANVAS_HEIGHT = 600;

    // Values for the Mandelbrot set
    private static double MANDELBROT_RE_MIN = -2;   // Real Number Minimum Value for this Mandelbrot
    private static double MANDELBROT_RE_MAX = 1;    // Real Number Maximum Value for this Mandelbrot
    private static double MANDELBROT_IM_MIN = -1.2; // Imaginary Number Minimum Value for this Mandelbrot
    private static double MANDELBROT_IM_MAX = 1.2;  // Imaginary Number Maximum Value for this Mandelbrot

    // Inside the start method we:
    // Determine number of threads to run
    // The range for each thread to calculate on
    // Get the results using a future object - Matrix multiplication, Week 7
    // Draw all the pixels to the Canvas
    @Override
    public void start(Stage primaryStage) throws ExecutionException, InterruptedException {
        Pane fractalRootPane = new Pane();
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        // Precision
        // double precision = Math.max((reMax - reMin) / Assignment4Starter.CANVAS_WIDTH, (imMax - imMin) / Assignment4Starter.CANVAS_HEIGHT);
        double precision = Math.max((MANDELBROT_RE_MAX - MANDELBROT_RE_MIN) / Assignment4Starter.CANVAS_WIDTH, (MANDELBROT_IM_MAX - MANDELBROT_IM_MIN) / Assignment4Starter.CANVAS_HEIGHT);

        // Calculate the number of threads appropriate for the application and system.
        // Determine number of threads to run.
        final int threadCount = Runtime.getRuntime().availableProcessors();

        // Create our thread pool
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);

        // Determine range for each thread to calculate on.
        // We are creating columns across the width of our canvas.
        int threadRange = (int)Math.ceil((double)CANVAS_WIDTH / threadCount);

        // Collect the results using a Future object.
        Future<ArrayList<Pixels>>[] futures = new Future[threadCount];

        // Calculate the convergence for these starting values

        // Pull this into the ConvergenceCalc?
        // ConvergenceCalculation p = new ConvergenceCalculation();
        // Calling on the paintSet in ConvergenceCalculation
        // p.paintSet(canvas.getGraphicsContext2D(),
        //         MANDELBROT_RE_MIN,
        //         MANDELBROT_RE_MAX,
        //         MANDELBROT_IM_MIN,
        //         MANDELBROT_IM_MAX);

        // Draw all pixels to Canvas
        for(int i = 0; i < threadCount; i++) {
            // Threads
            int start = Math.min(i * threadRange, CANVAS_WIDTH);
            int end = Math.min((i+1) * threadRange, CANVAS_WIDTH);
            // System.out.println(threadRange);
            double cValue = MANDELBROT_RE_MIN + ((i * threadRange) * precision);
            // System.out.println(cValue);
            // GraphicsContext canvas, int xStart, int xEnd, double cValue, double precisionValue
            ConvergenceCalculation c = new ConvergenceCalculation(canvas.getGraphicsContext2D(), start, end, cValue, precision, MANDELBROT_IM_MIN);
            futures[i] = threadPool.submit(c);
        }

        for (int i = 0; i < futures.length; i++) {
            try {
                ArrayList<Pixels> pixels = futures[i].get();
                for (Pixels pixel : pixels) {
                    pixel.draw(canvas.getGraphicsContext2D());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        threadPool.shutdown();

        fractalRootPane.getChildren().add(canvas);
        Scene scene = new Scene(fractalRootPane, CANVAS_WIDTH, CANVAS_HEIGHT);
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("Mandelbrot Set");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
