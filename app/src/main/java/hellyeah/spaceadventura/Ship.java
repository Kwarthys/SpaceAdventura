package hellyeah.spaceadventura;

/**
 * Created by Thomas on 24/12/2015.
 */
import android.content.Context;
import android.graphics.PointF;

public class Ship {

    PointF a;
    PointF b;
    PointF c;
    PointF centre;

    /* Which way is the ship facing Straight up to start with */
    float facingAngle = 270;

    // How long will our spaceship be
    private float length;
    private float width;

    // This will hold the pixels per second speed that the ship can move at
    private float speed = 100;

    /* These next two variables control the actual movement rate per frame
    their values are set each frame based on speed and heading */
    private float horizontalVelocity;
    private float verticalVelocity;

    /* How fast does the ship rotate? 100 degrees per second */
    private float rotationSpeed = 50;

    // Which ways can the ship move
    /*
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int THRUSTING = 3;
    */

    public final int FORWARD = 0;
    public final int LEFT = -1;
    public final int RIGHT = 1;

    // Is the ship moving and in which direction
    private boolean shipThrusting = false;
    private int shipDirection = FORWARD;


    /*This the the constructor method When we create an object from this class we will pass
    in the screen width and height*/
    public Ship(Context context, int screenX, int screenY)
    {
        length = screenX / 5;
        width = screenY / 5;

        a = new PointF();
        b = new PointF();
        c = new PointF();
        centre = new PointF();

        centre.x = screenX / 2;
        centre.y = screenY / 2;

        a.x = centre.x;
        a.y = centre.y - length / 2;

        b.x = centre.x - width / 2;
        b.y = centre.y + length / 2;

        c.x = centre.x + width / 2;
        c.y = centre.y + length / 2;

    }

    public PointF getCentre()
    {
        return  centre;
    }

    public PointF getA()
    {
        return  a;
    }

    public PointF getB()
    {
        return  b;
    }

    public PointF getC()
    {
        return  c;
    }

    float getFacingAngle(){
        return facingAngle;
    }

    /*This method will be used to change/set if the ship is rotating left, right or thrusting*/
    /*
    public void setMovementState(int state)
    {
            shipMoving = state;
    }
    */
    public void setThrust(boolean b){shipThrusting = b;}
    public void setDirection(int state, int laSpeed){shipDirection = state; rotationSpeed = laSpeed;}
    public boolean isThrusting(){return shipThrusting;}
    public int getShipDirection(){return shipDirection;}


    /*
    This update method will be called from update in HeadingAndRotationView
    It determines if the player ship needs to move and changes the coordinates
    and rotation when necessary.
    */

    public void update(long fps){

        /*
        Where are we facing at the moment
        Then when we rotate we can work out
        by how much
        */

        float previousFA = facingAngle;

        if(shipDirection == LEFT){

            facingAngle = facingAngle -rotationSpeed / fps;

            if(facingAngle < 1){
                facingAngle = 360;
            }
        }

        if(shipDirection == RIGHT){

            facingAngle = facingAngle + rotationSpeed / fps;

            if(facingAngle > 360){
                facingAngle = 1;
            }
        }

        if(shipThrusting){

            /*
            facingAngle can be any angle between 1 and 360 degrees
            the Math.toRadians method simply converts the more conventional
            degree measurements to radians which are required by
            the cos and sin methods.
            */

            horizontalVelocity = (float)(Math.cos(Math.toRadians(facingAngle)));
            verticalVelocity = (float)(Math.sin(Math.toRadians(facingAngle)));

            // move the ship - 1 point at a time
            centre.x = centre.x + horizontalVelocity * speed / fps;
            centre.y = centre.y + verticalVelocity * speed / fps;

            a.x = a.x + horizontalVelocity * speed / fps;
            a.y = a.y + verticalVelocity * speed / fps;

            b.x = b.x + horizontalVelocity * speed / fps;
            b.y = b.y + verticalVelocity * speed / fps;

            c.x = c.x + horizontalVelocity * speed / fps;
            c.y = c.y + verticalVelocity * speed / fps;

        }

        /*
        Now rotate each of the three points by
        the change in rotation this frame
        facingAngle - previousFA
        */

        float tempX;
        float tempY;

        // rotate point a
        a.x = a.x - centre.x;
        a.y = a.y - centre.y;

        tempX = (float)(a.x * Math.cos(Math.toRadians(facingAngle - previousFA)) -
                a.y * Math.sin(Math.toRadians(facingAngle - previousFA)));

        tempY = (float)(a.x * Math.sin(Math.toRadians(facingAngle - previousFA)) +
                a.y * Math.cos(Math.toRadians(facingAngle - previousFA)));

        a.x = tempX + centre.x;
        a.y = tempY + centre.y;

        // rotate point b
        b.x = b.x - centre.x;
        b.y = b.y - centre.y;

        tempX = (float)(b.x * Math.cos(Math.toRadians(facingAngle - previousFA)) -
                b.y * Math.sin(Math.toRadians(facingAngle - previousFA)));

        tempY = (float)(b.x * Math.sin(Math.toRadians(facingAngle - previousFA)) +
                b.y * Math.cos(Math.toRadians(facingAngle - previousFA)));

        b.x = tempX + centre.x;
        b.y = tempY + centre.y;

        // rotate point c
        c.x = c.x - centre.x;
        c.y = c.y - centre.y;

        tempX = (float)(c.x * Math.cos(Math.toRadians(facingAngle - previousFA)) -
                c.y * Math.sin(Math.toRadians(facingAngle - previousFA)));

        tempY = (float)(c.x * Math.sin(Math.toRadians(facingAngle - previousFA)) +
                c.y * Math.cos(Math.toRadians(facingAngle - previousFA)));

        c.x = tempX + centre.x;
        c.y = tempY + centre.y;

    }// End of update method



}
