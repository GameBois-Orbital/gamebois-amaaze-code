package com.gamebois.amaaze.repository;

import android.util.Log;

import com.gamebois.amaaze.livedata.MazeLiveData;
import com.gamebois.amaaze.model.Maze;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.EnumMap;

public class MazeRepository {

    private static final String TAG = "Maze Repository";
    private static MazeRepository singletonInstance;
    private final FirebaseFirestore firestore;
    private EnumMap<Filter, MazeLiveData> mazeLiveDataMap;
    private EnumMap<Filter, Query> querySupplyMap;

    private MazeRepository() {
        firestore = FirebaseFirestore.getInstance();
        mazeLiveDataMap = new EnumMap<Filter, MazeLiveData>(Filter.class);
        querySupplyMap = new EnumMap<Filter, Query>(Filter.class);
        generateQueries();
    }

    public static MazeRepository getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new MazeRepository();
        }
        return singletonInstance;
    }

    private void generateQueries() {

        //TODO: Finish remaining queries

        Query publicPopularityQuery = firestore.collection("mazes")
                .whereEqualTo("isPublic", true)
                .orderBy("numLikes", Query.Direction.DESCENDING);
        querySupplyMap.put(Filter.PUBLIC_BY_POPULARITY, publicPopularityQuery);

        Query privatePopularityQuery = firestore.collection("mazes")
                .whereEqualTo("isPublic", false)
                .orderBy("numLikes", Query.Direction.DESCENDING);
        querySupplyMap.put(Filter.PRIVATE_BY_POPULARITY, privatePopularityQuery);

        Query privateDateQuery = firestore.collection("mazes")
                .whereEqualTo("isPublic", false)
                .orderBy("timeCreated", Query.Direction.DESCENDING);
        querySupplyMap.put(Filter.PRIVATE_BY_DATE, privateDateQuery);

        Query publicDateQuery = firestore.collection("mazes")
                .whereEqualTo("isPublic", true)
                .orderBy("timeCreated", Query.Direction.DESCENDING);
        querySupplyMap.put(Filter.PUBLIC_BY_DATE, publicDateQuery);
    }

    public MazeLiveData getPublicByPopularity() {
        return getFromFilter(Filter.PUBLIC_BY_POPULARITY);
    }

    public MazeLiveData getPrivateByPopularity() {
        return getFromFilter(Filter.PRIVATE_BY_POPULARITY);
    }

    public MazeLiveData getPublicByDate() {
        return getFromFilter(Filter.PUBLIC_BY_DATE);
    }

    public MazeLiveData getPrivateByDate() {
        return getFromFilter(Filter.PRIVATE_BY_DATE);
    }

    public void addMaze(Maze maze) {
        firestore.collection("mazes")
                .document(maze.getUniqueID())
                .set(maze);
    }

    public void updateMaze(Maze maze) {
        //TODO
    }

    public void deleteMaze(Maze maze) {
        //TODO
    }

    //Check for null and instantiate
    public MazeLiveData getFromFilter(Filter filter) {
        MazeLiveData requested = mazeLiveDataMap.get(filter);
        if (requested == null) {
            requested = new MazeLiveData(querySupplyMap.get(filter));
            mazeLiveDataMap.put(filter, requested);
            Log.d(TAG, "Requested");
        }
        return requested;
    }

    enum Filter {
        PUBLIC_BY_DATE,
        PUBLIC_BY_POPULARITY,
        PUBLIC_BY_LIKED,
        PRIVATE_BY_DATE,
        PRIVATE_BY_POPULARITY
    }


}
