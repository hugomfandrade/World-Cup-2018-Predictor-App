package org.hugoandrade.worldcup2018.predictor.presenter;

import android.os.RemoteException;

import org.hugoandrade.worldcup2018.predictor.MVP;
import org.hugoandrade.worldcup2018.predictor.data.raw.LoginData;
import org.hugoandrade.worldcup2018.predictor.model.parser.MobileClientData;
import org.hugoandrade.worldcup2018.predictor.utils.ErrorMessageUtils;

public class SignUpPresenter extends MobileClientPresenterBase<MVP.RequiredSignUpViewOps>

        implements MVP.ProvidedSignUpPresenterOps {


    @Override
    public void onCreate(MVP.RequiredSignUpViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the SignUpModel class to instantiate/manage and
        // "this" to provide SignUpModel with this MVP.RequiredModelOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        // No-ops
    }

    @Override
    public void registerUser(String username, String password, String confirmPassword) {

        if (getMobileClientService() == null) {
            signUpOperationResult(false, ErrorMessageUtils.genNotBoundMessage(), null);
            return;
        }

        try {
            getMobileClientService().signUp(new LoginData(username, password));

            getView().disableUI();
        } catch (RemoteException e) {
            e.printStackTrace();
            signUpOperationResult(false, ErrorMessageUtils.genErrorSendingMessage(), null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.REQUEST_RESULT_SUCCESS;

        if (operationType == MobileClientData.OperationType.REGISTER.ordinal()) {
            signUpOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getLoginData());
        }
    }

    private void signUpOperationResult(boolean wasOperationSuccessful, String message, LoginData loginData) {
        if (wasOperationSuccessful) {
            getView().successfulRegister(loginData);
        }
        else {
            getView().reportMessage(ErrorMessageUtils.handleRegisterErrorMessage(getActivityContext(), message));
        }

        getView().enableUI();
    }
}
