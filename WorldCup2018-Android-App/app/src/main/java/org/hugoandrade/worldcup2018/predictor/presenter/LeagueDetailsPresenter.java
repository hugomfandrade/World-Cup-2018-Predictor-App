package org.hugoandrade.worldcup2018.predictor.presenter;

import android.app.Activity;
import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.worldcup2018.predictor.GlobalData;
import org.hugoandrade.worldcup2018.predictor.MVP;
import org.hugoandrade.worldcup2018.predictor.data.LeagueWrapper;
import org.hugoandrade.worldcup2018.predictor.data.raw.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.data.raw.Prediction;
import org.hugoandrade.worldcup2018.predictor.data.raw.User;
import org.hugoandrade.worldcup2018.predictor.model.parser.MobileClientData;
import org.hugoandrade.worldcup2018.predictor.utils.ErrorMessageUtils;
import org.hugoandrade.worldcup2018.predictor.utils.MatchUtils;
import org.hugoandrade.worldcup2018.predictor.utils.SharedPreferencesUtils;
import org.hugoandrade.worldcup2018.predictor.view.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class LeagueDetailsPresenter extends MobileClientPresenterBase<MVP.RequiredLeagueDetailsViewOps>

        implements MVP.ProvidedLeagueDetailsPresenterOps {

    @Override
    public void onCreate(MVP.RequiredLeagueDetailsViewOps view) {

        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this MVP.RequiredModelOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {

    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.REQUEST_RESULT_SUCCESS;

        if (operationType == MobileClientData.OperationType.DELETE_LEAGUE.ordinal()) {
            onLeagueDeleted(isOperationSuccessful,
                    data.getErrorMessage());
        }
        else if (operationType == MobileClientData.OperationType.LEAVE_LEAGUE.ordinal()) {
            onLeagueLeft(isOperationSuccessful,
                    data.getErrorMessage());
        }
        else if (operationType == MobileClientData.OperationType.GET_PREDICTIONS.ordinal()) {
            onPredictionsFetched(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getUser(),
                    data.getPredictionList());
        }
        else if (operationType == MobileClientData.OperationType.FETCH_MORE_USERS.ordinal()) {
            onUsersFetched(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getString(),
                    data.getLeagueUserList());
        }
        else if (operationType == MobileClientData.OperationType.FETCH_USERS_BY_STAGE.ordinal()) {
            onLeagueByStageFetched(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getInteger(),
                    data.getLeagueWrapper());
        }
        else if (operationType == MobileClientData.OperationType.FETCH_MORE_USERS_BY_STAGE.ordinal()) {
            onUsersByStageFetched(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getInteger(),
                    data.getString(),
                    data.getLeagueUserList());
        }
        else if (operationType == MobileClientData.OperationType.LOGOUT.ordinal()) {

            if (getActivityContext() != null
                    && getApplicationContext() instanceof Activity) {
                Activity activity = (Activity) getActivityContext();

                if (!activity.isDestroyed() && !activity.isFinishing()) {
                    SharedPreferencesUtils.resetLastAuthenticatedLoginData(activity);
                    activity.startActivity(LoginActivity.makeIntent(activity));
                    activity.finish();
                }
            }
        }
    }

    @Override
    public void fetchRemainingPredictions(User user) {

        int to = MatchUtils.getMatchNumberOfFirstNotPlayedMatch(
                GlobalData.getInstance().getMatchList(),
                GlobalData.getInstance().getServerTime().getTime());
        to = to == 0? 0 : to - 1;

        if (to == 0) {
            getView().startUserPredictionsActivity(user, new ArrayList<Prediction>());
        }
        else {
            List<Prediction> predictionList = GlobalData.getInstance().getPredictionsOfUser(user.getID());

            Log.e(TAG, predictionList.size() + " = " + to);
            if (predictionList.size() >= to) {
                getView().startUserPredictionsActivity(user, predictionList);
            }
            else {
                getPredictionsOfSelectedUser(user);
            }
        }

    }

    @Override
    public void fetchMoreUsers(String leagueID, int numberOfMembers) {

        if (getMobileClientService() == null) {
            onUsersFetched(false, ErrorMessageUtils.genNotBoundMessage(), leagueID, null);
            return;
        }

        try {
            getMobileClientService().fetchMoreUsers(leagueID, numberOfMembers, 10);

            getView().disableUI();
        } catch (RemoteException e) {
            e.printStackTrace();
            onUsersFetched(false, ErrorMessageUtils.genErrorSendingMessage(), leagueID, null);
        }
    }

    @Override
    public void fetchUsers(String leagueID, int stage, int minMatchNumber, int maxMatchNumber) {


        if (getMobileClientService() == null) {
            onUsersFetched(false, ErrorMessageUtils.genNotBoundMessage(), leagueID, null);
            return;
        }

        try {
            getMobileClientService().fetchUsersByStage(leagueID, GlobalData.getInstance().user.getID(),
                    0, 5, stage, minMatchNumber, maxMatchNumber);

            getView().disableUI();
        } catch (RemoteException e) {
            e.printStackTrace();
            onUsersFetched(false, ErrorMessageUtils.genErrorSendingMessage(), leagueID, null);
        }
    }

    @Override
    public void fetchMoreUsers(String leagueID, int numberOfMembers, int stage, int minMatchNumber, int maxMatchNumber) {


        if (getMobileClientService() == null) {
            onUsersFetched(false, ErrorMessageUtils.genNotBoundMessage(), leagueID, null);
            return;
        }

        try {
            getMobileClientService().fetchMoreUsersByStage(leagueID, numberOfMembers, 10, stage, minMatchNumber, maxMatchNumber);

            getView().disableUI();
        } catch (RemoteException e) {
            e.printStackTrace();
            onUsersFetched(false, ErrorMessageUtils.genErrorSendingMessage(), leagueID, null);
        }
    }

    @Override
    public void logout() {
        if (getMobileClientService() == null) {
            return;
        }

        try {
            getMobileClientService().logout();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteLeague(String userID, String leagueID) {

        if (getMobileClientService() == null) {
            onLeagueDeleted(false, ErrorMessageUtils.genNotBoundMessage());
            return;
        }

        try {
            getMobileClientService().deleteLeague(userID, leagueID);

            getView().disableUI();
        } catch (RemoteException e) {
            e.printStackTrace();
            onLeagueDeleted(false, ErrorMessageUtils.genErrorSendingMessage());
        }
    }

    @Override
    public void leaveLeague(String userID, String leagueID) {

        if (getMobileClientService() == null) {
            onLeagueLeft(false, ErrorMessageUtils.genNotBoundMessage());
            return;
        }

        try {
            getMobileClientService().leaveLeague(userID, leagueID);

            getView().disableUI();
        } catch (RemoteException e) {
            e.printStackTrace();
            onLeagueLeft(false, ErrorMessageUtils.genErrorSendingMessage());
        }
    }

    private void getPredictionsOfSelectedUser(User user) {

        if (getMobileClientService() == null) {
            onPredictionsFetched(false, ErrorMessageUtils.genNotBoundMessage(), user, null);
            return;
        }

        try {
            getMobileClientService().getPredictions(user);

            getView().disableUI();
        } catch (RemoteException e) {
            e.printStackTrace();
            onPredictionsFetched(false, ErrorMessageUtils.genErrorSendingMessage(), user, null);
        }
    }

    private void onPredictionsFetched(boolean isOk, String errorMessage, User user, List<Prediction> predictionList) {
        if (isOk) {

            int from = 1;
            int to = MatchUtils.getMatchNumberOfFirstNotPlayedMatch(
                    GlobalData.getInstance().getMatchList(),
                    GlobalData.getInstance().getServerTime().getTime());
            to = to == 0? 0 : to - 1;

            GlobalData.getInstance().setPredictionsOfUser(user, predictionList, from, to);

            getView().startUserPredictionsActivity(user, GlobalData.getInstance().getPredictionsOfUser(user.getID()));
        }
        else {
            getView().reportMessage(ErrorMessageUtils.handleErrorMessage(getActivityContext(), errorMessage));
        }

        getView().enableUI();
    }

    private void onLeagueDeleted(boolean isOk, String errorMessage) {
        if (isOk) {
            getView().leagueLeft();
        }
        else {
            getView().reportMessage(ErrorMessageUtils.handleErrorMessage(getActivityContext(), errorMessage));
        }

        getView().enableUI();
    }

    private void onLeagueLeft(boolean isOk, String errorMessage) {
        if (isOk) {
            getView().leagueLeft();
        }
        else {
            getView().reportMessage(ErrorMessageUtils.handleErrorMessage(getActivityContext(), errorMessage));
        }

        getView().enableUI();
    }

    private void onUsersFetched(boolean isOk, String errorMessage, String leagueID, List<LeagueUser> userList) {
        if (isOk) {
            GlobalData.getInstance().addUsersToLeague(leagueID, userList);

            getView().updateListOfUsers(userList);
            //getView().leagueLeft();
        }
        else {
            getView().reportMessage(ErrorMessageUtils.handleErrorMessage(getActivityContext(), errorMessage));
        }

        getView().enableUI();
    }

    private void onLeagueByStageFetched(boolean isOk, String errorMessage, int stage, LeagueWrapper leagueWrapper) {
        if (isOk) {
            GlobalData.getInstance().setLeagueWrapperByStage(leagueWrapper, stage);

            getView().updateListOfUsersByStage(stage);
            // TODO getView().updateListOfUsers(userList);
            //getView().leagueLeft();
        }
        else {
            getView().reportMessage(ErrorMessageUtils.handleErrorMessage(getActivityContext(), errorMessage));
        }

        getView().enableUI();
    }

    private void onUsersByStageFetched(boolean isOk, String errorMessage, int stage, String leagueID, List<LeagueUser> userList) {
        if (isOk) {
            GlobalData.getInstance().addUsersToLeagueByStage(leagueID, userList, stage);

            getView().updateListOfUsersByStage(stage);
            // TODO getView().updateListOfUsers(userList);
            //getView().leagueLeft();
        }
        else {
            getView().reportMessage(ErrorMessageUtils.handleErrorMessage(getActivityContext(), errorMessage));
        }

        getView().enableUI();
    }

}
