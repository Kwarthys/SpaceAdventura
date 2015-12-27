package hellyeah.spaceadventura;

/**
 * Created by Thomas on 24/12/2015.
 */
import android.content.Context;
import android.graphics.PointF;

public class Ship {

    PointF topRight;
    PointF topLeft;
    PointF botRight;
    PointF botLeft;
    PointF centre;

    /* Which way is the ship facing Straight up to start with */
    float facingAngle = 270;

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
        float length = screenY / 5;
        float width = screenX / 5;

        topRight = new PointF();
        topLeft = new PointF();
        botLeft = new PointF();
        botRight = new PointF();
        centre = new PointF();

        centre.x = screenX / 2;
        centre.y = screenY / 2;

        topLeft.x = centre.x - width/2;
        topLeft.y = centre.y - length/2;

        topRight.x = centre.x + width / 2;
        topRight.y = centre.y - length / 2;

        botLeft.x = centre.x - width / 2;
        botLeft.y = centre.y + length / 2;

        botRight.x = centre.x + width / 2;
        botRight.y = centre.y + length / 2;

    }

    public PointF getCentre()
    {
        return  centre;
    }

    public PointF getTopRight()
    {
        return  topRight;
    }

    public PointF getTopLeft()
    {
        return  topLeft;
    }

    public PointF getBotRight()
    {
        return  botRight;
    }

    public PointF getBotLeft()
    {
        return  botLeft;
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

        if(shipDirection == RIGHT){

            facingAngle = facingAngle -rotationSpeed / fps;

            if(facingAngle < 1){
                facingAngle = 360;
            }
        }

        if(shipDirection == LEFT){

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

            float dx = horizontalVelocity * speed / fps;
            float dy = verticalVelocity * speed / fps;

            topRight.x += dx;topRight.y += dy;
            topLeft.x += dx; topLeft.y += dy;
            botLeft.x += dx; botLeft.y += dy;
            botRight.x += dx; botRight.y += dy;

        }

        /*
        Now rotate each of the three points by
        the change in rotation this frame
        facingAngle - previousFA
        */

        float dangle = facingAngle - previousFA;

        rotate(topLeft, dangle);
        rotate(topRight, dangle);
        rotate(botLeft, dangle);
        rotate(botRight, dangle);

    }// End of update method

    private void rotate(PointF c, float dteta)
    {
        c.x = c.x - centre.x;
        c.y = c.y - centre.y;

        float tempX = (float)(c.x * Math.cos(Math.toRadians(dteta)) -
                c.y * Math.sin(Math.toRadians(dteta)));

        float tempY = (float)(c.x * Math.sin(Math.toRadians(dteta)) +
                c.y * Math.cos(Math.toRadians(dteta)));

        c.x = tempX + centre.x;
        c.y = tempY + centre.y;
    }



}
