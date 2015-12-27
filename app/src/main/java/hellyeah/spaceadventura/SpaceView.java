package hellyeah.spaceadventura;

/**
 * Created by Thomas on 24/12/2015.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SpaceView extends SurfaceView implements Runnable
{
    private final String TAG = "SpaceView";

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

    private final double GRAVITY = 10;

    private final SensorManager sensorManager;
    private final Sensor capt;
    private int accelerationAngle;
    private float accelerationY;

    SensorEventListener leListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            accelerationY = event.values[1];
            accelerationAngle = (int)(Math.toDegrees(Math.asin(-accelerationY/GRAVITY)));

            if(accelerationAngle > 30)
                accelerationAngle = 30;
            else if(accelerationAngle < -30)
                accelerationAngle = -30;
            if(accelerationAngle > 5)
            {
                ship.setDirection(ship.RIGHT, Math.abs(4*accelerationAngle)-20);
            }
            else if(accelerationAngle < -5)
            {
                ship.setDirection(ship.LEFT, Math.abs(4*accelerationAngle)-20);
            }
            else
            {
                ship.setDirection(ship.FORWARD, 0);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    // When we initialize (call new()) on view
    // This special constructor method runs
    public SpaceView(Context context, int x, int y)
    {
        /* The next line of code asks the SurfaceView class to set up our object. How kind. */
        super(context);

        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        capt = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);






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

            sensorManager.registerListener(leListener, capt, SensorManager.SENSOR_DELAY_NORMAL);

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
            canvas.drawColor(Color.argb(255, 25, 0, 100));

            Point horizonR = new Point();
            Point horizonL = new Point();

            horizonR.x = screenX; horizonL.x = 0;
            horizonR.y = (int)(2*screenY/3 + screenX/2 * Math.tan(Math.toRadians(accelerationAngle)));
            horizonL.y = (int)(2*screenY/3 - screenX/2 * Math.tan(Math.toRadians(accelerationAngle)));
            paint.setColor(Color.argb(255,0,100,0));

            while(horizonL.y <= screenY || horizonR.y <= screenY )
            {
                canvas.drawLine(horizonL.x, horizonL.y, horizonR.x, horizonR.y,paint);
                horizonL.y++;
                horizonR.y++;
            }


            // Choose the brush color for drawing
            if(ship.isThrusting())
                paint.setColor(Color.argb(255,  255, 0, 0));
            else
                paint.setColor(Color.argb(255,  255, 255, 255));

            // Line from b to c = Rear
            canvas.drawLine(ship.getB().x, ship.getB().y,
                    ship.getC().x, ship.getC().y,
                    paint);


            // Line from a to b = LeftSide
            if(ship.getShipDirection() == 1)
                paint.setColor(Color.argb(255,  255, 0, 0));
            else
                paint.setColor(Color.argb(255,  255, 255, 255));
            canvas.drawLine(ship.getA().x, ship.getA().y,
                    ship.getB().x, ship.getB().y,
                    paint);

            // Line from c to a = RightSide
            if(ship.getShipDirection() == -1)
                paint.setColor(Color.argb(255,  255, 0, 0));
            else
                paint.setColor(Color.argb(255,  255, 255, 255));
            canvas.drawLine(ship.getC().x, ship.getC().y,
                    ship.getA().x, ship.getA().y,
                    paint);


            paint.setColor(Color.argb(255,  100, 100, 100));
            canvas.drawCircle(ship.getCentre().x, ship.getCentre().y, 10, paint);

            int textSize = 30;

            paint.setTextSize(textSize);
            canvas.drawText("facingAngle = "+ (int)ship.getFacingAngle()+ " degrees", 20, textSize, paint);
            canvas.drawText("Acceleration :" + accelerationY, 20, textSize*2, paint);
            canvas.drawText("Inclinaison :" + accelerationAngle + " = arcsin(" + (int)accelerationY + "/" + GRAVITY +")", 20, textSize*3, paint);

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // If the Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        sensorManager.unregisterListener(leListener, capt);
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
        sensorManager.registerListener(leListener, capt, SensorManager.SENSOR_DELAY_NORMAL);
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.


/*
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Que faire en cas de changement de précision ?
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        // Que faire en cas d'évènements sur le capteur ?
        Log.d(TAG, "censor changed");
        acceleration = sensorEvent.values[2];
    }
*/

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                paused = false;

                /*if(motionEvent.getY() > screenY - screenY / 8) {
                    if (motionEvent.getX() > screenX / 2) {
                        ship.setMovementState(ship.RIGHT);
                    } else {
                        ship.setMovementState(ship.LEFT);
                    }

                }*/
                // Thrust
                ship.setThrust(true);

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                ship.setThrust(false);

                break;
        }
        return true;
    }
}
