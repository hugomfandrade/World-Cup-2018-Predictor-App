package org.hugoandrade.worldcup2018.predictor.presenter;

import android.app.Activity;
import android.os.RemoteException;

import org.hugoandrade.worldcup2018.predictor.GlobalData;
import org.hugoandrade.worldcup2018.predictor.MVP;
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

public class MatchPredictionPresenter extends MobileClientPresenterBase<MVP.RequiredMatchPredictionViewOps>

        implements MVP.ProvidedMatchPredictionPresenterOps {

    @Override
    public void onCreate(MVP.RequiredMatchPredictionViewOps view) {

        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this MVP.RequiredModelOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {

        int currentMatchNumber = MatchUtils.getMatchNumberOfFirstNotPlayedMatch(
                GlobalData.getInstance().getMatchList(),
                GlobalData.getInstance().getServerTime().getTime()) - 1;

        getPredictions(getView().getUserList(), currentMatchNumber);
    }

    @Override
    public void getPredictions(List<LeagueUser> userList, int matchNumber) {

        // filter users whose predictions need to be fetched
        List<User> uList = new ArrayList<>();
        for (LeagueUser user : userList) {
            if (!GlobalData.getInstance().wasPredictionFetched(user.getUser(), matchNumber)) {
                uList.add(user.getUser());
            }
        }

        if (uList.size() == 0) {
            List<User> t = new ArrayList<>();
            for (LeagueUser u : userList) {
                t.add(u.getUser());
            }
            getView().setMatchPredictionList(matchNumber, t);
            getView().enableUI();
            return;
        }

        if (getMobileClientService() == null) {
            onGettingPredictionsOperationFailedResult(ErrorMessageUtils.genNotBoundMessage());
            return;
        }

        try {
            getMobileClientService().getPredictionsOfUsers(uList, matchNumber);

            getView().disableUI();

        } catch (RemoteException e) {
            e.printStackTrace();
            onGettingPredictionsOperationFailedResult(ErrorMessageUtils.genErrorSendingMessage());
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

    private void onGettingPredictionsOperationFailedResult(String message) {
        onGettingPredictionsOperationResult(false, message, 0, null, null);
    }

    private void onGettingPredictionsOperationResult(boolean wasOperationSuccessful,
                                                     String message,
                                                     int matchNumber,
                                                     List<User> userList,
                                                     List<Prediction> predictionList) {
        getView().enableUI();

        if (wasOperationSuccessful) {

            GlobalData.getInstance().setPredictionsOfUsers(matchNumber, userList, predictionList);

            getView().setMatchPredictionList(matchNumber, userList);
        }
        else {
            getView().reportMessage(ErrorMessageUtils.handleErrorMessage(getActivityContext(), message));
        }
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

        if (operationType == MobileClientData.OperationType.GET_PREDICTIONS_OF_USERS.ordinal()) {
            onGettingPredictionsOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getInteger(),
                    data.getUserList(),
                    data.getPredictionList());
        }
        else if (operationType == MobileClientData.OperationType.LOGOUT.ordinal()) {

            if (getActivityContext() != null && getApplicationContext() instanceof Activity) {
                Activity activity = (Activity) getActivityContext();

                if (!activity.isDestroyed() && !activity.isFinishing()) {
                    SharedPreferencesUtils.resetLastAuthenticatedLoginData(activity);
                    activity.startActivity(LoginActivity.makeIntent(activity));
                    activity.finish();
                }
            }
        }
    }
}
