package org.hugoandrade.worldcup2018.predictor.data.raw;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private final String mID;
    private final String mUsername;
    private final int mScore;

    public static class Entry {

        public static final String TABLE_NAME = "Account";

        public static class Cols {
            public final static String ID = "id";
            public final static String USERNAME = "Username";
            public final static String SCORE = "Score";
        }
    }

    public User(String id, String username, int score) {
        mID = id;
        mUsername = username;
        mScore = score;
    }

    public User(String id, String username) {
        this(id, username, 0);
    }

    public String getID() {
        return mID;
    }

    public String getUsername() {
        return mUsername;
    }

    public int getScore() {
        return mScore;
    }

    protected User(Parcel in) {
        mID = in.readString();
        mUsername = in.readString();
        mScore = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mUsername);
        dest.writeInt(mScore);
    }

    @Override
    public String toString() {
        return "User{" +
                "mID='" + mID + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mScore=" + mScore +
                '}';
    }
}
