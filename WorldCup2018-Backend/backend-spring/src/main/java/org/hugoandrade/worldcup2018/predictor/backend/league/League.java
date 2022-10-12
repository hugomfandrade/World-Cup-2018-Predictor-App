package org.hugoandrade.worldcup2018.predictor.backend.league;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String mID;
    private String mName;
    private String mAdminID;
    private String mCode;
    private int mNumberOfMembers;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "mLeague", cascade = { CascadeType.ALL }, orphanRemoval = true)
    /* @ManyToMany(fetch = FetchType.EAGER, targetEntity = LeagueUser.class, cascade = { CascadeType.ALL })
    @JoinTable(
            name = "league_league_user",
            joinColumns = @JoinColumn(name = "mLeague_ID"),
            inverseJoinColumns = @JoinColumn(name = "mLeagueUser_ID"))*/
    private Set<LeagueUser> leagueUsers = new HashSet<>();

    public League() { }

    public League(String name, String adminID) {
        mName = name;
        mAdminID = adminID;
    }

    public League(String name) {
        mName = name;
    }

    public String getID() {
        return mID;
    }

    public void setID(String id) {
        this.mID = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAdminID() {
        return mAdminID;
    }

    public void setAdminID(String adminID) {
        this.mAdminID = adminID;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        this.mCode = code;
    }

    public int getNumberOfMembers() {
        return mNumberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.mNumberOfMembers = numberOfMembers;
    }

    public List<LeagueUser> getLeagueUsers() {
        return new ArrayList<>(leagueUsers);
    }

    public League addLeagueUser(LeagueUser leagueUser) {
        leagueUsers.add(leagueUser);
        return this;
    }

    public void removeLeagueUser(String userID) {
        leagueUsers.removeIf(leagueUser -> Objects.equals(userID, leagueUser.getUserID()));
    }

    @Override
    public String toString() {
        return "League{" +
                "mID='" + mID + '\'' +
                ", mName='" + mName + '\'' +
                ", mAdminID='" + mAdminID + '\'' +
                ", mCode='" + mCode + '\'' +
                ", mNumberOfMembers=" + mNumberOfMembers +
                '}';
    }
}
