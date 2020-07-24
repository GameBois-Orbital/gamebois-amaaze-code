package com.gamebois.amaaze.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.Log;

import com.gamebois.amaaze.BuildConfig;
import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.view.GameActivity;

import java.util.ArrayList;
import java.util.List;

public class GraphicThread extends Thread {
    private String LOG_TAG = GameActivity.class.getSimpleName();
    float BALL_RADIUS = 5;
    float END_RADIUS;
    float WORMHOLE_RADIUS;

    private boolean running = false;
    private final int refresh_rate = 6;

    private GraphicSurface gs;

    private Canvas c = null;
    private Createbox2d box2d;
    private Ball2D ball;

    private End2D endHole;
    private ArrayList<Maze2D> mazes = new ArrayList<Maze2D>();
    private ArrayList<Wormhole2D> wormholes = new ArrayList<>();
    private Surface2D surfaceBoundary;
    private float screen_height, screen_width;
    private float creatorHeight, creatorWidth;
    private float MIN = -3.8f;
    private float MAX = 3.8f;

    private float scale, xoffset, yoffset;

    public GraphicThread(GraphicSurface gs, Context context) {
        this.gs = gs;
        box2d = new Createbox2d();// create box2d world
        box2d.listenForCollisions(context);
        scale = Math.min(screen_width / creatorWidth, screen_height / creatorHeight);
        xoffset = (float) ((screen_width - creatorWidth * scale) / 2.0);
        yoffset = (float) ((screen_height - creatorHeight * scale) / 2.0);
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
        long previousTime, currentTime, startTime;
        previousTime = System.currentTimeMillis();

        //border surfaces so that ball won't go out of the screen
        surfaceBoundary = new Surface2D(screen_width, screen_height, box2d);

       setMazes(gs.getMazeArrayList());
       setBallAndEnd();
       setWormholes();


        while (running) {
            currentTime = System.currentTimeMillis();
            while ((currentTime - previousTime) < refresh_rate) {
                currentTime = System.currentTimeMillis();
            }
            previousTime = currentTime;


            if (box2d.isGameOver()){
                gs.getGameOver().postValue(true);
                running = false;
            }

            int a = 2;
            if (box2d.isWarping() && ball.isWarpable(currentTime)) {
                box2d.notWarping();
                String indexString = box2d.contactListener.touched;
                int nextIndex = -1;
                for (int i = 0; i < wormholes.size(); i++) {
                    if (indexString.equals(((Object) i).toString())) {
                        nextIndex = (i + 1) % (wormholes.size()); //get nextIndex by loop
                        break;
                    }
                }
                ball.teleportTo(wormholes.get(nextIndex), currentTime);
            }

            box2d.step(); //box2d step

            Log.d(LOG_TAG, "force gs roll " + limit(gs.getRoll()) + " , gs picth " + limit(gs.getRoll()));
            ball.addforce(-limit(gs.getRoll()), -limit(gs.getPitch())); // add force on ball and limit it

            try {
                c = gs.getHolder().lockCanvas();
                synchronized (gs.getHolder()) {
                    c.drawColor(Color.TRANSPARENT);
                    c.drawColor(0, PorterDuff.Mode.CLEAR);

                    for (Maze2D maze2D : mazes) {
                        maze2D.display(c);  //display maze

                    }
                    for (Wormhole2D wormhole2D : wormholes) {
                        wormhole2D.display(c);           // display wormholes
                    }

                    endHole.display(c); //display endHole

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
            e.printStackTrace();
        }
    }
    

    public void destroyAll() {
        if (surfaceBoundary !=null) {
            surfaceBoundary.destroy();
        }

        for(Maze2D maze2D : mazes) {
            maze2D.destroy();  //destroy maze
        }

        for(Wormhole2D wormhole2D : wormholes) {
            wormhole2D.destroy();
        }

        ball.destroy(); //destroy ball
    }

    private void setMazes(List<ContourList> mazeArrayList) {

        if (mazeArrayList != null) {
            Log.d(LOG_TAG, "Hello" + creatorWidth + " " + creatorHeight);
            for (int i = 0; i < mazeArrayList.size(); i++) {
                mazes.add(new Maze2D(mazeArrayList.get(i).getContourList(), scale, xoffset, yoffset, box2d)); //add maze points to maze for creating box2d body
            }
        }

    }

    private void setWormholes() {
        ArrayList<PointF> wormholesArrayList = gs.getWormholesArrayList();
        if (wormholesArrayList != null) {
            for (int i = 0; i < wormholesArrayList.size(); i++) {
                wormholes.add(new Wormhole2D(i, wormholesArrayList.get(i).x * scale + xoffset, wormholesArrayList.get(i).y *scale + yoffset, END_RADIUS, BALL_RADIUS, box2d));
            }
        }
    }

    private void setBallAndEnd() {
        ArrayList<PointF> startAndEndArrayList = gs.getBallArrayList();
        if (startAndEndArrayList != null) {
            END_RADIUS = startAndEndArrayList.get(2).x;
            ball = new Ball2D(startAndEndArrayList.get(0).x * scale + xoffset, startAndEndArrayList.get(0).y * scale + yoffset, BALL_RADIUS, box2d);
            endHole = new End2D(startAndEndArrayList.get(1).x * scale + xoffset, startAndEndArrayList.get(1).y * scale + yoffset, END_RADIUS, BALL_RADIUS, box2d);
        }
    }


    public float getCreatorHeight() {
        return creatorHeight;
    }

    public void setCreatorHeight(float creatorHeight) {
        this.creatorHeight = creatorHeight;
    }

    public float getCreatorWidth() {
        return creatorWidth;
    }

    public void setCreatorWidth(float creatorWidth) {
        this.creatorWidth = creatorWidth;
    }


}
