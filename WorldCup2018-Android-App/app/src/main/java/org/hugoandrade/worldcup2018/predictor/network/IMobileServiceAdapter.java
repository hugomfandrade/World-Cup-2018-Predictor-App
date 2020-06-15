package org.hugoandrade.worldcup2018.predictor.network;

import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import org.hugoandrade.worldcup2018.predictor.data.LoginData;
import org.hugoandrade.worldcup2018.predictor.data.Prediction;

public interface IMobileServiceAdapter {

    void setMobileServiceUser(MobileServiceUser mobileServiceUser);

    MobileServiceCallback logOut();

    MobileServiceCallback login(final LoginData loginData);

    MobileServiceCallback signUp(final LoginData loginData);

    MobileServiceCallback getSystemData();

    MobileServiceCallback getMatches();

    MobileServiceCallback getCountries();

    MobileServiceCallback getPredictions(String userID);

    MobileServiceCallback getPredictions(String userID, int firstMatchNumber, int lastMatchNumber);

    MobileServiceCallback getPredictions(String[] users, int firstMatchNumber, int lastMatchNumber);

    MobileServiceCallback getPredictions(String[] users, int matchNumber);

    MobileServiceCallback insertPrediction(final Prediction prediction);

    MobileServiceCallback getLeagues(final String userID);

    MobileServiceCallback createLeague(String userID, String leagueName);

    MobileServiceCallback joinLeague(String userID, String leagueCode);

    MobileServiceCallback deleteLeague(final String userID, String leagueID);

    MobileServiceCallback leaveLeague(final String userID, String leagueID);

    MobileServiceCallback fetchMoreUsers(final String leagueID, int skip, int top);

    MobileServiceCallback fetchMoreUsers(final String leagueID, int skip, int top,
                                         int minMatchNumber, int maxMatchNumber);

    MobileServiceCallback fetchUsers(final String leagueID, final String userID, int skip, int top,
                                     final int minMatchNumber, final int maxMatchNumber);

    MobileServiceCallback fetchRankOfUser(final String leagueID, String userID);

    MobileServiceCallback fetchRankOfUser(final String leagueID, String userID,
                                          int minMatchNumber, int maxMatchNumber);
}
