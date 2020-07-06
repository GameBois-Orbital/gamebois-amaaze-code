package com.gamebois.amaaze;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

@SuppressLint("ParcelCreator")
public class ParcelableListOfLists implements Parcelable {

    private ArrayList<ArrayList<PointF>> listOfLists;

    public ParcelableListOfLists(ArrayList<ArrayList<PointF>> listOfLists) {
        this.listOfLists = listOfLists;
    }

    public ArrayList<ArrayList<PointF>> getListOfLists() {
        return listOfLists;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (listOfLists != null) {
            dest.writeInt(listOfLists.size());

            for (ArrayList<PointF> list : listOfLists) {
                dest.writeTypedList(list);
            }
        } else {
            dest.writeInt(-1);
        }

    }

    public ParcelableListOfLists(Parcel in) {
        int size = in.readInt();

        if (size != -1) {
            this.listOfLists = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                ArrayList<PointF> list = in.createTypedArrayList(PointF.CREATOR);
                listOfLists.add(list);
            }
        } else {
            this.listOfLists = null;
        }
    }
}
