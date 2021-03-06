package com.gamebois.amaaze.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.util.Log;

import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.content.ContextCompat;

import com.gamebois.amaaze.R;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;


public class Wormhole2D {
    String LOG_TAG = Wormhole2D.class.getSimpleName();

    private int index;
    private float x,y,radius, ballRadius;
    private float FRICTION = 200;
    private Createbox2d box2d;
    private Body b1;

    private Context context;

    public Wormhole2D(int index, float x, float y, float radius, float ballRadius, Createbox2d box2d, Context context) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.ballRadius = ballRadius;
        this.box2d = box2d;

        this.context = context;

        BodyDef bd = new BodyDef();  // define dynamic body with initial ball center
        bd.type = BodyType.DYNAMIC;
        bd.position.set(box2d.coordPixelsToWorld(this.x, this.y));
        b1 = box2d.createBody(bd);

        CircleShape cs = new CircleShape();
        cs.m_radius = box2d.scalarPixelsToWorld((float) (radius - (ballRadius*2*0.6))); // shape has radius smaller than this.radius by 0.8 ball diameter

        FixtureDef fd = new FixtureDef(); // Body properties
        fd.shape = cs;
        fd.friction = FRICTION;

        b1.createFixture(fd); //add properties to body
        b1.setUserData(index);
        Log.d(LOG_TAG, "wormhole created");
    }

    public void display(Canvas c) {
        Drawable dWormhole = ContextCompat.getDrawable(context, R.drawable.wormhole);
        dWormhole.setBounds((int) Math.ceil(this.x - radius),(int) Math.ceil( y - radius), (int) Math.ceil( x + radius),(int) Math.ceil( y + radius));
        dWormhole.draw(c);
//        Paint paint = new Paint();
//        paint.setColor(Color.GREEN);
//        c.drawCircle(x, y, this.radius, paint); //draw ball
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public void destroy() {
        box2d.destroyBody(b1);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
