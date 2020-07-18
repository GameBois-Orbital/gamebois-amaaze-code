package com.gamebois.amaaze.graphics;

import android.graphics.Canvas;
import android.util.Log;

import com.gamebois.amaaze.view.GameActivity;

import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public class Createbox2d {
    private String LOG_TAG = GameActivity.class.getSimpleName();

    static final long FPS = 10;
    boolean doSleep = true;
    private static final float TIMESTEP = 1.0f /60f;
    private static final int VELOCITY_ITERATIONS = 10;
    private static final int POSITION_ITERATIONS = 8;
    private Vec2 gravity = new Vec2(0.0f, 0.0f);
    private World world;
    private float scaleFactor = 150.0f;
    private float IscaleFactor=1/scaleFactor;//meter to pixel
    private Canvas canvas;
    private float width;
    private float  height;


    Createbox2d() {
        world = new World(gravity);
        world.setWarmStarting(true);
        world.setContinuousPhysics(true);
    }

    public void setScreenSize(float screen_width, float screen_height) {
        width = screen_width;
        height = screen_height;
        Log.d(LOG_TAG, "Createbox2d screen size: " + width + " , " + height);
    }

    /**
     * to iterate in box2d world
     */
    public void step() {
        float timeStep = 1.0f / 60f;
        world.step(TIMESTEP,VELOCITY_ITERATIONS,POSITION_ITERATIONS);
        world.clearForces();
    }
    /**
     * destroy body in world
     */

    public void destroyBody(Body b) {
        world.destroyBody(b);
    }

    /**
     *  create body in box2d world
     */

    public Body createBody(BodyDef bd) {

        return world.createBody(bd);
    }
    /**
     *  returns world object
     */
    public World getWorld() {
        return world;
    }

    /**
     *  returns body coordinates in pixels
     */
    public Vec2 getBodyPixelCoord(Body b) {
        Transform xf = b.getTransform();//body pixels in box2d world
        return coordWorldToPixels(xf.p);// box2d world to pixels
    }

    /**
     *  scale pixels to world
     */

    public float scalarPixelsToWorld(float val) {

        return val*IscaleFactor;
    }

    /**
     *  scale world to pixels
     */

    public float scalarWorldToPixels(float val) {

        return val*scaleFactor;
    }

    /**
     *   pixels (x,y) to box2d world
     */
    public Vec2 coordPixelsToWorld(float pixelX, float pixelY) {
        float offsetX=width/2;
        float offsetY=height/2;
        float worldX=(pixelX-offsetX)*IscaleFactor;
        float worldY=(-pixelY+offsetY)*IscaleFactor;
        return new Vec2(worldX,worldY);
    }

    /**
     *  pixels (Vec2) to box2d world
     */

    public Vec2 coordPixelsToWorld(Vec2 p) {

        float offsetX=width/2;
        float offsetY=height/2;
        float worldX=(p.x-offsetX)*IscaleFactor;
        float worldY=(-p.y+offsetY)*IscaleFactor;
        return new Vec2(worldX,worldY);
    }
    
    /**
     *   box2d world (Vec2) to pixels
     */

    public Vec2 coordWorldToPixels(Vec2 p) {
        float offsetX=width/2*IscaleFactor;
        float offsetY=height/2*IscaleFactor;
        float pixelX=(p.x+offsetX)*scaleFactor;
        float pixelY=(-p.y+offsetY)*scaleFactor;
        return new Vec2(pixelX, pixelY);
    }
}
