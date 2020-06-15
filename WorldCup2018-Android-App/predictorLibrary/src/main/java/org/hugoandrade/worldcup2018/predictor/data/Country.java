package org.hugoandrade.worldcup2018.predictor.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Country implements Comparable<Country>, Parcelable {

    @SerializedName(Entry.Cols.ID) private String mID;
    @SerializedName(Entry.Cols.NAME) private String mName;
    @SerializedName(Entry.Cols.GROUP) private String mGroup;
    @SerializedName(Entry.Cols.COEFFICIENT) private float mCoefficient;
    @SerializedName(Entry.Cols.MATCHES_PLAYED) private int mMatchesPlayed;
    @SerializedName(Entry.Cols.GOALS_FOR) private int mGoalsFor;
    @SerializedName(Entry.Cols.GOALS_AGAINST) private int mGoalsAgainst;
    @SerializedName(Entry.Cols.GOALS_DIFFERENCE) private int mGoalsDifference;
    @SerializedName(Entry.Cols.POINTS) private int mPoints;
    @SerializedName(Entry.Cols.POSITION) private int mPosition;
    @SerializedName(Entry.Cols.VICTORIES) private int mVictories;
    @SerializedName(Entry.Cols.DRAWS) private int mDraws;
    @SerializedName(Entry.Cols.DEFEATS) private int mDefeats;
    @SerializedName(Entry.Cols.FAIR_PLAY_POINTS) private int mFairPlayPoints;
    @SerializedName(Entry.Cols.DRAWING_OF_LOTS) private int mDrawingOfLots;

    private boolean mHasAdvancedGroupStage;

    public Country(String id, String name,
                   int matchesPlayed,
                   int victories, int draws, int defeats,
                   int goalsFor, int goalsAgainst, int goalsDifference,
                   String group,
                   int points, int position,
                   int fairPlayPoints, int drawingOfLots) {
        this(id, name, matchesPlayed, victories, draws, defeats, goalsFor, goalsAgainst, goalsDifference, group, points, position, 0, fairPlayPoints, drawingOfLots);
    }

    @Deprecated
    public Country(String id, String name,
                   int matchesPlayed,
                   int victories, int draws, int defeats,
                   int goalsFor, int goalsAgainst, int goalsDifference,
                   String group,
                   int points, int position,
                   float coefficient, int fairPlayPoints, int drawingOfLots) {
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
        mCoefficient = coefficient;
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

    public void setFairPlayPoints(int fairPlayPoints) {
        mFairPlayPoints = fairPlayPoints;
    }

    public int getFairPlayPoints() {
        return mFairPlayPoints;
    }

    public float getCoefficient() {
        return mCoefficient;
    }

    public void setAdvancedGroupStage(boolean hasAdvanced) {
        mHasAdvancedGroupStage = hasAdvanced;
    }

    public boolean hasAdvancedGroupStage() {
        return mHasAdvancedGroupStage;
    }

    public int getDrawingOfLots() {
        return mDrawingOfLots;
    }

    @Override
    public int compareTo(@NonNull Country o) {
        if (mPosition != o.mPosition)
            return mPoints - o.mPoints;
        return 0;
    }

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
        if (o instanceof Country) {
            Country c = (Country) o;
            if (!mID.equals(c.mID)) return false;
            if (!mName.equals(c.mName)) return false;
            if (mMatchesPlayed != c.mMatchesPlayed) return false;
            if (mVictories != c.mVictories) return false;
            if (mDraws != c.mDraws) return false;
            if (mDefeats != c.mDefeats) return false;
            if (mGoalsFor != c.mGoalsFor) return false;
            if (mGoalsAgainst != c.mGoalsAgainst) return false;
            if (mGoalsDifference != c.mGoalsDifference) return false;
            if (!mGroup.equals(c.mGroup)) return false;
            if (mPoints != c.mPoints) return false;
            if (mPosition != c.mPosition) return false;
            if (mFairPlayPoints != c.mFairPlayPoints) return false;
            if (mCoefficient != c.mCoefficient) return false;
            return true;
        }
        return false;
    }

    public Country(Parcel in) {
        mID = in.readString();
        mName = in.readString();
        mGroup = in.readString();
        mCoefficient = in.readFloat();
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
        mHasAdvancedGroupStage = in.readByte() != 0;
        mDrawingOfLots = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mName);
        dest.writeString(mGroup);
        dest.writeFloat(mCoefficient);
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
        dest.writeInt((byte) (mHasAdvancedGroupStage ? 1 : 0));
        dest.writeInt(mDrawingOfLots);
    }

    @Override
    public int describeContents() {
        return 0;
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
            public static final String COEFFICIENT = "Coefficient";
            public static final String FAIR_PLAY_POINTS = "FairPlayPoints";
            public static final String DRAWING_OF_LOTS = "DrawingOfLots";
        }
    }
}
