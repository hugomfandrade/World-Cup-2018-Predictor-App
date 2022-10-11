package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import com.fasterxml.jackson.annotation.JsonProperty;

import static org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionDto.Entry.Cols.*;

public class PredictionDto {

    @JsonProperty(ID) private String mID;
    @JsonProperty(USER_ID) private String mUserID;
    @JsonProperty(MATCH_NO) private int mMatchNo;
    @JsonProperty(HOME_TEAM_GOALS) private int mHomeTeamGoals;
    @JsonProperty(AWAY_TEAM_GOALS) private int mAwayTeamGoals;
    @JsonProperty(SCORE) private int mScore;

    public PredictionDto() {}

    public static PredictionDto emptyInstance(int matchNumber, String userID) {
        return new PredictionDto(-1, -1, matchNumber, userID);
    }

    public PredictionDto(int homeTeamGoals, int awayTeamGoals, int matchNo, String userID) {
        mHomeTeamGoals = homeTeamGoals;
        mAwayTeamGoals = awayTeamGoals;
        mMatchNo = matchNo;
        mUserID = userID;
        mScore = -1;
    }

    public PredictionDto(int homeTeamGoals, int awayTeamGoals, int matchNo) {
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
        if (!(o instanceof PredictionDto)) return false;
        if (!areEqual(((PredictionDto) o).mUserID, this.mUserID)) return false;
        if (((PredictionDto) o).mMatchNo != this.mMatchNo) return false;
        if (((PredictionDto) o).mHomeTeamGoals != this.mHomeTeamGoals) return false;
        if (((PredictionDto) o).mAwayTeamGoals != this.mAwayTeamGoals) return false;
        if (((PredictionDto) o).mScore != this.mScore) return false;
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

    public static class Entry {

        public static final String TABLE_NAME = "Prediction";

        public static class Cols {
            public static final String ID = "id";
            public static final String USER_ID = "UserID";
            public static final String MATCH_NO = "MatchNumber";
            public static final String HOME_TEAM_GOALS = "HomeTeamGoals";
            public static final String AWAY_TEAM_GOALS = "AwayTeamGoals";
            public static final String SCORE = "Score";
        }
    }
}
