package com.usal.jorgeav.sportapp.data;

/**
 * Created by Jorge Avila on 25/05/2017.
 */

public class Sport {
    String mName;
    float mLevel;
    int mVotes;

    public Sport(String mName, float mLevel, int mVotes) {
        this.mName = mName;
        this.mLevel = mLevel;
        this.mVotes = mVotes;
    }

    public String getmName() {
        return mName;
    }

    public float getmLevel() {
        return mLevel;
    }

    public int getmVotes() {
        return mVotes;
    }
}
