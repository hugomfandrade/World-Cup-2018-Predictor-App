package org.hugoandrade.worldcup2018.predictor.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;

public class SystemData implements Parcelable {

    @SerializedName(Entry.Cols.ID) private String mID;
    @SerializedName(Entry.Cols.RULES) private String mRules;
    @SerializedName(Entry.Cols.APP_STATE) private boolean mAppState;
    @SerializedName(Entry.Cols.SYSTEM_DATE) private Calendar mSystemDate;
    private Calendar mDateOfChange;
    private Calendar mDateOfLastRecordedSystemDate;

    public static class Entry {

        public static final String API_NAME = "SystemData";
        public static final String API_NAME_UPDATE_SCORES = "UpdateScoresOfPredictions";

        public static class Cols {
            public static final String ID = "id";
            public static final String RULES = "Rules";
            public static final String APP_STATE = "AppState";
            public static final String SYSTEM_DATE = "SystemDate";
        }
    }

    public SystemData(String id, String rules, boolean appState, Calendar systemDate, Calendar dateOfChange) {
        mID = id;
        mRules = rules;
        mAppState = appState;
        mSystemDate = systemDate;
        mDateOfChange = dateOfChange;
    }

    public SystemData(String id, String rules, boolean appState, Calendar systemDate) {
        mID = id;
        mRules = rules;
        mAppState = appState;
        mSystemDate = systemDate;
        mDateOfLastRecordedSystemDate = Calendar.getInstance();
    }

    private SystemData(Parcel in) {
        mID = in.readString();
        mRules = in.readString();
        mAppState = in.readByte() != 0;
        mSystemDate = (Calendar) in.readSerializable();
        mDateOfChange = (Calendar) in.readSerializable();
        mDateOfLastRecordedSystemDate = (Calendar) in.readSerializable();
    }

    public String getID() {
        return mID;
    }

    public String getRawRules() {
        return mRules;
    }

    public Calendar getSystemDate() {
        return mSystemDate;
    }

    public void add(long time) {
        mSystemDate.setTimeInMillis(mSystemDate.getTimeInMillis() + time);
    }

    public Calendar getDateOfChange() {
        return mDateOfChange;
    }

    public void setDateOfChange(Calendar dateOfChange) {
        mDateOfChange = dateOfChange;
    }

    public void setAppState(boolean state) {
        mAppState = state;
    }

    public void setRules(Rules rules) {

        mRules = Integer.toString(rules.getRuleIncorrectPrediction()) + "," +
                Integer.toString(rules.getRuleCorrectOutcome()) + "," +
                Integer.toString(rules.getRuleCorrectMarginOfVictory()) + "," +
                Integer.toString(rules.getRuleCorrectPrediction());
    }

    public static final Creator<SystemData> CREATOR = new Creator<SystemData>() {
        @Override
        public SystemData createFromParcel(Parcel in) {
            return new SystemData(in);
        }

        @Override
        public SystemData[] newArray(int size) {
            return new SystemData[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mRules);
        dest.writeByte((byte) (mAppState ? 1 : 0));
        dest.writeSerializable(mSystemDate);
        dest.writeSerializable(mDateOfChange);
        dest.writeSerializable(mDateOfLastRecordedSystemDate);
    }

    public boolean getAppState() {
        return mAppState;
    }

    public void setSystemDate(Calendar systemDate) {
        mSystemDate = systemDate;
        mDateOfLastRecordedSystemDate = Calendar.getInstance();
    }

    public void setSystemDate(int year, int month, int day) {
        mSystemDate.set(year, month, day);
    }

    public void setSystemDate(int field, int val) {
        mSystemDate.set(field, val);
    }

    public Calendar getDate() {
        Calendar c = Calendar.getInstance();
        long diff = c.getTimeInMillis() - mDateOfLastRecordedSystemDate.getTimeInMillis();
        c.setTimeInMillis(mSystemDate.getTimeInMillis() + diff);
        return c;
    }

    public Rules getRules() {
        try {
            String[] s = mRules.split(",");
            return new Rules(Integer.parseInt(s[0]),
                    Integer.parseInt(s[1]),
                    Integer.parseInt(s[2]),
                    Integer.parseInt(s[3]));
        } catch (Exception e) {
            return new Rules(-1, -1, -1, -1);
        }
    }

    @Override
    public String toString() {
        return "SystemData{" +
                "mID='" + mID + '\'' +
                ", mRules='" + mRules + '\'' +
                ", mAppState=" + mAppState +
                ", mSystemDate=" + mSystemDate +
                ", mDateOfLastRecordedSystemDate=" + mDateOfLastRecordedSystemDate +
                '}';
    }

    public static class Rules {

        private final int mRuleCorrectPrediction;
        private final int mRuleCorrectMarginOfVictory;
        private final int mRuleCorrectOutcome;
        private final int mRuleIncorrectPrediction;

        public Rules(int incorrectPrediction,
                     int correctOutcome,
                     int correctMarginOfVictory,
                     int correctPrediction) {
            mRuleIncorrectPrediction = incorrectPrediction;
            mRuleCorrectOutcome = correctOutcome;
            mRuleCorrectMarginOfVictory = correctMarginOfVictory;
            mRuleCorrectPrediction = correctPrediction;
        }

        public int getRuleCorrectPrediction() {
            return mRuleCorrectPrediction;
        }

        public int getRuleCorrectOutcome() {
            return mRuleCorrectOutcome;
        }

        public int getRuleCorrectMarginOfVictory() {
            return mRuleCorrectMarginOfVictory;
        }

        public int getRuleIncorrectPrediction() {
            return mRuleIncorrectPrediction;
        }
    }
}
