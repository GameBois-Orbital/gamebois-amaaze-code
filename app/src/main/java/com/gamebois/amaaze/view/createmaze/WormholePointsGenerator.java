package com.gamebois.amaaze.view.createmaze;

import android.graphics.Path;
import android.graphics.PointF;

import com.gamebois.amaaze.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WormholePointsGenerator {
    String LOG_TAG = WormholePointsGenerator.class.getSimpleName();

    private List<Path> pathList;
    private float screen_width, screen_height;
    private int radius;
    private Random rand;
    ArrayList<PointF> wormholePoints;
    ArrayList<Path> wormholePaths;

    WormholePointsGenerator(List<Path> pathList, float screen_width, float screen_height, int radius) {
        this.pathList = pathList;
        this.screen_width = screen_width;
        this.screen_height = screen_height;
        this.radius = radius;
        this.rand = new Random();
        this.wormholePoints = new ArrayList<>();
        this.wormholePaths = new ArrayList<>();
    }

    public ArrayList<PointF> generate(int numOfWormholes) {
        float wormholeRealmLength = Math.min(screen_width, screen_height);     // realm  = where 68.2% of points land
        if (BuildConfig.DEBUG && wormholeRealmLength != screen_height) {
            throw new AssertionError("Assertion failed");
        }
        float centerOfScreen_x = screen_width / (float) 2;
        float centerOfScreen_y = screen_height / (float) 2;

        while (numOfWormholes > wormholePoints.size()) {
            float rand_x = (float) rand.nextGaussian();
            float rand_y = (float) rand.nextGaussian();

            float point_x = centerOfScreen_x + (rand_x * centerOfScreen_x);
            float point_y = centerOfScreen_y + (rand_y * centerOfScreen_y);

            if ((point_x + radius) > screen_width || (point_x - radius) < 0 || (point_y + radius) > screen_height || (point_y - radius) < 0) {
                continue;
            }

            Path potentialWormhole = new Path();
            potentialWormhole.addCircle(point_x, point_y, radius, Path.Direction.CW);

            if (isAcceptablePoint(potentialWormhole)) {
                wormholePaths.add(potentialWormhole);
                wormholePoints.add(new PointF(point_x, point_y));
            }
        }
        return wormholePoints;
    }


    private boolean isAcceptablePoint(Path potentialWormhole) {
        boolean acceptable = true;
        for (Path contour : pathList) {
            Path path = new Path();
            path.op(contour, potentialWormhole, Path.Op.INTERSECT);
            if (!path.isEmpty()) {
                acceptable = false;
                break;
            }
        }
        if (acceptable) {
            for (Path wormHolePath : wormholePaths) {
                Path path = new Path();
                path.op(wormHolePath, potentialWormhole, Path.Op.INTERSECT);
                if (!path.isEmpty()) {
                    acceptable = false;
                    break;
                }
            }
        }
        return acceptable;
    }
}
