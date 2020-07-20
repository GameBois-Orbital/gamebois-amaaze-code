package com.gamebois.amaaze.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.google.android.play.core.splitinstall.c;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import java.util.ArrayList;

public class Surface2D {
    private ArrayList<Vec2> surface;
    private Body body;
    private Createbox2d box2d;


    public Surface2D(float screen_width, float screen_height, Createbox2d box2d) {


        this.box2d = box2d;
        surface = new ArrayList<Vec2>();
        surface.add(new Vec2(0, 0));
        surface.add(new Vec2(screen_width, 0));
        surface.add(new Vec2(screen_width, screen_height));
        surface.add(new Vec2(0, screen_height));


        ChainShape chain = new ChainShape(); //create ChainShape in box2d

        Vec2[] vertices = new Vec2[surface.size()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = box2d.coordPixelsToWorld(surface.get(i)); //convert surface points to box2d world coordinates
        }
        chain.createLoop(vertices, vertices.length);

        BodyDef bd = new BodyDef(); //Define body
        body = box2d.getWorld().createBody(bd);
        body.createFixture(chain, 1);
        body.setUserData(this);

    }

    public void destroy() {
        box2d.destroyBody(body);
    }
}
