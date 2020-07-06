package com.gamebois.amaaze.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;

public abstract class MazeViewModel extends ViewModel {

    public abstract Query getQuery();
}
