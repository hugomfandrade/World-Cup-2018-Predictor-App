package org.hugoandrade.worldcup2018.predictor.backend.league;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
