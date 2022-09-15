package org.hugoandrade.worldcup2018.predictor.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.codehaus.jackson.map.util.ISO8601Utils;

import javax.persistence.*;
import java.util.Date;

import static org.hugoandrade.worldcup2018.predictor.backend.model.Match.Entry.Cols.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match implements Comparable<Match> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(ID) private String mID;
    @JsonProperty(MATCH_NUMBER) private int mMatchNo;
    @JsonProperty(HOME_TEAM_ID) private String mHomeTeamID;
    @JsonProperty(AWAY_TEAM_ID) private String mAwayTeamID;
    @Transient private Country mHomeTeam;
    @Transient private Country mAwayTeam;
    @JsonProperty(HOME_TEAM_GOALS) private int mHomeTeamGoals;
    @JsonProperty(AWAY_TEAM_GOALS) private int mAwayTeamGoals;
    @JsonProperty(HOME_TEAM_NOTES) private String mHomeTeamNotes;
    @JsonProperty(AWAY_TEAM_NOTES) private String mAwayTeamNotes;
    @JsonProperty(STAGE) private String mStage;
    @JsonProperty(GROUP) private String mGroup;
    @JsonProperty(STADIUM) private String mStadium;
    @JsonProperty(DATE_AND_TIME) private Date mDateAndTime;

    public Match() { }

    public Match(int matchNumber, String homeTeamID, String awayTeamID,
                 String date, String stadium, String group, String stage) {
        this(null, matchNumber, homeTeamID, awayTeamID,
                -1, -1, null, null,
                group, stage, stadium, ISO8601UtilsWrapper_parse(date));
    }

    public Match(String id,
                 int matchNo,
                 String homeTeamID,
                 String awayTeamID,
                 int homeGoals,
                 int awayGoals,
                 String homeTeamNotes,
                 String awayTeamNotes,
                 String group,
                 String stage,
                 String stadium,
                 Date dateAndTime) {
        mID = id;
        mMatchNo = matchNo;
        mHomeTeamID = homeTeamID;
        mAwayTeamID = awayTeamID;
        mHomeTeamGoals = homeGoals;
        mAwayTeamGoals = awayGoals;
        mHomeTeamNotes = homeTeamNotes;
        mAwayTeamNotes = awayTeamNotes;
        mGroup = group;
        mStadium = stadium;
        mStage = stage;
        mDateAndTime = dateAndTime;
    }

    public String getID() {
        return mID;
    }

    public void setMatchNumber(int matchNumber) {
        this.mMatchNo = matchNumber;
    }

    public int getMatchNumber() {
        return mMatchNo;
    }

    public String getHomeTeamID() {
        return mHomeTeamID;
    }

    public void setHomeTeamID(String homeTeamID) {
        mHomeTeamID = homeTeamID;
    }

    public String getAwayTeamID() {
        return mAwayTeamID;
    }

    public void setAwayTeamID(String awayTeamID) {
        mAwayTeamID = awayTeamID;
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

    public String getHomeTeamNotes() {
        return mHomeTeamNotes;
    }

    public void setHomeTeamNotes(String homeTeamNotes) {
        mHomeTeamNotes = homeTeamNotes;
    }

    public String getAwayTeamNotes() {
        return mAwayTeamNotes;
    }

    public void setAwayTeamNotes(String awayTeamNotes) {
        mAwayTeamNotes = awayTeamNotes;
    }

    public Country getHomeTeam() {
        return mHomeTeam;
    }

    public void setHomeTeam(Country homeTeam) {
        mHomeTeam = homeTeam;
    }

    public Country getAwayTeam() {
        return mAwayTeam;
    }

    public void setAwayTeam(Country awayTeam) {
        mAwayTeam= awayTeam;
    }

    public String getHomeTeamName() {
        return mHomeTeam == null ? getHomeTeamID() : mHomeTeam.getName();
    }

    public String getAwayTeamName() {
        return mAwayTeam == null ? getAwayTeamID() : mAwayTeam.getName();
    }

    public String getStage() {
        return mStage;
    }

    public String getGroup() {
        return mGroup;
    }

    public String getStadium() {
        return mStadium;
    }

    public Date getDateAndTime() {
        return mDateAndTime;
    }

    public Country getMHomeTeam() {
        return mHomeTeam;
    }

    @Override
    public int compareTo(Match o) {
        return this.mMatchNo - o.mMatchNo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Match)) return false;
        if (!areEqual(((Match) o).mID, this.mID)) return false;
        if (((Match) o).mMatchNo != this.mMatchNo) return false;
        if (((Match) o).mHomeTeamGoals != this.mHomeTeamGoals) return false;
        if (((Match) o).mAwayTeamGoals != this.mAwayTeamGoals) return false;
        if (!areEqual(((Match) o).mDateAndTime, this.mDateAndTime)) return false;
        if (!areEqual(((Match) o).mHomeTeamID, this.mHomeTeamID)) return false;
        if (!areEqual(((Match) o).mAwayTeamID, this.mAwayTeamID)) return false;
        if (!areEqual(((Match) o).mHomeTeamNotes, this.mHomeTeamNotes)) return false;
        if (!areEqual(((Match) o).mAwayTeamNotes, this.mAwayTeamNotes)) return false;
        if (!areEqual(((Match) o).mStage, this.mStage)) return false;
        if (!areEqual(((Match) o).mGroup, this.mGroup)) return false;
        if (!areEqual(((Match) o).mStadium, this.mStadium)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "id: " + mID
                + ", MatchNo: " + Integer.toString(mMatchNo)
                + ", HomeTeam: " + mHomeTeamID
                + ", AwayTeam: " + mAwayTeamID
                + ", HomeTeamGoals: " + Integer.toString(mHomeTeamGoals)
                + ", AwayTeamGoals: " + Integer.toString(mAwayTeamGoals)
                + ", HomeTeamNotes: " + mHomeTeamNotes
                + ", AwayTeamNotes: " + mAwayTeamNotes
                + ", Group: " + mGroup
                + ", Stage: " + mStage
                + ", Stadium: " + mStadium
                + ", DateTime: " + ISO8601Utils.format(mDateAndTime);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private static boolean areEqual(String obj1, String obj2) {
        if (obj1 == null && obj2 == null)
            return true;
        if (obj1 != null && obj2 != null)
            return obj1.equals(obj2);

        return false;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean areEqual(Date date1, Date date2) {
        if (date1 == null && date2 == null)
            return true;
        if (date1 != null && date2 != null)
            return date1.equals(date2);

        return false;
    }

    public static class Entry {

        public static final String TABLE_NAME = "Match";

        public static class Cols {
            public static final String ID = "id";
            public static final String MATCH_NUMBER = "MatchNumber";
            public static final String HOME_TEAM_ID = "HomeTeamID";
            public static final String AWAY_TEAM_ID = "AwayTeamID";
            public static final String HOME_TEAM_GOALS = "HomeTeamGoals";
            public static final String AWAY_TEAM_GOALS = "AwayTeamGoals";
            public static final String HOME_TEAM_NOTES = "HomeTeamNotes";
            public static final String AWAY_TEAM_NOTES = "AwayTeamNotes";
            public static final String GROUP = "GroupLetter";
            public static final String STAGE = "Stage";
            public static final String STADIUM = "Stadium";
            public static final String DATE_AND_TIME = "DateAndTime";
        }
    }

    private static Date ISO8601UtilsWrapper_parse(String originalDate) {
        if (originalDate == null) return null;
        String date =
                originalDate.substring(4, 4 + 4) + "-" + // year
                originalDate.substring(2, 2 + 2) + "-" + // month
                originalDate.substring(0, 2) + "T" + // day
                originalDate.substring(8, 8 + 2) + ":" + // hour
                originalDate.substring(10, 10 + 2) + ":" + // minute
                "00" + // second
                "Z"
                ;

        return ISO8601Utils.parse(date);
    }
}
