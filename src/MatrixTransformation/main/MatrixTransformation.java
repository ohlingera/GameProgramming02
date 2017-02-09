package MatrixTransformation.main;

import MatrixTransformation.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * MatrixTransformation is the main driver class for the Matrix Transformation application.  It contains 3 VectorObjects that have
 * different properties.  The square automatically bounces around on its own like the Windows screen saver.  The hexagon can be
 * moved around using WASD controls.  It rotates, and its rotation can be sped up and slowed down, as well as switching the
 * direction it rotates.  The triangle is where the cursor is, and it can be made to rotate clockwise by clicking the right mouse
 * button and counterclockwise by clicking the left mouse button.
 * 
 * @author Al Ohlinger with starter code from the text by Timothy Wright
 */
public class MatrixTransformation extends JFrame implements Runnable {
    private static final int SCREEN_W = 1280;   // The screen width 
    private static final int SCREEN_H = 720;    // The screen height
    private BufferStrategy bs;                  // Used to help run the application
    private volatile boolean running;           // If the game is running or not
    private Thread gameThread;                  // The main thread for the application
    private RelativeMouseInput mouse;           // The mouse used to draw with
    private KeyboardInput keyboard;             // Get input from the keyboard
    private final Point prevPosition = new Point(SCREEN_W / 2, SCREEN_H / 2);   // The position the mouse was last at
    VectorObject square;    /* A square VectorObject, automatically moves and can't be interacted with */
    VectorObject hex;       /* A hexagon VectorObject, rotates automatically, can be moved using WASD, rotation can be changed with
                               QE and SPACE */
    VectorObject triangle;  /* A triangle VectorObject, moves automatically to the mouse position, can be rotated clicking right or
                               left mouse buttons*/

    /**
     * This is the constructor for the application.  It sets up the and creates the 3 objects in the game.
     */
    public MatrixTransformation() {
        // Set up the points to draw our object with
        Point[] myPointsS = {new Point(-20, 20), new Point (20, 20), new Point (20, -20), new Point (-20, -20)};
        Point[] myPointsH = {new Point(10, -17), new Point (-10, -17), new Point (-20, 0), new Point (-10, 17), new Point (10, 17),
                             new Point (20, 0)};
        Point[] myPointsT = {new Point(0, -20), new Point (-17, 10), new Point (17, 10)};
        // Create the objects using VectorObject, passing width and height as well.
        square = new VectorObject(myPointsS, 20, 20);
        hex = new VectorObject(myPointsH, 20, 20);
        triangle = new VectorObject(myPointsT, 20, 20);
        // Disable the default cursor
        disableCursor();
    }

    /**
     * This method is unchanged other than setting the canvas size, color, and title.
     */
    protected void createAndShowGUI() {
	Canvas canvas = new Canvas();
	canvas.setSize(SCREEN_W, SCREEN_H);
	canvas.setBackground(Color.WHITE);
	canvas.setIgnoreRepaint(true);
	getContentPane().add(canvas);
	setTitle("Matrix Transformation");
	setIgnoreRepaint(true);
	pack();
	// Add key listeners
	keyboard = new KeyboardInput();
	canvas.addKeyListener(keyboard);
	// Add mouse listeners
	// For full screen : mouse = new RelativeMouseInput( this );
	mouse = new RelativeMouseInput(canvas);
	canvas.addMouseListener(mouse);
	canvas.addMouseMotionListener(mouse);
	canvas.addMouseWheelListener(mouse);
	setVisible(true);
	canvas.createBufferStrategy(2);
	bs = canvas.getBufferStrategy();
	canvas.requestFocus();
	gameThread = new Thread(this);
	gameThread.start();
    }

    /**
     * This method is unchanged other than removing the call to FrameRate since we don't use that
     */
    public void run() {
	running = true;
	while (running) {
            gameLoop();
        }
    }
    
    /**
     * This method is unchanged
     */
    private void gameLoop() {
    	processInput();
	processObjects();
	renderFrame();
	sleep(10L);
    }

    /**
     * This method is unchanged
     */
    private void renderFrame() {
	do {
            do {
		Graphics g = null;
		try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
		} finally {
                    if (g != null) {
                        g.dispose();
                    }
		}
            } while (bs.contentsRestored());
		bs.show();
	} while (bs.contentsLost());
    }

    /**
     * This method is unchanged
     * 
     * @param sleep 
     */
    private void sleep(long sleep) {
	try {
            Thread.sleep(sleep);
	} catch (InterruptedException ex) {
            
	}
    }
    
    /**
     * This method is unchanged other than setting the cursor's location to the middle of the screen rather than 0, 0
     */
    private void disableCursor() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.createImage("");
        Point point = new Point(SCREEN_W / 2, SCREEN_H / 2);
        String name = "CanBeAnything";
        Cursor cursor = tk.createCustomCursor(image, point, name);
        setCursor(cursor);
    }
    
    /**
     * In this method, we get input from the user.  The triangle and hex objects can be interacted with. The hex is controlled with
     * the keyboard and the triangle is controlled with the mouse.
     */
    private void processInput() {
	// Get input
        keyboard.poll();
	mouse.poll();

        // I couldn't figure out how to get the square to 'bounce' using the regualar translate method so I used a special one
        square.autoTranslate();
        
        // Process mouse input
        triangle.translate(mouse.getPosition().x - prevPosition.x, mouse.getPosition().y - prevPosition.y);
        /* Move the cursor and its object to the correct position.  The if statements fix the problem where moving the cursor
           outside of the window and then back in at a different position messed up how the previous position relative to where
           we're drawing the object updates */
        if (mouse.getPosition().x >= 20 && mouse.getPosition().x <= SCREEN_W - 20) {
            prevPosition.x = (mouse.getPosition().x);
        }
        if (mouse.getPosition().y >= 20 && mouse.getPosition().y <= SCREEN_H - 20) {
            prevPosition.y = (mouse.getPosition().y);
        }
        // If the right mouse button is pressed, we rotate the cursor object clockwise
        if (mouse.buttonDownOnce(MouseEvent.BUTTON3) || mouse.buttonDown(MouseEvent.BUTTON3)) {
            if (triangle.rotStep < 0) {
                triangle.rotate(-triangle.rotStep * 2);
            }
            else {
                triangle.rotate(0);
            }
        }
        // If the left mouse button is pressed, we rotate the cursor object counterclockwise
        if (mouse.buttonDownOnce(MouseEvent.BUTTON1) || mouse.buttonDown(MouseEvent.BUTTON1)) {
            if (triangle.rotStep > 0) {
                triangle.rotate(-triangle.rotStep * 2);
            }
            else {
                triangle.rotate(0);
            }
        }
        
        // The hex object rotates automatically, clockwise initially
        hex.rotate(0);
        // If the W key is pressed, move the hex up
	if (keyboard.keyDownOnce(KeyEvent.VK_W) || keyboard.keyDown(KeyEvent.VK_W)) {
            hex.translate(0, -3);
        }
        // A Key pressed - move hex left
	if (keyboard.keyDownOnce(KeyEvent.VK_A) || keyboard.keyDown(KeyEvent.VK_A)) {
            hex.translate(-3, 0);
	}
        // S - down
	if (keyboard.keyDownOnce(KeyEvent.VK_S) || keyboard.keyDown(KeyEvent.VK_S)) {
            hex.translate(0, 3);
	}
        // D - right
        if (keyboard.keyDownOnce(KeyEvent.VK_D) || keyboard.keyDown(KeyEvent.VK_D)) {
            hex.translate(3, 0);
	}
        // If the Q key is pressed, the rotation slows down up to a point.
	if (keyboard.keyDownOnce(KeyEvent.VK_Q) || keyboard.keyDown(KeyEvent.VK_Q)) {
            if (hex.rotStep > Math.toRadians(1.0) && hex.rotStep > 0)
                hex.rotStep -= Math.toRadians(.02);
            else if (hex.rotStep < Math.toRadians(-1.0))
                hex.rotStep += Math.toRadians(.02);
	}
        // E key - rotation speeds up up to a point
        if (keyboard.keyDownOnce(KeyEvent.VK_E) || keyboard.keyDown(KeyEvent.VK_E)) {
            if (hex.rotStep < Math.toRadians(5.0) && hex.rotStep > 0)
                hex.rotStep += Math.toRadians(.02);
            else if (hex.rotStep > Math.toRadians(-5.0))
                hex.rotStep -= Math.toRadians(.02);
	}
        // Space key - rotation of the hex object's direction is switched
	if (keyboard.keyDownOnce(KeyEvent.VK_SPACE)) {
            hex.rotStep = -hex.rotStep;
	}
    }

    /**
     * Call update world for each of the objects, which will apply all vectors, including translation, rotation, etc.
     */
    private void processObjects() {
        square.updateWorld();
        hex.updateWorld();
        triangle.updateWorld();
    }

    /**
     * Set the color for each object and call its render
     * 
     * @param   g   The graphics tool
     */
    private void render(Graphics g) {
        g.setColor(Color.YELLOW);
        square.render(g);
        g.setColor(Color.CYAN);
        hex.render(g);
        g.setColor(Color.BLACK);
        /* This if statement is if the user starts with the mouse outside of the window, we don't render anything for the cursor 
           object until the user moves it inside the window.  (If you can manage to get your cursor exactly on 0,0 the object
           disappears as well.) */
        if (mouse.getPosition().x != 0 || mouse.getPosition().y != 0) {
            triangle.render(g);
        }
    }

    /**
     * This method is unchanged
     */
    protected void onWindowClosing() {
	try {
            running = false;
            gameThread.join();
	} catch (InterruptedException e) {
            e.printStackTrace();
	}
	System.exit(0);
    }

    /**
     * This method is unchanged
     * 
     * @param args
     */
    public static void main(String[] args) {
	final MatrixTransformation app = new MatrixTransformation();
	app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
		app.onWindowClosing();
            }
	});
	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
		app.createAndShowGUI();
            }
	});
    }
}