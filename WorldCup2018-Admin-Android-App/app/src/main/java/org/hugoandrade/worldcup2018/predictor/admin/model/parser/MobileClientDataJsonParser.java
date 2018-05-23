package org.hugoandrade.worldcup2018.predictor.admin.model.parser;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.hugoandrade.worldcup2018.predictor.admin.data.Country;
import org.hugoandrade.worldcup2018.predictor.admin.data.LoginData;
import org.hugoandrade.worldcup2018.predictor.admin.data.Match;
import org.hugoandrade.worldcup2018.predictor.admin.data.SystemData;
import org.hugoandrade.worldcup2018.predictor.admin.utils.ISO8601;

import java.util.ArrayList;

/**
 * Parses the Json data returned from the Mobile Service Client API
 * and returns the objects that contain this data.
 */
public class MobileClientDataJsonParser {

    /**
     * Used for logging purposes.
     */
    private final String TAG =
            getClass().getSimpleName();

    public SystemData parseSystemData(JsonObject jsonObject) {

        return new SystemData(
                getJsonPrimitive(jsonObject, SystemData.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, SystemData.Entry.Cols.RULES, null),
                getJsonPrimitive(jsonObject, SystemData.Entry.Cols.APP_STATE, false),
                ISO8601.toCalendar(getJsonPrimitive(jsonObject, SystemData.Entry.Cols.SYSTEM_DATE, null)));


    }

    public ArrayList<Country> parseCountryList(JsonElement result) {

        ArrayList<Country> allCountryList = new ArrayList<>();

        for (JsonElement item : result.getAsJsonArray()) {
            try {
                allCountryList.add(parseCountry(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing Country" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return allCountryList;
    }

    public Country parseCountry(JsonObject jsonObject) {

        return new Country(
                getJsonPrimitive(jsonObject, Country.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.NAME, null),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.MATCHES_PLAYED, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.VICTORIES, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.DRAWS, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.DEFEATS, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.GOALS_FOR, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.GOALS_AGAINST, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.GOALS_DIFFERENCE, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.GROUP, null),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.POINTS, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.POSITION, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.FAIR_PLAY_POINTS, 0),
                getJsonPrimitive(jsonObject, Country.Entry.Cols.DRAWING_OF_LOTS, 0));
    }

    public ArrayList<Match> parseMatchList(JsonElement result) {

        ArrayList<Match> allMatchList = new ArrayList<>();

        for (JsonElement item : result.getAsJsonArray()) {
            try {
                allMatchList.add(parseMatch(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing Match" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return allMatchList;
    }

    public LoginData parseLoginData(JsonObject jsonObject) {
        return new LoginData(
                getJsonPrimitive(jsonObject, LoginData.Entry.Cols.USER_ID, null),
                getJsonPrimitive(jsonObject, LoginData.Entry.Cols.USERNAME, null),
                getJsonPrimitive(jsonObject, LoginData.Entry.Cols.PASSWORD, null),
                getJsonPrimitive(jsonObject, LoginData.Entry.Cols.TOKEN, null)
        );
    }

    public Match parseMatch(JsonObject jsonObject) {

        return new Match(
                getJsonPrimitive(jsonObject, Match.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.MATCH_NUMBER, 0),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.HOME_TEAM_ID, null),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.AWAY_TEAM_ID, null),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.HOME_TEAM_GOALS, -1),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.AWAY_TEAM_GOALS, -1),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.HOME_TEAM_NOTES, null),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.AWAY_TEAM_NOTES, null),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.GROUP, null),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.STAGE, null),
                getJsonPrimitive(jsonObject, Match.Entry.Cols.STADIUM, null),
                ISO8601.toDate(getJsonPrimitive(jsonObject, Match.Entry.Cols.DATE_AND_TIME, null))
        );
    }

    private static int getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, int defaultValue) {
        try {
            return (int) jsonObject.getAsJsonPrimitive(jsonMemberName).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static float getJsonPrimitive(JsonObject jsonObject, String jsonMemberName,
                                          @SuppressWarnings("SameParameterValue")  float defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String getJsonPrimitive(JsonObject jsonObject, String jsonMemberName,
                                           @SuppressWarnings("SameParameterValue") String defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsString();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static boolean getJsonPrimitive(JsonObject jsonObject, String jsonMemberName,
                                            @SuppressWarnings("SameParameterValue") boolean defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsBoolean();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
