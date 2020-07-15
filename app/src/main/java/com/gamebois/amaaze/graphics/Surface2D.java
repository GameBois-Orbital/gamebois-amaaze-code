package com.gamebois.amaaze.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import java.util.ArrayList;

public class Surface2D {
    private ArrayList<Vec2> surface;
    private float x0,y0,x1,y1;
    private Body body;
    private Createbox2d box2d;

    public Surface2D(float x0,float y0,float x1,float y1 ,Createbox2d box2d){
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.box2d = box2d;
        surface = new ArrayList<Vec2>();
        surface.add(new Vec2(x0,y0));
        surface.add(new Vec2(x1,y1));

        ChainShape chain = new ChainShape(); //create ChainShape in box2d

        Vec2[] vertices = new Vec2[surface.size()];
        for(int i = 0; i < vertices.length; i++) {
            vertices[i] = box2d.coordPixelsToWorld(surface.get(i)); //convert surface points to box2d world coordinates
        }
        chain.createLoop(vertices, vertices.length);

        BodyDef bd = new BodyDef(); //Define body
        body = box2d.getWorld().createBody(bd);
        body.createFixture(chain,1);
        body.setUserData(this);
        
    }

    public void display(Canvas c) {
        Paint paint = new Paint();
        paint.setColor(0x000000);//make boundary light
        paint.setAlpha(255);
        paint.setStrokeWidth(6);
        c.drawLine(x0, y0, x1, y1, paint); //draw lines
    }

    public void destroy() {
        box2d.destroyBody(body);
    }


}
