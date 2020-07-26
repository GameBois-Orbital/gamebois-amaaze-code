package com.gamebois.amaaze.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Ball2D {
    String LOG_TAG = Ball2D.class.getSimpleName();

    private float x,y,radius;
    private long availableTime;
    private Createbox2d box2d;
    private Body b1;


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public Createbox2d getBox2d() {
        return box2d;
    }

    public Ball2D(float x, float y, float radius, Createbox2d box2d) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.box2d = box2d;
        this.availableTime = 0;

        BodyDef bd = new BodyDef();  // define dynamic body with initial ball center
        bd.type = BodyType.DYNAMIC;
        bd.position.set(box2d.coordPixelsToWorld(this.x, this.y));
        b1 = box2d.createBody(bd);

        CircleShape cs = new CircleShape();
        cs.m_radius = box2d.scalarPixelsToWorld(this.radius);

        FixtureDef fd = new FixtureDef(); // Body properties
        fd.shape = cs;
        fd.density = 0.3f;
        fd.friction = 0.1f;
        fd.restitution = 0.3f;

        b1.createFixture(fd); //add properties to body
        b1.setUserData(this);

    }


    public void addforce(float x, float y) {  // create then apply force on ball center
        Vec2 force = new Vec2(b1.getMass()*y/2,-b1.getMass()*x/2);
        Vec2 pos = b1.getWorldCenter();
        b1.applyForce(force, pos);
    }

    public void display(Canvas c) {
        Vec2 pos = box2d.getBodyPixelCoord(b1); //get body center
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        c.drawCircle(pos.x, pos.y, this.radius, paint); //draw ball

        Vec2 center = b1.getWorldCenter();
        b1.applyForce(new Vec2(0,0), center); //add force on center
        Log.d(LOG_TAG, "display to" + pos.x + "and" + pos.y);
    }

    public void destroy() {
        box2d.destroyBody(b1);
    }

    public void warpableAt(long l) {
        availableTime = l;

    }

    public boolean isWarpable(long currentTime) {
        return availableTime < currentTime;
    }

    public void teleportTo(Wormhole2D wormhole2D, long currentTime) {
        Vec2 point = box2d.coordPixelsToWorld((float) (wormhole2D.getX() + (wormhole2D.getRadius()*0.9 - radius)),
                (float) (wormhole2D.getY() + (wormhole2D.getRadius()*0.9 - radius)));
        b1.setTransform(point, 1);
        availableTime = currentTime + 2000;
    }
}
