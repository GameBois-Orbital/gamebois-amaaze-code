package com.gamebois.amaaze.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;

import com.gamebois.amaaze.BuildConfig;
import com.gamebois.amaaze.model.ContourList;
import com.gamebois.amaaze.view.CustomChronometer;
import com.gamebois.amaaze.view.GameActivity;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class GraphicThread extends Thread {
    private String LOG_TAG = GameActivity.class.getSimpleName();
    float END_RADIUS, WORMHOLE_RADIUS, BALL_RADIUS;
    CustomChronometer custChrono;

    private boolean running = false;
    private final int refresh_rate = 6;

    private GraphicSurface gs;

    private Canvas c = null;
    private Createbox2d box2d;
    private Ball2D ball;
    private End2D endHole;
    private ArrayList<Maze2D> mazes = new ArrayList<>();
    private ArrayList<Wormhole2D> wormholes = new ArrayList<>();
    private Surface2D surfaceBoundary;
    private float screen_height, screen_width;
    private float creatorHeight, creatorWidth;
    private float MIN = -3.8f;
    private float MAX = 3.8f;

    private Context context;

    private float scale, xoffset, yoffset;

    public GraphicThread(GraphicSurface gs, Context context) {
        this.gs = gs;
        this.context = context;
        box2d = new Createbox2d();// create box2d world
        box2d.listenForCollisions(context);
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

    public void setGameWorldAndGraphics() {
        //border surfaces so that ball won't go out of the screen
        surfaceBoundary = new Surface2D(screen_width, screen_height, box2d);

        scale = Math.min(screen_width / creatorWidth, screen_height / creatorHeight);
        xoffset = (float) ((screen_width - creatorWidth * scale) / 2.0);
        yoffset = (float) ((screen_height - creatorHeight * scale) / 2.0);

        END_RADIUS = gs.getEndPointRadius();
        WORMHOLE_RADIUS = END_RADIUS;
        BALL_RADIUS = END_RADIUS * 0.4f;
        setMazes(gs.getMazeArrayList());
        setWormholes(gs.getWormholesArrayList());
        setBallAt(gs.getStartPoint());
        setEndAt(gs.getEndPoint());

    }

    public void run() {
        long previousTime, currentTime, startTime;
        previousTime = System.currentTimeMillis();
        startTime = SystemClock.elapsedRealtime();
        custChrono.setBase(startTime);
        custChrono.start();

        while (running) {
            currentTime = System.currentTimeMillis();
            while ((currentTime - previousTime) < refresh_rate) {
                currentTime = System.currentTimeMillis();
            }
            previousTime = currentTime;


            if (box2d.isGameOver()){
                gs.getGameOver().postValue(true);
                running = false;
                custChrono.stop();
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
        endHole.destroy();
    }

    private void setMazes(List<ContourList> mazeArrayList) {

        if (mazeArrayList != null) {
            for (int i = 0; i < mazeArrayList.size(); i++) {
                ArrayList<PointF> contour = mazeArrayList.get(i).getContourList();
                for (PointF point : contour) {
                    point.set(point.x*scale + xoffset, point.y*scale + yoffset);
                }
                Log.d(LOG_TAG, "hello" + contour.toString());
                mazes.add(new Maze2D(contour, box2d)); //add maze points to maze for creating box2d body
            }
        }
    }
    private void setWormholes(ArrayList<PointF> wormholesArrayList) {
        if (wormholesArrayList != null) {
            for (int i = 0; i < wormholesArrayList.size(); i++) {
                wormholes.add(new Wormhole2D(i, wormholesArrayList.get(i).x*scale + xoffset, wormholesArrayList.get(i).y*scale + yoffset, WORMHOLE_RADIUS, BALL_RADIUS, box2d, context));
            }
        }
    }
    private void setBallAt(PointF start) {
        ball = new Ball2D(start.x*scale + xoffset, start.y*scale + yoffset, BALL_RADIUS, box2d);
    }
    private void setEndAt(PointF end) {
        endHole = new End2D(end.x*scale + xoffset, end.y*scale + yoffset, END_RADIUS, BALL_RADIUS, box2d);
    }

    public void setCreatorHeight(float creatorHeight) {
        this.creatorHeight = creatorHeight;
    }
    public void setCreatorWidth(float creatorWidth) {
        this.creatorWidth = creatorWidth;
    }


    public void setCustChrono(CustomChronometer chronometer) {
        custChrono = chronometer;
    }
}

