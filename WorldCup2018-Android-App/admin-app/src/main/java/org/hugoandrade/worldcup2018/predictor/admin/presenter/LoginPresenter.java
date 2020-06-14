package org.hugoandrade.worldcup2018.predictor.admin.presenter;

import android.content.Context;
import android.util.Log;

import org.hugoandrade.worldcup2018.predictor.admin.GlobalData;
import org.hugoandrade.worldcup2018.predictor.admin.MVP;
import org.hugoandrade.worldcup2018.predictor.admin.model.LoginModel;
import org.hugoandrade.worldcup2018.predictor.data.LoginData;
import org.hugoandrade.worldcup2018.predictor.data.SystemData;
import org.hugoandrade.worldcup2018.predictor.presenter.PresenterBase;
import org.hugoandrade.worldcup2018.predictor.utils.SharedPreferencesUtils;

public class LoginPresenter
        extends PresenterBase<MVP.RequiredLoginViewOps,
                                      MVP.RequiredLoginPresenterOps,
                                      MVP.ProvidedLoginModelOps,
                                      LoginModel>

        implements MVP.ProvidedLoginPresenterOps,
                   MVP.RequiredLoginPresenterOps {

    @Override
    public void onCreate(MVP.RequiredLoginViewOps view) {

        super.onCreate(view, LoginModel.class, this);
    }

    @Override
    public void onConfigurationChange(MVP.RequiredLoginViewOps view) { }

    @Override
    public void onResume() { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        getModel().getSystemData();
    }

    @Override
    public void loginRequestResult(boolean isOk, String message, LoginData loginData) {
        if (isOk) {

            SharedPreferencesUtils.putLoginData(getActivityContext(), loginData);

            getView().successfulLogin();
        }
        else {
            // operation failed, show error message
            if (message != null)
                getView().reportMessage(message);
        }

        getView().enableUI();
    }

    @Override
    public void getSystemDataRequestResult(boolean isOk, String message, SystemData systemData) {
        if (isOk) {

            GlobalData.setSystemData(systemData);
        }
        else {
            // operation failed, show error message
            if (message != null)
                getView().reportMessage(message);
        }
    }

    @Override
    public void login(String username, String password) {
        if (username.equals("")) {
            Log.w(TAG, "Username not entered");
            getView().reportMessage("Empty Username field");
            return;
        }
        if (password.equals("")) {
            Log.w(TAG, "Password not entered");
            getView().reportMessage("Empty Username field");
            return;
        }


        if (getModel().login(username, password))
            getView().disableUI();

    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }

}
