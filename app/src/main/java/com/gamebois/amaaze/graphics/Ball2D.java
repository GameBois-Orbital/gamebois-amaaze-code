package com.gamebois.amaaze.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Ball2D {

    private float x,y,radius;
    private Createbox2d box2d;
    private Body b1;
    
    public Ball2D(float x, float y, float radius, Createbox2d box2d) {
        this.x = x;
        this.y = y;
        this.radius = radius;

        BodyDef bd = new BodyDef();  // define dynamic body with initial ball center
        bd.type = BodyType.DYNAMIC;
        bd.position.set(box2d.coordPixelsToWorld(this.x, this.y));
        b1 = box2d.createBody(bd);

        CircleShape cs = new CircleShape();
        cs.m_radius = box2d.scalarPixelsToWorld(this.radius);

        FixtureDef fd = new FixtureDef(); // Body properties
        fd.shape = cs;
        fd.density = 1;
        fd.friction = 0.2f;
        fd.restitution = 0.5f;

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
    }

    public void destroy() {
        box2d.destroyBody(b1);
    }
}
