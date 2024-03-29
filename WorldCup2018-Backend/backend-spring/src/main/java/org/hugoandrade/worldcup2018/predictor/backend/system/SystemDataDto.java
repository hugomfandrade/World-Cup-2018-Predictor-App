package org.hugoandrade.worldcup2018.predictor.backend.system;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

import static org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataDto.Entry.Cols.*;

public class SystemDataDto {

    @JsonProperty(RULES) private String mRules;
    @JsonProperty(APP_STATE) private boolean mAppState;
    @JsonProperty(SYSTEM_DATE) private Date mSystemDate;

    public SystemDataDto() { }

    public SystemDataDto(String rules, boolean appState, Date systemDate) {
        mRules = rules;
        mAppState = appState;
        mSystemDate = systemDate;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemDataDto that = (SystemDataDto) o;
        return mAppState == that.mAppState
                && Objects.equals(mRules, that.mRules)
                && Objects.equals(mSystemDate, that.mSystemDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mRules, mAppState, mSystemDate);
    }

    public static class Entry {

        public static class Cols {
            public static final String RULES = "Rules";
            public static final String APP_STATE = "AppState";
            public static final String SYSTEM_DATE = "SystemDate";
        }
    }
}
