package MatrixTransformation.vector;

/**
 * Used from slides, unchanged.  This is an interface for a Drawable Object.  We can update its position in the world and draw it to
 * the screen.
 * 
 * @author  Patrick Cavanaugh (used by Al Ohlinger)
 */
import java.awt.Graphics;

public interface Drawable {
    void updateWorld(); //Update the World Matrix
    void render(Graphics g); //Draw the object with passed Graphics
}