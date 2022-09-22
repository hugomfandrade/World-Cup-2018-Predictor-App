package org.hugoandrade.worldcup2018.predictor.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static org.hugoandrade.worldcup2018.predictor.backend.model.League.Entry.Cols.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(ID) private String mID;
    @JsonProperty(NAME) private String mName;
    @JsonProperty(ADMIN_ID) private String mAdminID;
    @JsonProperty(CODE) private String mCode;
    @JsonProperty(NUMBER_OF_MEMBERS) private int mNumberOfMembers;

    public League() { }

    @Deprecated
    public League(String id, String name, String adminID, String code, int numberOfMembers) {
        mID = id;
        mName = name;
        mAdminID = adminID;
        mCode = code;
        mNumberOfMembers = numberOfMembers;
    }

    public League(String name, String adminID) {
        mName = name;
        mAdminID = adminID;
    }

    public String getID() {
        return mID;
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

    public static class Entry {

        public static final String TABLE_NAME = "League";
        public static final String API_NAME_CREATE_LEAGUE = "CreateLeague";
        public static final String API_NAME_JOIN_LEAGUE = "JoinLeague";
        public static final String API_NAME_LEAVE_LEAGUE = "LeaveLeague";
        public static final String API_NAME_DELETE_LEAGUE = "DeleteLeague";

        // parameters
        public static final String MIN_MATCH_NUMBER = "MinMatchNumber";
        public static final String MAX_MATCH_NUMBER = "MaxMatchNumber";
        public static final String USER_ID = "UserID";

        public static class Cols {
            public static final String ID = "id";
            public static final String NAME = "Name";
            public static final String ADMIN_ID = "AdminID";
            public static final String CODE = "Code";
            public static final String NUMBER_OF_MEMBERS = "NumberOfMembers";
        }
    }
}
