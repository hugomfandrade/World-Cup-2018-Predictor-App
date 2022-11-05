package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

import static org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryDto.Entry.Cols.*;

public class CountryDto implements Comparable<CountryDto>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(ID) private String mID;
    @JsonProperty(NAME) private String mName;
    @JsonProperty(GROUP) private String mGroup;
    @JsonProperty(COEFFICIENT) private float mCoefficient;
    @JsonProperty(MATCHES_PLAYED) private int mMatchesPlayed;
    @JsonProperty(GOALS_FOR) private int mGoalsFor;
    @JsonProperty(GOALS_AGAINST) private int mGoalsAgainst;
    @JsonProperty(GOALS_DIFFERENCE) private int mGoalsDifference;
    @JsonProperty(POINTS) private int mPoints;
    @JsonProperty(POSITION) private int mPosition;
    @JsonProperty(VICTORIES) private int mVictories;
    @JsonProperty(DRAWS) private int mDraws;
    @JsonProperty(DEFEATS) private int mDefeats;
    @JsonProperty(FAIR_PLAY_POINTS) private int mFairPlayPoints;
    @JsonProperty(DRAWING_OF_LOTS) private int mDrawingOfLots;

    private boolean mHasAdvancedGroupStage;

    public CountryDto() {
    }

    public CountryDto(String name, String group, int drawingOfLots) {
        mName = name;
        mGroup = group;
        mDrawingOfLots = drawingOfLots;
    }

    public CountryDto(String id, String name,
                      int matchesPlayed,
                      int victories, int draws, int defeats,
                      int goalsFor, int goalsAgainst, int goalsDifference,
                      String group,
                      int points, int position,
                      int fairPlayPoints, int drawingOfLots) {
        this(id, name, matchesPlayed, victories, draws, defeats, goalsFor, goalsAgainst, goalsDifference, group, points, position, 0, fairPlayPoints, drawingOfLots);
    }

    @Deprecated
    public CountryDto(String id, String name,
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

    public void setID(String id) {
        this.mID = id;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setGroup(String group) {
        this.mGroup = group;
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

    public void setDrawingOfLots(int drawingOfLots) {
        this.mDrawingOfLots = drawingOfLots;
    }

    @Override
    public int compareTo(CountryDto o) {
        if (this.getPoints() != o.getPoints())
            return this.getPoints() - o.getPoints();
        if (this.getGoalsDifference() != o.getGoalsDifference())
            return this.getGoalsDifference() - o.getGoalsDifference();
        if (this.getGoalsFor() != o.getGoalsFor())
            return this.getGoalsFor() - o.getGoalsFor();
        if (this.getFairPlayPoints() != o.getFairPlayPoints())
            return this.getFairPlayPoints() - o.getFairPlayPoints();
        if (this.getDrawingOfLots() != o.getDrawingOfLots())
            return -(this.getDrawingOfLots() - o.getDrawingOfLots());
        return 0;
    }

    public String toString() {
        return this.mName
                + ", id: " + this.mID
                + ", Points: " + this.mPoints
                + ", GD: " + this.mGoalsDifference
                + ", GF: " + this.mGoalsFor
                + ", GA: " + this.mGoalsAgainst
                + ", MP: " + this.mMatchesPlayed
                + ", P: " + this.mPosition
                + ", G: " + this.mGroup;
    }

    public void set(CountryDto o) {
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
        if (o instanceof CountryDto) {
            CountryDto c = (CountryDto) o;
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
