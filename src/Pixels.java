import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Pixels {

    // Each pixel is a 1x1 value with a color.
    private double xPosition;
    private double yPosition;
    private Color color;

    /**
     * Pixels constructor
     * @param xPosition x-position of pixel.
     * @param yPosition y-position of pixel.
     * @param color color of pixel.
     */
    public Pixels(double xPosition, double yPosition, Color color) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.color = color;
    }

    /**
     * Here we draw our 1x1 pixel
     * @param gc the canvas
     */
    public void draw(GraphicsContext gc) {
        // We create a 1x1 pixel of the passed color
        gc.setFill(color);
        gc.fillRect(xPosition, yPosition, 1, 1);
    }
}
