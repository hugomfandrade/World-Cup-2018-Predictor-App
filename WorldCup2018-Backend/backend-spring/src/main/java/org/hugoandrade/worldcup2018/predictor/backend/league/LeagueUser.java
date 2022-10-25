package org.hugoandrade.worldcup2018.predictor.backend.league;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.springframework.data.annotation.Transient;

import javax.persistence.*;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueUser {

    @javax.persistence.Id                       // jpa
    @org.springframework.data.annotation.Id     // mongo
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String mID;
    private String mLeagueID;
    private String mUserID;
    private int mRank;

    @ManyToOne(fetch = EAGER, cascade = { PERSIST })    // jpa
    @Transient                                          // mongo
    public League mLeague;

    @ManyToOne(fetch = EAGER, cascade = { PERSIST })    // jpa
    @Transient                                          // mongo
    public Account mAccount;

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

    public void setRank(int rank) {
        this.mRank = rank;
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

    public void setAccount(Account account) {
        if (account == null) return;
        this.mAccount = account;
        this.mUserID = account.getId();
    }

    public Account getAccount() {
        return mAccount;
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
