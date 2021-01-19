package com.gamebois.amaaze.model.pathfinding;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

public class Node {

    public static final String LOG_TAG = Node.class.getSimpleName();
    private final int x, y;
    public float globalGoal;
    public float localGoal;
    List<Node> neighbours = new ArrayList<>();
    Node parent = null;
    private boolean isObstacle = false;
    private boolean isVisited = false;
    private Path cell;

    /**
     * Sets the coordinates of the top right corner of the cell
     *
     * @param x x coordinate of top right corner of cell
     * @param y y coordinate of top right corner of cell
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public void setObstacle(List<Path> walls) {
//        for (wall : walls) {
//
//        }

        isObstacle = false;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public void setCell(int size) {
        this.cell = new Path();
        int leftCoord = this.x;
        int topCoord = this.y;
        int rightCoord = this.x + size - 1;
        int bottomCoord = this.y + size - 1;
        cell.addRect(leftCoord, topCoord, rightCoord, bottomCoord, Path.Direction.CCW);
//        Log.d(LOG_TAG, "I'm the node at " + leftCoord + ", " + topCoord);
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
