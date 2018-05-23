// IMobileClientService.aidl
package org.hugoandrade.worldcup2018.predictor.model;

// Declare any non-default types here with import statements
import org.hugoandrade.worldcup2018.predictor.model.IMobileClientServiceCallback;
import org.hugoandrade.worldcup2018.predictor.data.raw.Prediction;
import org.hugoandrade.worldcup2018.predictor.data.raw.LoginData;
import org.hugoandrade.worldcup2018.predictor.data.raw.User;
import org.hugoandrade.worldcup2018.predictor.data.raw.League;

interface IMobileClientService {

    void registerCallback(IMobileClientServiceCallback cb);

    void unregisterCallback(IMobileClientServiceCallback cb);

    void getSystemData();

    void logout();

    void login(in LoginData loginData);

    void signUp(in LoginData loginData);

    void getInfo(String userID);

    void putPrediction(in Prediction prediction);

    void getPredictions(in User user);

    void getPredictionsOfUsers(in List<User> userList, int matchNumber);

    void createLeague(in String userID, in String leagueName);

    void joinLeague(in String userID, in String leagueCode);

    void leaveLeague(in String userID, in String leagueID);

    void deleteLeague(in String userID, in String leagueID);

    void fetchMoreUsers(in String leagueID, int skip, int top);

    void fetchUsersByStage(in String leagueID, String userID, int skip, int top, int stage, int minMatchNumber, int maxMatchNumber);

    void fetchMoreUsersByStage(in String leagueID, int skip, int top, int stage, int minMatchNumber, int maxMatchNumber);
}
