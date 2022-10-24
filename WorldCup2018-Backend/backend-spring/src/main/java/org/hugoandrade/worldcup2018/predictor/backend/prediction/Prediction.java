package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Prediction {

    @javax.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String mID;
    private String mUserID;
    private int mMatchNo;
    private int mHomeTeamGoals;
    private int mAwayTeamGoals;
    private int mScore;

    public Prediction() {}

    public Prediction(int homeTeamGoals, int awayTeamGoals, int matchNo, String userID) {
        mHomeTeamGoals = homeTeamGoals;
        mAwayTeamGoals = awayTeamGoals;
        mMatchNo = matchNo;
        mUserID = userID;
        mScore = -1;
    }

    public Prediction(int homeTeamGoals, int awayTeamGoals, int matchNo) {
        this(homeTeamGoals, awayTeamGoals, matchNo, null);
    }

    public void setID(String id) {
        mID = id;
    }

    public String getID() {
        return mID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getUserID() {
        return mUserID;
    }

    public int getMatchNumber() {
        return mMatchNo;
    }

    public void setMatchNumber(int matchNo) {
        this.mMatchNo = matchNo;
    }

    public int getHomeTeamGoals() {
        return mHomeTeamGoals;
    }

    public void setHomeTeamGoals(int homeTeamGoals) {
        mHomeTeamGoals = homeTeamGoals;
    }

    public int getAwayTeamGoals() {
        return mAwayTeamGoals;
    }

    public void setAwayTeamGoals(int awayTeamGoals) {
        mAwayTeamGoals = awayTeamGoals;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "mID=" + mID +
                ", mUserID='" + mUserID + '\'' +
                ", mMatchNo=" + mMatchNo +
                ", mHomeTeamGoals=" + mHomeTeamGoals +
                ", mAwayTeamGoals=" + mAwayTeamGoals +
                ", mScore=" + mScore +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Prediction)) return false;
        if (!areEqual(((Prediction) o).mUserID, this.mUserID)) return false;
        if (((Prediction) o).mMatchNo != this.mMatchNo) return false;
        if (((Prediction) o).mHomeTeamGoals != this.mHomeTeamGoals) return false;
        if (((Prediction) o).mAwayTeamGoals != this.mAwayTeamGoals) return false;
        if (((Prediction) o).mScore != this.mScore) return false;
        return true;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private static boolean areEqual(String obj1, String obj2) {
        if (obj1 == null && obj2 == null)
            return true;
        if (obj1 != null && obj2 != null)
            return obj1.equals(obj2);

        return false;
    }
}
