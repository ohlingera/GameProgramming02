package MatrixTransformation.util;

import MatrixTransformation.vector.*;
import java.awt.Graphics;
import java.awt.Point;

/**
 * VectorObject is a class that takes a series of points connected as an object and updates them in the game world as translations,
 * rotations, scaling, and shearing are all applied to them.
 * 
 * @author Al Ohlinger with starter code from the text by Timothy Wright
 */
public class VectorObject implements Drawable {
    
    private static final int SCREEN_W = 1280;   // The screen width in pixels
    private static final int SCREEN_H = 720;    // The screen height in pixels
    public Vector2f[] polygon;                  // An array that holds the points of the object (screen coordinates)
    private final Vector2f[] world;             // An array that holds the points of the object (world coordinates)
    private final float width;                  // The width of the object
    private final float height;                 // The height of the object
    public float tx, ty;                        // Used to help translate the object
    public float vx, vy;                        // Used to help translate the object
    public float rot, rotStep;                  // Used to help rotate the object
    public float scale, scaleStep;              // Used to help scale the object
    public float sx, sxStep;                    // Used to help shear the object
    public float sy, syStep;                    // Used to help shear the object

    /**
     * This VectorObject initializer reads in an array of points and stores them to polygon as a Vector2f array.  It also
     * initializes width, height, world array, and the rest of the vectoring variables.
     * 
     * @param   myPoints    The array of points to draw the object with
     * @param   width       The width of the object in pixels
     * @param   height      The height of the object in pixels
     */
    public VectorObject(Point[] myPoints, float width, float height) {
        polygon = new Vector2f[myPoints.length];
        this.width = width;
        this.height = height;
        for (int i = 0; i < myPoints.length; i++) {
            polygon[i] = new Vector2f(myPoints[i].x, myPoints[i].y);
        }
        world = new Vector2f[polygon.length];
        initialize();
    }
    
    /**
     * This methods initializes the rest of the variables used for vectoring.
     */
    private void initialize() {
	tx = SCREEN_W / 2;
	ty = SCREEN_H / 2;
	vx = 2;
        vy = 2;
	rot = 0.0f;
	rotStep = (float) Math.toRadians(2.0);
	scale = 1.0f;
	scaleStep = 0.1f;
	sx = sy = 0.0f;
	sxStep = syStep = 0.01f;
    }

    /**
     * This method changes the screen coordinates of the current object into world coordinates as well as updates any changes made
     * to the object by calling the world's vectoring methods.  (Rotate, etc.)
     */
    @Override
    public void updateWorld() {
        // Change to world coordinates
        for (int i = 0; i < polygon.length; ++i) {
            world[i] = new Vector2f(polygon[i]);
	}
        // Apply vectoring
        for (int i = 0; i < world.length; ++i) {
            world[i].shear(sx, sy);
            world[i].scale(scale, scale);
            world[i].rotate(rot);
            world[i].translate(tx, ty);
	}
    }

    /**
     * Draw the current object to the screen in the correct location by connecting the points of the object using lines.
     * 
     * @param   g   The graphics tool
     */
    @Override
    public void render(Graphics g) {
        // First point
        Vector2f S = world[world.length - 1];
	// Second point
        Vector2f P;
	for (int i = 0; i < world.length; ++i) {
            P = world[i];
            //Draw a line between the two points
            g.drawLine((int) S.x, (int) S.y, (int) P.x, (int) P.y);
            S = P;
	}
    }
    
    /**
     * This method scales an object.  The amount passed in is how much to change how much we're scaling by.
     * 
     * @param   s   The amount to scale the object by
     */
    public void scale(float s) {
        scaleStep += s;
        scale += scaleStep;
    }
    
    /**
     * This method rotates an object.  The amount passed in is how much to change how much we're rotating by.
     * 
     * @param   r   The amount to rotate the object by
     */
    public void rotate (float r) {
        rotStep += r;
        rot += rotStep;
    }
    
    /**
     * This method automatically translates an object and makes it bounce around a window like a screen saver.  It was created
     * specially for the square object in the MatrixTransformation application because I couldn't get it to 'bounce' properly using
     * the translate method.
     */
    public void autoTranslate() {
        tx += vx;
        if (tx < width || tx > SCREEN_W - width) {
            vx = -vx;
        }
        ty += vy;
        if (ty < height || ty > SCREEN_H - height) {
            vy = -vy;
        }
    }
    
    /**
     * This method translates an object.  The amount passed in is how much to change how much we're translating by.
     * 
     * @param   x   The amount to translate the object by on the x-axis
     * @param   y   The amount to translate the object by on the y-axis
     */
    public void translate(float x, float y) {
        tx += x;
        if (tx < width || tx > SCREEN_W - width) {
            tx -= x;
        }
        ty += y;
        if (ty < height || ty > SCREEN_H - height) {
            ty -= y;
        }
    }
    
    /**
     * This method shears an object on its x axis.  The amount passed in is how much to change how much we're shearing by.
     * 
     * @param   x   The amount to shear the object by
     */
    public void xShear(float x) {
        sxStep += x;
        sx += sxStep;
    }
    
    /**
     * This method shears an object on its y axis.  The amount passed in is how much to change how much we're shearing by.
     * 
     * @param   y   The amount to shear the object by
     */
    public void yShear(float y) {
        syStep += y;
        sy += syStep;
    }
}