package com.gamebois.amaaze.model.pathfinding;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class PathFinder {

    public static final String LOG_TAG = PathFinder.class.getSimpleName();

    private final int screenHeight;
    private final int screenWidth;
    private final List<Path> pathList;
    private final int numNodes;
    public Node[] nodes;
    private List<PointF> successfulPath = null;
    private int cellSize = 20;
    private int rowLength;
    private PointF startCoords;
    private PointF endCoords;
    private float straightDist = 1.0f;
    private float diagonalDist = 1.414f;
    private Node start;
    private Node end;
    private MazeObserver mazeObserver;

    public PathFinder(int screenHeight, int screenWidth, List<Path> pathList, PointF start, PointF end) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.numNodes = (screenHeight / cellSize) * (screenWidth / cellSize);
        this.nodes = new Node[numNodes];
        this.pathList = pathList;
        this.startCoords = start;
        this.endCoords = end;
        this.rowLength = screenWidth / cellSize;
    }

    public void setUpGraph() {
        //Initialise nodes with paths
        for (int y = 0; y < screenHeight; y += cellSize) {
            for (int x = 0; x < screenWidth; x += cellSize) {
                int arrayPos = getPosAt(x, y);
                int abovePos = getPosAbove(arrayPos);
                int leftPos = getPosToLeft(arrayPos);
                int diagonalLeftPos = getPosAbove(arrayPos) - 1;
                int diagonalRightPos = getPosAbove(arrayPos) + 1;


                // Log.d(LOG_TAG, String.valueOf(arrayPos));
                // Log.d(LOG_TAG, String.valueOf(abovePos));
                // Log.d(LOG_TAG, String.valueOf(leftPos));

                //Add current node to array
                Node toAdd = new Node(x, y);
                toAdd.setCell(cellSize);
                checkObstacle(toAdd);
                nodes[arrayPos] = toAdd;
                Log.d(LOG_TAG, toAdd.toString());
                if (!toAdd.isObstacle()) {
                    addNeighbours(toAdd, abovePos, abovePos >= 0);
                    addNeighbours(toAdd, leftPos, leftPos >= 0);
                    addNeighbours(toAdd, diagonalLeftPos,
                            diagonalLeftPos >= 0 && (diagonalLeftPos + 1) % rowLength != 0);
                    addNeighbours(toAdd, diagonalRightPos,
                            diagonalRightPos >= 0 && diagonalRightPos % rowLength != 0);
                }
            }
        }

//        Test
//        Node one = nodes[0];
//        Node two = nodes[2];
//        Log.d(LOG_TAG, "I'm printing the neighbours of the first node now");
//        for (Node neighbour : one.neighbours) {
//            Log.d(LOG_TAG, "Neighbour: " + neighbour.toString());
//        }
    }

    public boolean findPaths() {
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
            if (current == end || contains(end.x, end.y, current)) {
                Log.d(LOG_TAG, "Ended");
                //Retrace steps
//                Node previous = end.getParent();
//                while (previous != start) {
//                    Log.d(LOG_TAG, previous.toString());
//                    successfulPath.add(new PointF(previous.x, previous.y));
//                    previous = previous.getParent();
//                }
                return true;
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
            notifyObservers();
        }

        return false;
    }

    private void addNeighbours(Node toAdd, int neighbourPosition, boolean boundaryCondition) {
        if (boundaryCondition) {
            Node neighbour = nodes[neighbourPosition];
            checkObstacle(neighbour);
            if (!neighbour.isObstacle()) {
                toAdd.neighbours.add(neighbour);
                neighbour.neighbours.add(toAdd);
            }
        }
    }

    private void checkObstacle(Node neighbour) {
        if (neighbour.isObstacleComputed()) {
            return;
        }

        for (Path contour : pathList) {
            Path path = new Path();
            path.op(contour, neighbour.getCell(), Path.Op.INTERSECT);
            if (!path.isEmpty()) {
                neighbour.setObstacle(true);
                return;
            }
        }

        neighbour.setObstacle(false);
    }

    public boolean contains(int pointX, int pointY, Node n) {
        boolean withinX = pointX >= n.x && pointX <= n.x + cellSize;
        boolean withinY = pointY >= n.y && pointY <= n.y + cellSize;
        return withinX && withinY;
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
        this.start = nodes[getPosAt(Math.round(startCoords.x), Math.round(startCoords.y))];
        this.end = nodes[getPosAt(Math.round(endCoords.x), Math.round(endCoords.y))];
        start.setStatus(Node.Status.START);
        end.setStatus(Node.Status.END);
    }

    /**
     * Maps Android screen coordinates to Node in array
     *
     * @param x x coordinate of top right corner of cell
     * @param y y coordinate of top right corner of cell
     * @return Corresponding Node in array
     */
    private int getPosAt(int x, int y) {
        int row = y / cellSize;
        int column = x / cellSize;
        int arrayPos = cellSize * row + column;
//        Log.d(LOG_TAG, "Cell at " + x + ", " + y + " is " + arrayPos);
        return arrayPos;
    }

    private int getPosBelow(int arrayPos) {
        return arrayPos + rowLength;
    }

    private int getPosAbove(int arrayPos) {
        return arrayPos - rowLength;
    }

    private int getPosToLeft(int arrayPos) {
        return arrayPos - 1;
    }

    private int getPosToRight(int arrayPos) {
        return arrayPos + 1;
    }

    public void subscribeToNodes(MazeObserver observer) {
        this.mazeObserver = observer;
    }

    private void notifyObservers() {
        if (mazeObserver != null) {
            mazeObserver.updateNodes();
        }
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