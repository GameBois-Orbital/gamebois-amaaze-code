package com.gamebois.amaaze.model.pathfinding;

import android.graphics.Path;
import android.util.Log;

import java.util.List;

public class PathFinder {

    public static final String LOG_TAG = PathFinder.class.getSimpleName();

    private final int screenHeight;
    private final int screenWidth;
    private final List<Path> pathList;
    private Node[] nodes;
    private int cellSize = 10;
    private int rowLength;

    public PathFinder(int screenHeight, int screenWidth, List<Path> pathList) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.nodes = new Node[(screenHeight / cellSize) * (screenWidth / cellSize)];
        this.pathList = pathList;
        this.rowLength = screenWidth / cellSize;
    }

    public void setUpGraph() {
        //Initialise nodes with paths
        for (int y = 0; y < screenHeight; y += cellSize) {
            for (int x = 0; x < screenWidth; x += cellSize) {
                int arrayPos = getPosAt(x, y);
//                Log.d(LOG_TAG, String.valueOf(arrayPos));
                int abovePos = getPosAbove(arrayPos);
//                Log.d(LOG_TAG, String.valueOf(abovePos));
                int leftPos = getPosToLeft(arrayPos);
//                Log.d(LOG_TAG, String.valueOf(leftPos));


                //Add current node to array
                Node toAdd = new Node(x, y);
                toAdd.setCell(cellSize);
                nodes[arrayPos] = toAdd;

                //Add node to neighbours
                if (abovePos >= 0) {
                    Node above = nodes[arrayPos];
                    toAdd.neighbours.add(above);
                    above.neighbours.add(toAdd);
                }

                if (leftPos >= 0) {
                    Node left = nodes[leftPos];
                    toAdd.neighbours.add(left);
                    left.neighbours.add(toAdd);
                }
            }
        }

//        Test
        Node one = nodes[0];
        Node two = nodes[2];
        Log.d(LOG_TAG, "I'm ``````````````````````````printing the neighbours of the first node now");
        for (Node neighbour : one.neighbours) {
            Log.d(LOG_TAG, "Neighbour: " + neighbour.toString());
        }
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
        Log.d(LOG_TAG, "Cell at " + x + ", " + y + " is " + arrayPos);
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
}
