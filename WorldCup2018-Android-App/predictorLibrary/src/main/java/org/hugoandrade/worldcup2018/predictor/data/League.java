package org.hugoandrade.worldcup2018.predictor.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class League implements Parcelable {

    @SerializedName(Entry.Cols.ID) private String mID;
    @SerializedName(Entry.Cols.NAME) private String mName;
    @SerializedName(Entry.Cols.ADMIN_ID) private String mAdminID;
    @SerializedName(Entry.Cols.CODE) private String mCode;
    @SerializedName(Entry.Cols.NUMBER_OF_MEMBERS) private int mNumberOfMembers;

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

    public String getCode() {
        return mCode;
    }

    public int getNumberOfMembers() {
        return mNumberOfMembers;
    }

    protected League(Parcel in) {
        mID = in.readString();
        mName = in.readString();
        mAdminID = in.readString();
        mCode = in.readString();
        mNumberOfMembers = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mName);
        dest.writeString(mAdminID);
        dest.writeString(mCode);
        dest.writeInt(mNumberOfMembers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<League> CREATOR = new Creator<League>() {
        @Override
        public League createFromParcel(Parcel in) {
            return new League(in);
        }

        @Override
        public League[] newArray(int size) {
            return new League[size];
        }
    };

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
