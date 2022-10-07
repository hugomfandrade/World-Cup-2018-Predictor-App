package org.hugoandrade.worldcup2018.predictor.backend.league;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUser.Entry.Cols.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(ID) private String mID;
    @JsonProperty(LEAGUE_ID) private String mLeagueID;
    @JsonProperty(USER_ID) private String mUserID;
    @JsonProperty(RANK) private int mRank;

    public LeagueUser() { }

    public LeagueUser(String leagueID, String userID, int rank) {
        this.mLeagueID = leagueID;
        this.mUserID = userID;
        this.mRank = rank;
    }

    public LeagueUser(String userID, int rank) {
        mUserID = userID;
        mRank = rank;
    }

    public LeagueUser(String userID) {
        mUserID = userID;
    }

    public String getID() {
        return mID;
    }

    public String getUserID() {
        return mUserID;
    }

    public int getRank() {
        return mRank;
    }

    public String getLeagueID() {
        return mLeagueID;
    }

    public static class Entry {

        public static final String TABLE_NAME = "LeagueUser";

        public static class Cols {
            public static final String ID = "id";
            public static final String RANK = "Rank";
            public static final String USER_ID = "UserID";
            public static final String LEAGUE_ID = "LeagueID";
        }
        public static final String USER_ID = "UserID";
    }
}
