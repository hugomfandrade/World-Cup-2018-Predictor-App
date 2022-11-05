package org.hugoandrade.worldcup2018.predictor.utils;

import android.content.Context;

import org.hugoandrade.worldcup2018.predictor.R;

import java.util.Locale;

public final class ErrorMessageUtils {

    /**
     * Ensure this class is only used as a utility.
     */
    private ErrorMessageUtils() {
        throw new AssertionError();
    }

    private static final String sTryAgainMessage = "Try again";
    private static final String sTryAgainMessagePT = "Tente novamente";

    private static final String sLoginIncorrectUsername = "Incorrect username";
    private static final String sLoginIncorrectPassword = "Incorrect password";

    public static String handleLoginErrorMessage(Context context, String message) {

        if (context == null || message == null) {
            return tryAgainMessage();
        }

        if (NetworkAppUtils.isNetworkUnavailableError(context, message))
            return message;

        if (message.contains(sLoginIncorrectUsername))
            return context.getString(R.string.error_username_not_exists);
        if (message.contains(sLoginIncorrectPassword))
            return context.getString(R.string.error_password_wrong);

        return context.getString(R.string.error_login_failed);

    }

    private static final String sRegisterUsernameExists = "Username already exists";

    public static String handleRegisterErrorMessage(Context context, String message) {

        if (context == null || message == null) {
            return tryAgainMessage();
        }

        if (NetworkAppUtils.isNetworkUnavailableError(context, message))
            return message;

        if (message.contains(sRegisterUsernameExists))
            return context.getString(R.string.error_username_exists);

        return context.getString(R.string.error_login_failed);
    }

    private static final String sJoinLeagueCodeErrorInit = "League with code";
    private static final String sJoinLeagueCodeErrorEnd = "does not exist";
    private static final String sJoinLeagueAlreadyMemberError = "You are already a member of this league";
    private static final String sJoinLeagueTooManyErrorInit = "You are a member";
    private static final String sJoinLeagueTooManyErrorEnd = "different leagues";

    public static String handleJoinLeagueErrorMessage(Context context, String message) {

        if (context == null || message == null) {
            return tryAgainMessage();
        }

        if (NetworkAppUtils.isNetworkUnavailableError(context, message))
            return message;

        if (message.contains(sJoinLeagueCodeErrorInit) && message.contains(sJoinLeagueCodeErrorEnd))
            return context.getString(R.string.error_league_code);
        if (message.contains(sJoinLeagueAlreadyMemberError))
            return context.getString(R.string.error_league_already_member);
        if (message.contains(sJoinLeagueTooManyErrorInit) && message.contains(sJoinLeagueTooManyErrorEnd))
            return context.getString(R.string.error_too_many_leagues);

        return context.getString(R.string.error_login_failed);
    }

    public static String handleErrorMessage(Context context, String message) {

        if (context == null || message == null) {
            return tryAgainMessage();
        }

        if (NetworkAppUtils.isNetworkUnavailableError(context, message))
            return message;

        return context.getString(R.string.error_login_failed);
    }

    private static String tryAgainMessage() {
        if (Locale.getDefault().getLanguage().equals("pt")) {
            return sTryAgainMessagePT;
        }
        else {
            return sTryAgainMessage;
        }
    }


    private static final String sNotBoundErrorMessage = "Not bound to the service";

    public static String genNotBoundMessage() {
        return sNotBoundErrorMessage;
    }

    private static final String sErrorSendingMessage = "Error sending message";

    public static String genErrorSendingMessage() {
        return sErrorSendingMessage;
    }


    private static final String sMustBeLoggedInError = "You must be logged in to use this application";
    private static final String sErrorValidatingAccessToken = "Error validating access token";
    private static final String sErrorPredictionPastDate = "Cannot insert prediction for that Match. Match date is past";

    public static String genMustBeLoggedInError() {
        return sMustBeLoggedInError;
    }
    public static String genErrorValidatingAccessTokenError() {
        return sErrorValidatingAccessToken;
    }
    public static boolean isErrorValidatingAccessTokenError(String message) {
        return sErrorValidatingAccessToken.equals(message);
    }
    public static boolean isErrorPredictionPastDateError(String message) {
        return sErrorPredictionPastDate.equals(message);
    }
}
