package com.gamebois.amaaze.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.Log;

import com.gamebois.amaaze.GameActivity;

import java.util.ArrayList;

public class GraphicThread extends Thread {
    private String LOG_TAG = GameActivity.class.getSimpleName();

    private boolean running = false;
    private final int refresh_rate = 6;
    

    private GraphicSurface gs;


    private Canvas c = null;
    private Createbox2d box2d;
    private Ball2D ball;
    private ArrayList<Maze2D> mazes = new ArrayList<Maze2D>();
    private Surface2D s1, s2, s3, s4;
    private float screen_height, screen_width;
    private float MIN = -3.8f;
    private float MAX = 3.8f;

    public GraphicThread(GraphicSurface gs, Context context) {
        this.gs = gs;
        box2d = new Createbox2d();// create box2d world
    }

    public void setScreenSize(float screen_width, float screen_height) {
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        box2d.setScreenSize(screen_width, screen_height);
    }

    public void setRunning(boolean b) {
        this.running = b;
    }

    /*
   limits the applied force to MIN and MAX values
 */
    public float limit(float value) {
        return Math.max(MIN, Math.min(value, MAX));
    }

    public void run() {
        long previousTime, currentTime;
        previousTime = System.currentTimeMillis();

        //border surfaces so that ball won't go out of the screen
      /*  s1 = new Surface2D(0, 0, screen_width, 0, box2d);
        s2 = new Surface2D(screen_width, 0, screen_width, screen_height, box2d);
        s3 = new Surface2D(screen_width, screen_height, 0, screen_height, box2d);
        s4 = new Surface2D(0, screen_height, 0, 0, box2d);
                                                                            */
        setMazes();
        setBall();


        while (running) {
            currentTime = System.currentTimeMillis();
            while ((currentTime - previousTime) < refresh_rate) {
                currentTime = System.currentTimeMillis();
            }
            previousTime = currentTime;

            box2d.step(); //box2d step

            Log.d(LOG_TAG, String.valueOf("force gs roll " + limit(gs.getRoll()) + " , gs picth " + limit(gs.getRoll())));
            ball.addforce(-limit(gs.getRoll()), -limit(gs.getPitch())); // add force on ball and limit it

            try {
                c = gs.getHolder().lockCanvas();
                synchronized (gs.getHolder()) {
                    c.drawColor(Color.TRANSPARENT);
                    c.drawColor(0, PorterDuff.Mode.CLEAR);
                    //display border
                   /* s1.display(c);
                    s2.display(c);
                    s3.display(c);
                    s4.display(c);   */
                    for (Maze2D maze2D : mazes) {
                        maze2D.display(c);  //display maze
                    }

                    ball.display(c); //display ball
                }
            } finally {
                if (c != null) {
                    gs.getHolder().unlockCanvasAndPost(c);
                }
            }

        }
        try {
            Thread.sleep(refresh_rate - 5); // Wait some time till I need to display again
        } catch (
                InterruptedException e) {
            assert e != null;
            e.printStackTrace();
        }
    }

    public void destroyAll() {
       /* if(s1 != null)
            s1.destroy();
        if(s2 != null)
            s2.destroy();
        if(s3 != null)
            s3.destroy();
        if(s4 != null)
            s4.destroy();       */

        for(Maze2D maze2D : mazes) {
            maze2D.destroy();  //destroy maze
        }

        ball.destroy(); //destroy ball
    }


    private void setMazes() {
        ArrayList<ArrayList<PointF>> mazeArrayList = gs.getMazeArrayList();
        if (mazeArrayList != null)
            for (int i = 0; i < mazeArrayList.size(); i++) {
                mazes.add(new Maze2D(mazeArrayList.get(i), box2d)); //add maze points to maze for creating box2d body
            }

    }

    private void setBall() {
        ArrayList<PointF> ballArrayList = gs.getBallArrayList();
        if (ballArrayList != null) {
            ball = new Ball2D(ballArrayList.get(0).x, ballArrayList.get(0).y, ballArrayList.get(1).x, box2d);
        }
    }
}

