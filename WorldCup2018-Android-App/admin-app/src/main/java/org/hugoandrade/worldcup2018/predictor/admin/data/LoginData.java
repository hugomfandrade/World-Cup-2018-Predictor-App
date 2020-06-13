package org.hugoandrade.worldcup2018.predictor.admin.data;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginData implements Parcelable {

    public static class Entry {

        public static final String API_NAME_LOGIN = "Login";

        public static class Cols {
            public final static String USER_ID = "UserID";
            public final static String USERNAME = "Username";
            public final static String PASSWORD = "Password";
            public final static String TOKEN = "Token";
        }
    }

    private String mUserID;
    private String mUsername;
    private String mPassword;
    private String mToken;

    public LoginData(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public LoginData(String userID, String username, String password, String token) {
        mUserID = userID;
        mUsername = username;
        mPassword = password;
        mToken = token;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUserID() {
        return mUserID;
    }

    public String getToken() {
        return mToken;
    }

    public LoginData(Parcel in) {
        mUserID = in.readString();
        mUsername = in.readString();
        mPassword = in.readString();
        mToken = in.readString();
    }

    public static final Creator<LoginData> CREATOR = new Creator<LoginData>() {
        @Override
        public LoginData createFromParcel(Parcel in) {
            return new LoginData(in);
        }

        @Override
        public LoginData[] newArray(int size) {
            return new LoginData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUserID);
        dest.writeString(mUsername);
        dest.writeString(mPassword);
        dest.writeString(mToken);
    }
}
