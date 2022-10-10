package org.hugoandrade.worldcup2018.predictor.backend.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

import static org.hugoandrade.worldcup2018.predictor.backend.system.SystemData.Entry.Cols.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(ID) private String mID;
    @JsonProperty(RULES) private String mRules;
    @JsonProperty(APP_STATE) private boolean mAppState;
    @JsonProperty(SYSTEM_DATE) private Date mSystemDate;
    @JsonProperty(DATE_OF_CHANGE) private Date mDateOfChange;

    public SystemData() { }

    public SystemData(String rules, boolean appState, Date systemDate) {
        this(null, rules, appState, systemDate, new Date());
    }

    public SystemData(String id, String rules, boolean appState, Date systemDate) {
        this(id, rules, appState, systemDate, new Date());
    }

    public SystemData(String id, String rules, boolean appState, Date systemDate, Date dateOfChange) {
        mID = id;
        mRules = rules;
        mAppState = appState;
        mSystemDate = systemDate;
        mDateOfChange = dateOfChange;
    }

    public String getID() {
        return mID;
    }

    public String getRawRules() {
        return mRules;
    }

    public Date getSystemDate() {
        return mSystemDate;
    }

    public void add(long time) {
        mSystemDate.setTime(mSystemDate.getTime() + time);
    }

    public Date getDateOfChange() {
        return mDateOfChange;
    }

    public void setDateOfChange(Date dateOfChange) {
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

    public boolean getAppState() {
        return mAppState;
    }

    public void setSystemDate(Date systemDate) {
        mSystemDate = systemDate;
        mDateOfChange = new Date();
    }

    /*
    public void setSystemDate(int year, int month, int day) {
        mSystemDate.setHours().set(year, month, day);
    }

    public void setSystemDate(int field, int val) {
        mSystemDate.set(field, val);
    }
    */

    public Date getDate() {
        Date d = new Date();
        long diff = d.getTime() - mDateOfChange.getTime();
        d.setTime(mSystemDate.getTime() + diff);
        return d;
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
                ", mDateOfChange=" + mDateOfChange +
                '}';
    }

    public static class Rules {

        private int mRuleCorrectPrediction;
        private int mRuleCorrectMarginOfVictory;
        private int mRuleCorrectOutcome;
        private int mRuleIncorrectPrediction;

        public Rules() {}

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

    public static class Entry {

        public static final String API_NAME = "SystemData";
        public static final String API_NAME_UPDATE_SCORES = "UpdateScoresOfPredictions";

        public static class Cols {
            public static final String ID = "id";
            public static final String RULES = "Rules";
            public static final String APP_STATE = "AppState";
            public static final String SYSTEM_DATE = "SystemDate";

            public static final String DATE_OF_CHANGE = "DateOfChange";
        }
    }
}
