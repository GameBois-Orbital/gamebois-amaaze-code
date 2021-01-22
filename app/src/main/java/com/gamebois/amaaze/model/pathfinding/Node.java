package com.gamebois.amaaze.model.pathfinding;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

public class Node {

    public final int x, y;

    public static final String LOG_TAG = Node.class.getSimpleName();
    public float globalGoal = Integer.MAX_VALUE;
    public float localGoal = Integer.MAX_VALUE;
    private Node parent = null;
    List<Node> neighbours = new ArrayList<>();
    private boolean isObstacleComputed = false;
    private boolean isObstacle = false;
    private Status status = Status.UNVISITED;
    private boolean isVisited = false;
    private Path cell;

    public void setObstacle(boolean isObstacle) {
        this.isObstacleComputed = true;
        this.isObstacle = isObstacle;
    }

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

    public Path getCell() {
        return this.cell;
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

    public Node getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isObstacleComputed() {
        return isObstacleComputed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        START, END, UNVISITED, VISITED, FRONTIER
    }
}
