package org.hugoandrade.worldcup2018.predictor.admin.data;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String mID;
    private String mUsername;
    private String mPassword;
    private int mScore;

    public static class Entry {

        public static final String TABLE_NAME = "Account";

        public static class Cols {
            public final static String ID = "id";
            public final static String USERNAME = "Username";
            public final static String PASSWORD = "Password";
            public final static String SCORE = "Score";
        }
    }

    public User(String id, String username, String password, int score) {
        mID = id;
        mUsername = username;
        mPassword = password;
        mScore = score;
    }

    public User(String id, String username, int score) {
        mID = id;
        mUsername = username;
        mScore = score;
    }

    public User(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public String getID() {
        return mID;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score) {
        mScore = score;
    }

    protected User(Parcel in) {
        mID = in.readString();
        mUsername = in.readString();
        mPassword = in.readString();
        mScore = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
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
        dest.writeString(mPassword);
        dest.writeInt(mScore);
    }
}
