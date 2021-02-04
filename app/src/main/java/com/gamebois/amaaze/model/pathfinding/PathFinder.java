package com.gamebois.amaaze.model.pathfinding;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class PathFinder {

    public static final String LOG_TAG = PathFinder.class.getSimpleName();

    private final int screenHeight;
    private final int screenWidth;
    public Node[][] nodes;
    private List<PointF> successfulPath = new ArrayList<>();
    private int cellSize = 10;
    private int rowLength;
    private int columnLength;
    private PointF startCoords;
    private PointF endCoords;
    private float straightDist = 1.0f;
    private float diagonalDist = 1.414f;
    private Node start;
    private Node end;

    public PathFinder(int screenHeight, int screenWidth, PointF start, PointF end) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.rowLength = screenWidth / cellSize; //Number of columns (horizontal length)
        this.columnLength = screenHeight / cellSize; //Number of rows (vertical length)
        this.nodes = new Node[rowLength + 1][columnLength + 1];
        this.startCoords = start;
        this.endCoords = end;
    }

    public void setUpGraph() {
        //Initialise nodes with paths
        for (int y = 0; y < screenHeight; y += cellSize) {
            Log.d(LOG_TAG, "Iter " + y);
            for (int x = 0; x < screenWidth; x += cellSize) {
                int yArrPos = y / cellSize;
                int xArrPos = x / cellSize;
                if (nodes[xArrPos][yArrPos] == null) {
                    //Add current node to array
                    Node toAdd = new Node(x, y);
                    toAdd.setCell(cellSize);
                    nodes[xArrPos][yArrPos] = toAdd;
                    if (!toAdd.isObstacle()) {
                        addNeighbours(toAdd, xArrPos, yArrPos);
                    }
                }
            }
        }

//        Test
        Node one = nodes[0][0];
        Node two = nodes[1][0];
        Log.d(LOG_TAG, "I'm printing the neighbours of the first node now");
        for (Node neighbour : one.neighbours) {
            Log.d(LOG_TAG, "Neighbour: " + neighbour.toString());
        }
    }

    public List<PointF> findPaths() {
        setStartAndEnd();

        //Set the local and global value of the start node
        start.localGoal = 0.0f;
        start.globalGoal = getHeuristic(start);
        start.setParent(start);

        //Add start node to the priority queue
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(20, new NodeComparator());
        frontier.add(start);

        while (!frontier.isEmpty()) {

            Log.d(LOG_TAG, "ITER1");
            Node current = frontier.poll();

            if (current == end) {
                Log.d(LOG_TAG, "Ended");
                Node previous = end.getParent();
                while (previous != start) {
                    Log.d(LOG_TAG, previous.toString());
                    successfulPath.add(new PointF(previous.x, previous.y));
                    previous = previous.getParent();
                }
                break;
            }

            if (current.isVisited()) {
                continue;
            }
//             Log.d(LOG_TAG, current.toString());
            current.setVisited(true);
            current.setStatus(Node.Status.VISITED);
            for (Node neighbour : current.neighbours) {
                if (!neighbour.isObstacle()) {
                    if (!neighbour.isVisited()) {
                        frontier.add(neighbour);
                        neighbour.setStatus(Node.Status.FRONTIER);
                    }

                    float newLocal = current.localGoal + getDistance(current, neighbour);
                    if (newLocal < neighbour.localGoal) {
                        neighbour.localGoal = newLocal;
                        neighbour.globalGoal = newLocal + getHeuristic(neighbour);
                        neighbour.setParent(current);
                    }
                }

            }
        }

        return successfulPath;
    }

    private void addNeighbours(Node toAdd, int xArrPos, int yArrPos) {
        if (xArrPos > 0) {
            //Left neighbour exists
            addNeighbours(toAdd, nodes[xArrPos - 1][yArrPos]);
        }

        if (yArrPos > 0) {
            //Above neighbour exists
            addNeighbours(toAdd, nodes[xArrPos][yArrPos - 1]);
        }

        if (xArrPos > 0 && yArrPos > 0) {
            //Diagonal left neighbour exists
            addNeighbours(toAdd, nodes[xArrPos - 1][yArrPos - 1]);
        }

        if (xArrPos < rowLength - 1 && yArrPos > 0) {
            //Diagonal right neighbour exists
            addNeighbours(toAdd, nodes[xArrPos + 1][yArrPos - 1]);
        }
    }

    private void addNeighbours(Node toAdd, Node neighbour) {
        if (!neighbour.isObstacle()) {
            toAdd.neighbours.add(neighbour);
            neighbour.neighbours.add(toAdd);
        }
    }


    private float getDistance(Node current, Node neighbour) {
        int dX = Math.abs(current.x - neighbour.x);
        int dY = Math.abs(current.y - neighbour.y);
        return straightDist * (dX + dY) + (diagonalDist - 2 * straightDist) * Math.min(dX, dY);
    }

    private float getHeuristic(Node n) {
        int dX = Math.abs(n.x - end.x);
        int dY = Math.abs(n.y - end.y);
        return straightDist * (dX + dY) + (diagonalDist - 2 * straightDist) * Math.min(dX, dY);
    }

    private void setStartAndEnd() {
        this.start = nodes[getXPosAt(Math.round(startCoords.x))][getYPosAt(Math.round(startCoords.y))];
        this.end = nodes[getXPosAt(Math.round(endCoords.x))][getYPosAt(Math.round(endCoords.y))];
        start.setStatus(Node.Status.START);
        end.setStatus(Node.Status.END);
    }

    /**
     * Maps Android screen coordinates to Node in array
     *
     * @param x x coordinate of top right corner of cell
     * @return Corresponding x position in array
     */
    public int getXPosAt(int x) {
        return x / cellSize;
    }

    public int getYPosAt(int y) {
        return y / cellSize;
    }

    public void createNodeAt(int xValue, int yValue) {
        int x = xValue * cellSize;
        int y = yValue * cellSize;
        nodes[xValue][yValue] = new Node(x, y);
    }
}

class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        if (o1.globalGoal < o2.globalGoal) {
            return -1;
        } else if (o1.globalGoal > o2.globalGoal) {
            return 1;
        }
        return 0;
    }
}