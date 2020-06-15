package org.hugoandrade.worldcup2018.predictor.backend.authentication;

public class LoginData {

    private String mUserID;
    private String mUsername;
    private String mPassword;
    private String mToken;

    public LoginData() { }

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

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setUserID(String userID) {
        this.mUserID = userID;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getToken() {
        return mToken;
    }
}
