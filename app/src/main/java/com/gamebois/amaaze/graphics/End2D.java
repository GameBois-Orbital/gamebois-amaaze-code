package com.gamebois.amaaze.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class End2D {

    private float x,y,radius, ballRadius;
    private Createbox2d box2d;
    private Body b1;

    public End2D(float x, float y, float radius, float ballRadius, Createbox2d box2d) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.ballRadius = ballRadius;
        this.box2d = box2d;

        BodyDef bd = new BodyDef();  // define dynamic body with initial ball center
        bd.type = BodyType.DYNAMIC;
        bd.position.set(box2d.coordPixelsToWorld(this.x, this.y));
        b1 = box2d.createBody(bd);

        CircleShape cs = new CircleShape();
        cs.m_radius = box2d.scalarPixelsToWorld((float) (radius - (ballRadius*2*0.8))); // shape has radius smaller than this.radius by 0.8 ball diameter

        FixtureDef fd = new FixtureDef(); // Body properties
        fd.shape = cs;

        b1.createFixture(fd); //add properties to body
        b1.setUserData(this);
    }

    public void display(Canvas c) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        c.drawCircle(x, y, this.radius, paint); //draw ball
    }

    public void destroy() {
        box2d.destroyBody(b1);
    }
}
