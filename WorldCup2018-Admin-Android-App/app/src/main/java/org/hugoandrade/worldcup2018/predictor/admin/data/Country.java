package org.hugoandrade.worldcup2018.predictor.admin.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Country implements Comparable<Country>, Parcelable {

    private String mID;
    private String mName;
    private String mGroup;
    private int mMatchesPlayed;
    private int mGoalsFor;
    private int mGoalsAgainst;
    private int mGoalsDifference;
    private int mPoints;
    private int mPosition;
    private int mVictories;
    private int mDraws;
    private int mDefeats;
    private int mFairPlayPoints;
    private int mDrawingOfLots;

    public static class Entry {

        public static final String TABLE_NAME = "Country";

        public static class Cols {
            public static final String ID = "id";
            public static final String NAME = "Name";
            public static final String MATCHES_PLAYED = "MatchesPlayed";
            public static final String VICTORIES = "Victories";
            public static final String DRAWS = "Draws";
            public static final String DEFEATS = "Defeats";
            public static final String GOALS_FOR = "GoalsFor";
            public static final String GOALS_AGAINST = "GoalsAgainst";
            public static final String GOALS_DIFFERENCE = "GoalsDifference";
            public static final String GROUP = "GroupLetter";
            public static final String POSITION = "Position";
            public static final String POINTS = "Points";
            public static final String FAIR_PLAY_POINTS = "FairPlayPoints";
            public static final String DRAWING_OF_LOTS = "DrawingOfLots";
        }
    }

    public Country(String id,
                   String name,
                   int matchesPlayed,
                   int victories,
                   int draws,
                   int defeats,
                   int goalsFor,
                   int goalsAgainst,
                   int goalsDifference,
                   String group,
                   int points,
                   int position,
                   int fairPlayPoints,
                   int drawingOfLots) {
        mID = id;
        mName = name;
        mMatchesPlayed = matchesPlayed;
        mVictories = victories;
        mDraws = draws;
        mDefeats = defeats;
        mGoalsFor = goalsFor;
        mGoalsAgainst = goalsAgainst;
        mGoalsDifference = goalsDifference;
        mGroup = group;
        mPoints = points;
        mPosition = position;
        mFairPlayPoints = fairPlayPoints;
        mDrawingOfLots = drawingOfLots;
    }

    public String getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public String getGroup() {
        return mGroup;
    }

    public int getMatchesPlayed() {
        return mMatchesPlayed;
    }

    public int getGoalsFor() {
        return mGoalsFor;
    }

    public int getGoalsAgainst() {
        return mGoalsAgainst;
    }

    public int getGoalsDifference() {
        return mGoalsDifference;
    }

    public int getPoints() {
        return mPoints;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getVictories() {
        return mVictories;
    }

    public int getDraws() {
        return mDraws;
    }

    public int getDefeats() {
        return mDefeats;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        mMatchesPlayed = matchesPlayed;
    }

    public void setGoalsFor(int goalsFor) {
        mGoalsFor = goalsFor;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        mGoalsAgainst = goalsAgainst;
    }

    public void setGoalsDifference(int goalsDifference) {
        mGoalsDifference = goalsDifference;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public void setVictories(int victories) {
        mVictories = victories;
    }

    public void setDraws(int draws) {
        mDraws = draws;
    }

    public void setDefeats(int defeats) {
        mDefeats = defeats;
    }

    public int getFairPlayPoints() {
        return mFairPlayPoints;
    }

    public int getDrawingOfLots() {
        return mDrawingOfLots;
    }

    public Country(Parcel in) {
        mID = in.readString();
        mName = in.readString();
        mGroup = in.readString();
        mMatchesPlayed = in.readInt();
        mGoalsFor = in.readInt();
        mGoalsAgainst = in.readInt();
        mGoalsDifference = in.readInt();
        mPoints = in.readInt();
        mPosition = in.readInt();
        mVictories = in.readInt();
        mDraws = in.readInt();
        mDefeats = in.readInt();
        mFairPlayPoints = in.readInt();
        mDrawingOfLots = in.readInt();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    public String toString() {
        return this.mName
                + ", id: " + this.mID
                + ", Points: " + Integer.toString(this.mPoints)
                + ", GD: " + Integer.toString(this.mGoalsDifference)
                + ", GF: " + Integer.toString(this.mGoalsFor)
                + ", GA: " + Integer.toString(this.mGoalsAgainst)
                + ", MP: " + Integer.toString(this.mMatchesPlayed)
                + ", P: " + Integer.toString(this.mPosition)
                + ", G: " + this.mGroup;
    }

    @Override
    public int compareTo(@NonNull Country o) {
        if (mPoints != o.mPoints)
            return mPoints - o.mPoints;
        if (mGoalsDifference != o.mGoalsDifference)
            return mGoalsDifference - o.mGoalsDifference;
        if (mGoalsFor != o.mGoalsFor)
            return mGoalsFor - o.mGoalsFor;
        if (mFairPlayPoints != o.mFairPlayPoints)
            return mFairPlayPoints - o.mFairPlayPoints;
        return 0;
    }

    public void set(Country o) {
        if (!this.mName.equals(o.mName) || !this.mGroup.equals(o.mGroup))
            return;

        mMatchesPlayed = o.mMatchesPlayed;
        mVictories = o.mVictories;
        mDraws = o.mDraws;
        mDefeats = o.mDefeats;
        mGoalsFor = o.mGoalsFor;
        mGoalsAgainst = o.mGoalsAgainst;
        mGoalsDifference = o.mGoalsDifference;
        mPoints = o.mPoints;
        mPosition = o.mPosition;
        mDrawingOfLots = o.mDrawingOfLots;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Country) {
            Country c = (Country) o;
            if (!mID.equals(c.mID))
                return false;
            if (!mName.equals(c.mName))
                return false;
            if (mMatchesPlayed != c.mMatchesPlayed)
                return false;
            if (mVictories != c.mVictories)
                return false;
            if (mDraws != c.mDraws)
                return false;
            if (mDefeats != c.mDefeats)
                return false;
            if (mGoalsFor != c.mGoalsFor)
                return false;
            if (mGoalsAgainst != c.mGoalsAgainst)
                return false;
            if (mGoalsDifference != c.mGoalsDifference)
                return false;
            if (!mGroup.equals(c.mGroup))
                return false;
            if (mPoints != c.mPoints)
                return false;
            if (mPosition != c.mPosition)
                return false;
            if (mFairPlayPoints != c.mFairPlayPoints)
                return false;
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mName);
        dest.writeString(mGroup);
        dest.writeInt(mMatchesPlayed);
        dest.writeInt(mGoalsFor);
        dest.writeInt(mGoalsAgainst);
        dest.writeInt(mGoalsDifference);
        dest.writeInt(mPoints);
        dest.writeInt(mPosition);
        dest.writeInt(mVictories);
        dest.writeInt(mDraws);
        dest.writeInt(mDefeats);
        dest.writeInt(mFairPlayPoints);
        dest.writeInt(mDrawingOfLots);
    }
}
