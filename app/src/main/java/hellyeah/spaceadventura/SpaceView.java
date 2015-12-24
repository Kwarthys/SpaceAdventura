package hellyeah.spaceadventura;

/**
 * Created by Thomas on 24/12/2015.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SpaceView extends SurfaceView implements Runnable{

    Context context;

    // This is our thread
    Thread gameThread = null;

    // Our SurfaceHolder to lock the surface before we draw our graphics
    SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    volatile boolean playing;

    // Game is paused at the start
    boolean paused = true;

    // A Canvas and a Paint object
    Canvas canvas;
    Paint paint;

    // This variable tracks the game frame rate
    long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The size of the screen in pixels
    int screenX;
    int screenY;

    // The players ship
    Ship ship;

    // When we initialize (call new()) on view
    // This special constructor method runs
    public SpaceView(Context context, int x, int y) {

        /*
        The next line of code asks the
        SurfaceView class to set up our object.
        How kind.
        */

        super(context);

        // Make a globally available copy of the context so we can use it in another method
        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        // Make a new player space ship
        ship = new Ship(context, screenX, screenY);

    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            /*
            Calculate the fps this frame
            We can then use the result to
            time animations and more.
            */

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }

    }

    private void update(){

        // Move the player's ship
        ship.update(fps);

    }

    private void draw(){
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));

            // Now draw the player spaceship
            // Line from a to b
            canvas.drawLine(ship.getA().x, ship.getA().y,
                    ship.getB().x, ship.getB().y,
                    paint);

            // Line from b to c
            canvas.drawLine(ship.getB().x, ship.getB().y,
                    ship.getC().x, ship.getC().y,
                    paint);

            // Line from c to a
            canvas.drawLine(ship.getC().x, ship.getC().y,
                    ship.getA().x, ship.getA().y,
                    paint);

            canvas.drawPoint(ship.getCentre().x, ship.getCentre().y,paint);

            paint.setTextSize(60);
            canvas.drawText("facingAngle = "+ (int)ship.getFacingAngle()+ " degrees", 20, 70, paint);

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // If the Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If the Activity is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                paused = false;

                if(motionEvent.getY() > screenY - screenY / 8) {
                    if (motionEvent.getX() > screenX / 2) {
                        ship.setMovementState(ship.RIGHT);
                    } else {
                        ship.setMovementState(ship.LEFT);
                    }

                }

                if(motionEvent.getY() < screenY - screenY / 8) {
                    // Thrust
                    ship.setMovementState(ship.THRUSTING);

                }

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                ship.setMovementState(ship.STOPPED);

                break;
        }
        return true;
    }
}
