package com.gamebois.amaaze.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import java.util.ArrayList;

public class Maze2D {
    private ArrayList<PointF> contour;
    private ArrayList<Vec2> surface;
    private Body body;
    private Createbox2d box2d;
    private int index = 0;


    public Maze2D(ArrayList<PointF> contour, Createbox2d box2d) {
        this.box2d = box2d;
        this.contour = contour; //get points to form maze


        surface = new ArrayList<>(); //to build box2d surface
        for (int i = 0; i < contour.size(); i++) {
            if (this.contour.get(i).x > 0 && this.contour.get(i).y > 0)
                surface.add(new Vec2(this.contour.get(i).x, this.contour.get(i).y )); //add points to build box2d surface
        }

        if(surface.size() > 0) {
            ChainShape chain = new ChainShape(); //chainshape in box2d

            Vec2[] vertices = new Vec2[surface.size()];
            for (int i = 0; i < vertices.length; i++) {  //Convert each vertex to Box2D World coordinates.
                vertices[i] = box2d.coordPixelsToWorld(surface.get(i));
            }

            chain.createChain(vertices, vertices.length); // create ChainShape with array of Vec2

            BodyDef bd = new BodyDef(); //Attach shape to body
            body = box2d.getWorld().createBody(bd);
            body.createFixture(chain,1);
            body.setUserData(this);
        }

    }

    public void display(Canvas c) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.parseColor("#ae52d4"));

        Path path = new Path();
        path.moveTo(contour.get(0).x, contour.get(0).y); //join lines
        for (int j = 0; j < contour.size(); j++) {
            path.lineTo(contour.get(j).x, contour.get(j).y);  //join lines
        }
        path.lineTo(contour.get(0).x,contour.get(0).y);
        c.drawPath(path, paint);
    }

    public void destroy() {
        box2d.destroyBody(body); //destory body
    }
}

//    private float scale, xoffset, yoffset;
//
//
//    public Maze2D(ArrayList<PointF> contour, float scale, float xoffset, float yoffset, Createbox2d box2d) {
//        this.box2d = box2d;
//        this.contour = contour; //get points to form maze
//
//        this.scale = scale;
//        this.xoffset = xoffset;
//        this.yoffset = yoffset;
//
//
//        surface = new ArrayList<Vec2>(); //to build box2d surface
//        for (int i = 0; i < contour.size(); i++) {
//            if (this.contour.get(i).x > 0 && this.contour.get(i).y > 0)
//                surface.add(new Vec2(this.contour.get(i).x*this.scale + this.xoffset, this.contour.get(i).y*this.scale + this.yoffset )); //add points to build box2d surface
//        }
//
//        if(surface.size() > 0) {
//            ChainShape chain = new ChainShape(); //chainshape in box2d
//
//            Vec2[] vertices = new Vec2[surface.size()];
//            for (int i = 0; i < vertices.length; i++) {  //Convert each vertex to Box2D World coordinates.
//                vertices[i] = box2d.coordPixelsToWorld(surface.get(i));
//            }
//
//             chain.createChain(vertices, vertices.length); // create ChainShape with array of Vec2
//
//            BodyDef bd = new BodyDef(); //Attach shape to body
//            body = box2d.getWorld().createBody(bd);
//            body.createFixture(chain,1);
//            body.setUserData(this);
//        }
//
//    }
//
//    public void display(Canvas c) {
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        paint.setColor(Color.RED);
//
//        Path path = new Path();
//        path.moveTo(contour.get(0).x*scale + xoffset, contour.get(0).y*scale + yoffset); //join lines
//        for (int j = 0; j < contour.size(); j++) {
//            path.lineTo(contour.get(j).x*scale + xoffset, contour.get(j).y*scale + yoffset);  //join lines
//        }
//        path.lineTo(contour.get(0).x*scale + xoffset,contour.get(0).y*scale + yoffset);
//        c.drawPath(path, paint);
//    }
//
//    public void destroy() {
//        box2d.destroyBody(body); //destory body
//    }
//}
