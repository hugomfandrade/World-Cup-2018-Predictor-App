package org.hugoandrade.worldcup2018.predictor.backend.league;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String mID;
    private String mLeagueID;
    private String mUserID;
    private int mRank;

    // @ManyToMany(fetch = FetchType.EAGER, mappedBy = "leagueUsers", cascade = { CascadeType.PERSIST })
    // public Set<League> mLeagues = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST })
    // @JoinColumn(name="mID", nullable=false)
    public League mLeague;

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

    public void setLeagueID(String leagueID) {
        this.mLeagueID = leagueID;
    }

    public void setLeague(League league) {
        this.mLeague = league;
        this.mLeagueID = league.getID();
    }

    public League getLeague() {
        return mLeague;
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
