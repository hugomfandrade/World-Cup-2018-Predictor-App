package org.hugoandrade.worldcup2018.predictor.model.parser;

import com.google.gson.JsonObject;

import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.League;
import org.hugoandrade.worldcup2018.predictor.data.LoginData;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.Prediction;
import org.hugoandrade.worldcup2018.predictor.data.SystemData;
import org.hugoandrade.worldcup2018.predictor.network.JsonObjectBuilder;
import org.hugoandrade.worldcup2018.predictor.utils.ISO8601;

/**
 * Parses the objects to Json data.
 */
public class MobileClientDataJsonFormatter {

    public JsonObject getAsJsonObject(Prediction prediction, String... exceptProperties) {

        return JsonObjectBuilder.instance()
                .addProperty(Prediction.Entry.Cols.ID, prediction.getID())
                .addProperty(Prediction.Entry.Cols.USER_ID, prediction.getUserID())
                .addProperty(Prediction.Entry.Cols.MATCH_NO, prediction.getMatchNumber() == -1? null: prediction.getMatchNumber())
                .addProperty(Prediction.Entry.Cols.HOME_TEAM_GOALS, prediction.getHomeTeamGoals() == -1? null: prediction.getHomeTeamGoals())
                .addProperty(Prediction.Entry.Cols.AWAY_TEAM_GOALS, prediction.getAwayTeamGoals() == -1? null: prediction.getAwayTeamGoals())
                .addProperty(Prediction.Entry.Cols.SCORE, prediction.getScore() == -1? null : prediction.getScore())
                .removeProperties(exceptProperties)
                .create();
    }

    public JsonObject getAsJsonObject(LoginData loginData, String... exceptProperties) {

        return JsonObjectBuilder.instance()
                .addProperty(LoginData.Entry.Cols.USERNAME, loginData.getUsername())
                .addProperty(LoginData.Entry.Cols.PASSWORD, loginData.getPassword())
                .removeProperties(exceptProperties)
                .create();
    }

    public JsonObject getAsJsonObject(League league, String... exceptProperties) {

        return JsonObjectBuilder.instance()
                .addProperty(League.Entry.Cols.NAME, league.getName())
                .addProperty(League.Entry.Cols.ADMIN_ID, league.getAdminID())
                .addProperty(League.Entry.Cols.CODE, league.getCode())
                .removeProperties(exceptProperties)
                .create();
    }

    public JsonObject getAsJsonObject(Country country, String... exceptProperties) {

        return JsonObjectBuilder.instance()
                .addProperty(Country.Entry.Cols.ID, country.getID())
                .addProperty(Country.Entry.Cols.NAME, country.getName())
                .addProperty(Country.Entry.Cols.MATCHES_PLAYED, country.getMatchesPlayed())
                .addProperty(Country.Entry.Cols.VICTORIES, country.getVictories())
                .addProperty(Country.Entry.Cols.DRAWS, country.getDraws())
                .addProperty(Country.Entry.Cols.DEFEATS, country.getDefeats())
                .addProperty(Country.Entry.Cols.GOALS_FOR, country.getGoalsFor())
                .addProperty(Country.Entry.Cols.GOALS_AGAINST, country.getGoalsAgainst())
                .addProperty(Country.Entry.Cols.GOALS_DIFFERENCE, country.getGoalsDifference())
                .addProperty(Country.Entry.Cols.GROUP, country.getGroup())
                .addProperty(Country.Entry.Cols.POSITION, country.getPosition())
                .addProperty(Country.Entry.Cols.POINTS, country.getPoints())
                .addProperty(Country.Entry.Cols.FAIR_PLAY_POINTS, country.getFairPlayPoints())
                .addProperty(Country.Entry.Cols.DRAWING_OF_LOTS, country.getDrawingOfLots())
                .removeProperties(exceptProperties)
                .create();
    }

    public JsonObject getAsJsonObject(SystemData systemData, String... exceptProperties) {

        return JsonObjectBuilder.instance()
                .addProperty(SystemData.Entry.Cols.ID, systemData.getID())
                .addProperty(SystemData.Entry.Cols.APP_STATE, systemData.getAppState())
                .addProperty(SystemData.Entry.Cols.RULES, systemData.getRawRules())
                .addProperty(SystemData.Entry.Cols.SYSTEM_DATE, ISO8601.fromCalendar(systemData.getSystemDate()))
                .removeProperties(exceptProperties)
                .create();
    }

    public JsonObject getAsJsonObject(Match match, String... exceptProperties) {

        return JsonObjectBuilder.instance()
                .addProperty(Match.Entry.Cols.ID, match.getID())
                .addProperty(Match.Entry.Cols.MATCH_NUMBER, match.getMatchNumber())
                .addProperty(Match.Entry.Cols.HOME_TEAM_ID, match.getHomeTeamID())
                .addProperty(Match.Entry.Cols.AWAY_TEAM_ID, match.getAwayTeamID())
                .addProperty(Match.Entry.Cols.HOME_TEAM_GOALS, match.getHomeTeamGoals() != -1? match.getHomeTeamGoals() : null)
                .addProperty(Match.Entry.Cols.AWAY_TEAM_GOALS, match.getAwayTeamGoals() != -1? match.getAwayTeamGoals() : null)
                .addProperty(Match.Entry.Cols.HOME_TEAM_NOTES, match.getHomeTeamNotes())
                .addProperty(Match.Entry.Cols.AWAY_TEAM_NOTES, match.getAwayTeamNotes())
                .addProperty(Match.Entry.Cols.GROUP, match.getGroup())
                .addProperty(Match.Entry.Cols.STAGE, match.getStage())
                .addProperty(Match.Entry.Cols.STADIUM, match.getStadium())
                .addProperty(Match.Entry.Cols.DATE_AND_TIME, ISO8601.fromDate(match.getDateAndTime()))
                .removeProperties(exceptProperties)
                .create();
    }

    public JsonObject getAsJsonObject(LoginData loginData) {

        return JsonObjectBuilder.instance()
                .addProperty(LoginData.Entry.Cols.USERNAME, loginData.getUsername())
                .addProperty(LoginData.Entry.Cols.PASSWORD, loginData.getPassword())
                .create();
    }

    public JsonObjectBuilder build() {
        return JsonObjectBuilder.instance();
    }
}
