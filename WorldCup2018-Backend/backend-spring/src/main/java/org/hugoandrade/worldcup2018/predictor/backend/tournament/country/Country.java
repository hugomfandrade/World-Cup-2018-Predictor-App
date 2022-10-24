package org.hugoandrade.worldcup2018.predictor.backend.tournament.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country implements Comparable<Country>, Serializable {

    @javax.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String mID;
    private String mName;
    private String mGroup;
    private float mCoefficient;
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

    private boolean mHasAdvancedGroupStage;

    public Country() {
    }

    public Country(String name, String group, int drawingOfLots) {
        mName = name;
        mGroup = group;
        mDrawingOfLots = drawingOfLots;
    }

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
    public int compareTo(Country o) {
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

    public enum Tournament {

        Russia("Russia"),
        SaudiArabia("Saudi Arabia"),
        Egypt("Egypt"),
        Uruguay("Uruguay"),

        Portugal("Portugal"),
        Spain("Spain"),
        Morocco("Morocco"),
        Iran("Iran"),

        France("France"),
        Australia("Australia"),
        Peru("Peru"),
        Denmark("Denmark"),

        Argentina("Argentina"),
        Iceland("Iceland"),
        Croatia("Croatia"),
        Nigeria("Nigeria"),

        Brazil("Brazil"),
        Switzerland("Switzerland"),
        CostaRica("Costa Rica"),
        Serbia("Serbia"),

        Germany("Germany"),
        Mexico("Mexico"),
        Sweden("Sweden"),
        SouthKorea("South Korea"),

        Belgium("Belgium"),
        Panama("Panama"),
        Tunisia("Tunisia"),
        England("England"),

        Poland("Poland"),
        Senegal("Senegal"),
        Colombia("Colombia"),
        Japan("Japan");

        public final String name;

        Tournament(String group) {
            name = group;
        }
    }
}
