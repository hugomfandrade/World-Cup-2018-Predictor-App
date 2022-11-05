package org.hugoandrade.worldcup2018.predictor.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import org.hugoandrade.worldcup2018.predictor.DevConstants;
import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.LeagueWrapper;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.League;
import org.hugoandrade.worldcup2018.predictor.data.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.data.LoginData;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.Prediction;
import org.hugoandrade.worldcup2018.predictor.data.SystemData;
import org.hugoandrade.worldcup2018.predictor.data.User;
import org.hugoandrade.worldcup2018.predictor.model.helper.MobileServiceJsonTableHelper;
import org.hugoandrade.worldcup2018.predictor.model.parser.MobileClientDataJsonFormatter;
import org.hugoandrade.worldcup2018.predictor.model.parser.MobileClientDataJsonParser;
import org.hugoandrade.worldcup2018.predictor.utils.NetworkBroadcastReceiverUtils;
import org.hugoandrade.worldcup2018.predictor.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MobileServiceAdapter implements IMobileServiceAdapter, NetworkBroadcastReceiverUtils.INetworkBroadcastReceiver {

    @SuppressWarnings("unused")
    private static final String TAG = MobileServiceAdapter.class.getSimpleName();

    private static MobileServiceAdapter mInstance = null;

    private Context mContext;
    private MobileServiceClient mClient = null;

    private MobileClientDataJsonParser parser = new MobileClientDataJsonParser();
    private MobileClientDataJsonFormatter formatter = new MobileClientDataJsonFormatter();

    private BroadcastReceiver mNetworkBroadcastReceiver;

    private boolean mIsNetworkAvailable = false;

    public static MobileServiceAdapter getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("MobileServiceAdapter is not initialized");
        }
        return mInstance;
    }

    public static void Initialize(Context context) {
        if (mInstance == null) {
            mInstance = new MobileServiceAdapter(context);
        } else {
            throw new IllegalStateException("MobileServiceAdapter is already initialized");
        }
    }

    public static void unInitialize(Context context) {
        try {
            getInstance().destroy(context);
        } catch (IllegalStateException e) {
            Log.e(TAG, "unInitialize error: " + e.getMessage());
        }
    }

    private MobileServiceAdapter(Context context) {
        mContext = context;

        try {
            mClient = new MobileServiceClient(
                    DevConstants.appUrl,
                    null,
                    context);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mIsNetworkAvailable = NetworkUtils.isNetworkAvailable(context);
        mNetworkBroadcastReceiver = NetworkBroadcastReceiverUtils.register(context, this);
    }

    private void destroy(Context context) {
        if (mNetworkBroadcastReceiver != null) {
            NetworkBroadcastReceiverUtils.unregister(context, mNetworkBroadcastReceiver);
            mNetworkBroadcastReceiver = null;
        }
    }

    public void setMobileServiceUser(MobileServiceUser mobileServiceUser) {
        mClient.setCurrentUser(mobileServiceUser);
    }

    public MobileServiceUser getMobileServiceUser() {
        return mClient.getCurrentUser();
    }

    public MobileServiceCallback logOut() {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (mClient != null)
            mClient.logout();

        callback.set(MobileServiceData.Builder
                .instance(MobileServiceData.LOGOUT, MobileServiceData.REQUEST_RESULT_SUCCESS)
                .create());

        return callback;

    }

    public MobileServiceCallback login(final LoginData loginData) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.LOGIN))
            return callback;

        ListenableFuture<JsonElement> future =
                mClient.invokeApi(LoginData.Entry.API_NAME_LOGIN,
                        formatter.getAsJsonObject(loginData),
                        HttpConstants.PostMethod,
                        null);

        Futures.addCallback(future, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonObject) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.LOGIN, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLoginData(parser.parseLoginData(jsonObject.getAsJsonObject()))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                sendErrorMessage(callback, MobileServiceData.LOGIN, t.getMessage() + ": Login");
            }
        });
        return callback;
    }

    public MobileServiceCallback signUp(final LoginData loginData) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.SIGN_UP))
            return callback;

        ListenableFuture<JsonElement> future =
                mClient.invokeApi(LoginData.Entry.API_NAME_REGISTER,
                        formatter.getAsJsonObject(loginData),
                        HttpConstants.PostMethod,
                        null);

        Futures.addCallback(future, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonObject) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.SIGN_UP, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLoginData(parser.parseLoginData(jsonObject.getAsJsonObject()))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                sendErrorMessage(callback, MobileServiceData.SIGN_UP, t.getMessage());
            }
        });
        return callback;
    }

    public MobileServiceCallback getSystemData() {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.GET_SYSTEM_DATA))
            return callback;

        ListenableFuture<JsonElement> future =
                mClient.invokeApi(SystemData.Entry.API_NAME,
                        null,
                        HttpConstants.GetMethod,
                        null);

        Futures.addCallback(future, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonObject) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.GET_SYSTEM_DATA, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setSystemData(parser.parseSystemData(jsonObject.getAsJsonObject()))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                sendErrorMessage(callback, MobileServiceData.GET_SYSTEM_DATA, t.getMessage() + ": getSystemData");
            }
        });
        return callback;
    }

    public MobileServiceCallback getMatches() {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.GET_MATCHES))
            return callback;

        ListenableFuture<JsonElement> futureCountries = MobileServiceJsonTableHelper
                .instance(Match.Entry.TABLE_NAME, mClient)
                .orderBy(Match.Entry.Cols.MATCH_NUMBER, QueryOrder.Ascending)
                .execute();
        Futures.addCallback(futureCountries, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.GET_MATCHES, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setMatchList(parser.parseMatchList(jsonElement))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                sendErrorMessage(callback, MobileServiceData.GET_MATCHES, throwable.getMessage());
            }
        });

        return callback;
    }

    public MobileServiceCallback getCountries() {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.GET_COUNTRIES))
            return callback;

        ListenableFuture<JsonElement> futureCountries =  MobileServiceJsonTableHelper
                .instance(Country.Entry.TABLE_NAME, mClient)
                .execute();
        Futures.addCallback(futureCountries, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.GET_COUNTRIES, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setCountryList(parser.parseCountryList(jsonElement))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                sendErrorMessage(callback, MobileServiceData.GET_COUNTRIES, throwable.getMessage());
            }
        });

        return callback;
    }

    public MobileServiceCallback getPredictions(String userID) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.GET_PREDICTIONS))
            return callback;

        ListenableFuture<JsonElement> i = MobileServiceJsonTableHelper
                .instance(Prediction.Entry.TABLE_NAME, mClient)
                .where().field(Prediction.Entry.Cols.USER_ID).eq(userID)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {

                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.GET_PREDICTIONS, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setPredictionList(parser.parsePredictionList(jsonElement))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                sendErrorMessage(callback, MobileServiceData.GET_PREDICTIONS, throwable.getMessage());
            }
        });

        return callback;
    }

    public MobileServiceCallback getPredictions(String userID, int firstMatchNumber, int lastMatchNumber) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.GET_PREDICTIONS))
            return callback;

        ListenableFuture<JsonElement> i = new MobileServiceJsonTable(Prediction.Entry.TABLE_NAME, mClient)
                .where().field(Prediction.Entry.Cols.USER_ID).eq(userID)
                .and().field(Prediction.Entry.Cols.MATCH_NO).ge(firstMatchNumber)
                .and().field(Prediction.Entry.Cols.MATCH_NO).le(firstMatchNumber)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.GET_PREDICTIONS, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setPredictionList(parser.parsePredictionList(jsonElement))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                sendErrorMessage(callback, MobileServiceData.GET_PREDICTIONS, throwable.getMessage());
            }
        });

        return callback;
    }

    public MobileServiceCallback getPredictions(String[] users, int firstMatchNumber, int lastMatchNumber) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.GET_PREDICTIONS))
            return callback;

        ListenableFuture<JsonElement> i = MobileServiceJsonTableHelper.instance(Prediction.Entry.TABLE_NAME, mClient)
                .where().field(Prediction.Entry.Cols.USER_ID).eq(users)
                .and().field(Prediction.Entry.Cols.MATCH_NO).ge(firstMatchNumber)
                .and().field(Prediction.Entry.Cols.MATCH_NO).le(lastMatchNumber)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.GET_PREDICTIONS, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setPredictionList(parser.parsePredictionList(jsonElement))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                sendErrorMessage(callback, MobileServiceData.GET_PREDICTIONS, throwable.getMessage());
            }
        });

        return callback;
    }

    public MobileServiceCallback getPredictions(String[] users, int matchNumber) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.GET_PREDICTIONS))
            return callback;

        ListenableFuture<JsonElement> i = MobileServiceJsonTableHelper.instance(Prediction.Entry.TABLE_NAME, mClient)
                .where().field(Prediction.Entry.Cols.USER_ID).eq(users)
                .and().field(Prediction.Entry.Cols.MATCH_NO).eq(matchNumber)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.GET_PREDICTIONS, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setPredictionList(parser.parsePredictionList(jsonElement))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                sendErrorMessage(callback, MobileServiceData.GET_PREDICTIONS, throwable.getMessage());
            }
        });

        return callback;
    }

    public MobileServiceCallback insertPrediction(final Prediction prediction) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!mIsNetworkAvailable) {
            callback.set(buildNetworkFailureMessage(MobileServiceData.INSERT_PREDICTION)
                    .setPrediction(prediction)
                    .create());
            return callback;
        }

        ListenableFuture<JsonObject> future = new MobileServiceJsonTable(Prediction.Entry.TABLE_NAME, mClient)
                .insert(formatter.getAsJsonObject(prediction, Prediction.Entry.Cols.ID));
        Futures.addCallback(future, new FutureCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject jsonObject) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.INSERT_PREDICTION, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setPrediction(prediction)
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e(TAG, "data.getPrediction::" + prediction);
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.INSERT_PREDICTION, MobileServiceData.REQUEST_RESULT_FAILURE)
                        .setPrediction(prediction)
                        .setMessage(t.getMessage())
                        .create());
            }
        });
        return callback;
    }

    public MobileServiceCallback getLeagues(final String userID) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.GET_LEAGUES))
            return callback;

        ListenableFuture<JsonElement> i = MobileServiceJsonTableHelper
                .instance(League.Entry.TABLE_NAME, mClient)
                .where().field(LeagueUser.Entry.USER_ID).eq(userID)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {

            private List<LeagueWrapper> leagueWrapperList = new ArrayList<>();
            private List<League> leagueList = new ArrayList<>();

            @Override
            public void onSuccess(JsonElement jsonElement) {

                leagueList = parser.parseLeagueList(jsonElement);

                // Get Top 5 of each League
                tryOnFinished();

                final MultipleCloudStatus n = new MultipleCloudStatus(leagueList.size() * 2);
                final Object syncObj = new Object();

                for (League league : leagueList) {

                    final LeagueWrapper leagueWrapper = new LeagueWrapper(league);

                    MobileServiceCallback c = fetchMoreUsers(league.getID(), 0, 5);
                    MobileServiceCallback.addCallback(c, new MobileServiceCallback.OnResult() {
                        @Override
                        public void onResult(MobileServiceData data) {
                            synchronized (syncObj) {
                                Log.e(TAG, "iCountries finished");

                                if (n.isAborted()) return; // An error occurred
                                n.operationCompleted();

                                leagueWrapper.setLeagueUserList(data.getLeagueUserList());
                                leagueWrapperList.add(leagueWrapper);

                                if (n.isFinished())
                                    tryOnFinished();
                            }
                        }
                    });

                    MobileServiceCallback i = fetchRankOfUser(leagueWrapper.getLeague().getID(), userID);
                    MobileServiceCallback.addCallback(i, new MobileServiceCallback.OnResult() {
                        @Override
                        public void onResult(MobileServiceData data) {
                            synchronized (syncObj) {

                                if (n.isAborted()) return; // An error occurred
                                n.operationCompleted();

                                List<LeagueUser> leagueUserList = data.getLeagueUserList();
                                if (leagueUserList.size() == 1)
                                    leagueWrapper.setMainUser(leagueUserList.get(0));

                                if (n.isFinished())
                                    tryOnFinished();
                            }
                        }
                    });

                }
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                sendErrorMessage(callback, MobileServiceData.GET_LEAGUES, throwable.getMessage());
            }

            private void tryOnFinished() {

                if (leagueWrapperList.size() == leagueList.size()) {
                    callback.set(MobileServiceData.Builder
                            .instance(MobileServiceData.GET_LEAGUES, MobileServiceData.REQUEST_RESULT_SUCCESS)
                            .setLeagueWrapperList(leagueWrapperList)
                            .create());
                }
            }
        });

        return callback;
    }

    public MobileServiceCallback createLeague(String userID, String leagueName) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.CREATE_LEAGUE))
            return callback;

        ListenableFuture<JsonElement> future =
                mClient.invokeApi(League.Entry.API_NAME_CREATE_LEAGUE,
                        JsonObjectBuilder.instance()
                                .addProperty(League.Entry.Cols.NAME, leagueName)
                                .addProperty(League.Entry.Cols.ADMIN_ID, userID)
                                .create(),
                        HttpConstants.PostMethod,
                        null);

        Futures.addCallback(future, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonObject) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.CREATE_LEAGUE, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLeague(parser.parseLeague(jsonObject.getAsJsonObject()))
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                sendErrorMessage(callback, MobileServiceData.CREATE_LEAGUE, t.getMessage());
            }
        });
        return callback;
    }

    public MobileServiceCallback joinLeague(String userID, String leagueCode) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.JOIN_LEAGUE))
            return callback;

        ListenableFuture<JsonElement> future =
                mClient.invokeApi(League.Entry.API_NAME_JOIN_LEAGUE,
                        formatter.build()
                                .addProperty(League.Entry.USER_ID, userID)
                                .addProperty(League.Entry.Cols.CODE, leagueCode)
                                .create(),
                        HttpConstants.PostMethod,
                        null);

        Futures.addCallback(future, new FutureCallback<JsonElement>() {

            private LeagueWrapper leagueWrapper;

            @Override
            public void onSuccess(JsonElement jsonObject) {
                League league = parser.parseLeague(jsonObject.getAsJsonObject());

                leagueWrapper = new LeagueWrapper(league);

                MobileServiceCallback c = fetchMoreUsers(league.getID(), 0, 5);
                MobileServiceCallback.addCallback(c, new MobileServiceCallback.OnResult() {
                    @Override
                    public void onResult(MobileServiceData data) {
                        leagueWrapper.setLeagueUserList(data.getLeagueUserList());

                        tryOnFinished();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                sendErrorMessage(callback, MobileServiceData.JOIN_LEAGUE, t.getMessage());
            }

            private void tryOnFinished() {
                Log.e(TAG, "joinLeague(finally)::" + leagueWrapper.toString());

                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.JOIN_LEAGUE, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLeagueWrapper(leagueWrapper)
                        .create());
            }
        });
        return callback;
    }

    public MobileServiceCallback deleteLeague(final String userID, String leagueID) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.DELETE_LEAGUE))
            return callback;

        ListenableFuture<JsonElement> future =
                mClient.invokeApi(League.Entry.API_NAME_DELETE_LEAGUE,
                        formatter.build()
                                .addProperty(League.Entry.USER_ID, userID)
                                .addProperty(League.Entry.Cols.ID, leagueID)
                                .create(),
                        HttpConstants.PostMethod,
                        null);

        Futures.addCallback(future, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonObject) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.DELETE_LEAGUE, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                sendErrorMessage(callback, MobileServiceData.DELETE_LEAGUE, t.getMessage());
            }
        });
        return callback;
    }

    public MobileServiceCallback leaveLeague(final String userID, String leagueID) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!isNetworkAvailable(callback, MobileServiceData.LEAVE_LEAGUE))
            return callback;

        ListenableFuture<JsonElement> future =
                mClient.invokeApi(League.Entry.API_NAME_LEAVE_LEAGUE,
                        formatter.build()
                                .addProperty(League.Entry.USER_ID, userID)
                                .addProperty(League.Entry.Cols.ID, leagueID)
                                .create(),
                        HttpConstants.PostMethod,
                        null);

        Futures.addCallback(future, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonObject) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.LEAVE_LEAGUE, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                sendErrorMessage(callback, MobileServiceData.LEAVE_LEAGUE, t.getMessage());
            }
        });
        return callback;
    }

    public MobileServiceCallback fetchMoreUsers(final String leagueID, int skip, int top) {
        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!mIsNetworkAvailable) {
            callback.set(buildNetworkFailureMessage(MobileServiceData.FETCH_MORE_USERS)
                    .setLeagueUserList(new ArrayList<LeagueUser>())
                    .setString(leagueID)
                    .create());
            return callback;
        }

        MobileServiceJsonTableHelper t = MobileServiceJsonTableHelper
                .instance(User.Entry.TABLE_NAME, mClient);

        if (!LeagueWrapper.OVERALL_ID.equals(leagueID))
            t.parameters(LeagueUser.Entry.LEAGUE_ID, leagueID);

        ListenableFuture<JsonElement> i = t
                .top(top)
                .skip(skip)
                .orderBy(User.Entry.Cols.SCORE, QueryOrder.Descending)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {

                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_MORE_USERS, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLeagueUserList(parser.parseLeagueUserList(jsonElement))
                        .setString(leagueID)
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {

                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_MORE_USERS, MobileServiceData.REQUEST_RESULT_FAILURE)
                        .setLeagueUserList(new ArrayList<LeagueUser>())
                        .setString(leagueID)
                        .setMessage(throwable.getMessage())
                        .create());
            }
        });

        return callback;
    }

    public MobileServiceCallback fetchMoreUsers(final String leagueID, int skip, int top,
                                                int minMatchNumber, int maxMatchNumber) {
        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!mIsNetworkAvailable) {
            callback.set(buildNetworkFailureMessage(MobileServiceData.FETCH_MORE_USERS_BY_STAGE)
                    .setLeagueUserList(new ArrayList<LeagueUser>())
                    .setString(leagueID)
                    .create());
            return callback;
        }

        MobileServiceJsonTableHelper t = MobileServiceJsonTableHelper
                .instance(User.Entry.TABLE_NAME, mClient);

        if (!LeagueWrapper.OVERALL_ID.equals(leagueID))
            t.parameters(LeagueUser.Entry.LEAGUE_ID, leagueID);

        t.parameters(League.Entry.MIN_MATCH_NUMBER, String.valueOf(minMatchNumber));
        t.parameters(League.Entry.MAX_MATCH_NUMBER, String.valueOf(maxMatchNumber));

        ListenableFuture<JsonElement> i = t
                .top(top)
                .skip(skip)
                .orderBy(User.Entry.Cols.SCORE, QueryOrder.Descending)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {

                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_MORE_USERS_BY_STAGE, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLeagueUserList(parser.parseLeagueUserList(jsonElement))
                        .setString(leagueID)
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_MORE_USERS_BY_STAGE, MobileServiceData.REQUEST_RESULT_FAILURE)
                        .setLeagueUserList(new ArrayList<LeagueUser>())
                        .setString(leagueID)
                        .setMessage(throwable.getMessage())
                        .create());
            }
        });

        return callback;
    }

    public MobileServiceCallback fetchUsers(final String leagueID, final String userID, int skip, int top,
                                            final int minMatchNumber, final int maxMatchNumber) {
        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!mIsNetworkAvailable) {
            callback.set(buildNetworkFailureMessage(MobileServiceData.FETCH_USERS_BY_STAGE)
                    .setLeagueUserList(new ArrayList<LeagueUser>())
                    .setString(leagueID)
                    .create());
            return callback;
        }

        ListenableFuture<JsonElement> i = MobileServiceJsonTableHelper
                .instance(League.Entry.TABLE_NAME, mClient)
                .where().field(LeagueUser.Entry.USER_ID).eq(userID)
                .and().field(LeagueUser.Entry.LEAGUE_ID).eq(leagueID)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {

            private LeagueWrapper leagueWrapper;

            @Override
            public void onSuccess(JsonElement jsonElement) {

                List<League> leagueList = parser.parseLeagueList(jsonElement);

                if (leagueList == null || leagueList.size() == 0) {
                    sendErrorMessage(callback, MobileServiceData.FETCH_USERS_BY_STAGE, "No League found!!!");
                    return;
                }
                League league = leagueList.get(0);

                final MultipleCloudStatus n = new MultipleCloudStatus(2);
                final Object syncObj = new Object();

                leagueWrapper = new LeagueWrapper(league);

                MobileServiceCallback c = fetchMoreUsers(league.getID(), 0, 5, minMatchNumber, maxMatchNumber);
                MobileServiceCallback.addCallback(c, new MobileServiceCallback.OnResult() {
                    @Override
                    public void onResult(MobileServiceData data) {
                        synchronized (syncObj) {
                            Log.e(TAG, "iCountries finished");

                            if (n.isAborted()) return; // An error occurred
                            n.operationCompleted();

                            leagueWrapper.setLeagueUserList(data.getLeagueUserList());

                            if (n.isFinished())
                                tryOnFinished();
                        }
                    }
                });

                MobileServiceCallback i = fetchRankOfUser(leagueWrapper.getLeague().getID(), userID, minMatchNumber, maxMatchNumber);
                MobileServiceCallback.addCallback(i, new MobileServiceCallback.OnResult() {
                    @Override
                    public void onResult(MobileServiceData data) {
                        synchronized (syncObj) {

                            if (n.isAborted()) return; // An error occurred
                            n.operationCompleted();

                            List<LeagueUser> leagueUserList = data.getLeagueUserList();
                            if (leagueUserList.size() == 1)
                                leagueWrapper.setMainUser(leagueUserList.get(0));

                            if (n.isFinished())
                                tryOnFinished();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                sendErrorMessage(callback, MobileServiceData.FETCH_USERS_BY_STAGE, throwable.getMessage());
            }

            private void tryOnFinished() {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_USERS_BY_STAGE, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLeagueWrapper(leagueWrapper)
                        .create());
            }
        });

        return callback;
    }

    public MobileServiceCallback fetchRankOfUser(final String leagueID, String userID) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!mIsNetworkAvailable) {
            callback.set(buildNetworkFailureMessage(MobileServiceData.INSERT_PREDICTION)
                    .setLeagueUserList(new ArrayList<LeagueUser>())
                    .setString(leagueID)
                    .create());
            return callback;
        }

        MobileServiceJsonTableHelper t = MobileServiceJsonTableHelper
                .instance(User.Entry.TABLE_NAME, mClient);

        if (!LeagueWrapper.OVERALL_ID.equals(leagueID))
            t.parameters(LeagueUser.Entry.LEAGUE_ID, leagueID);

        ListenableFuture<JsonElement> i = t
                //.orderBy(User.Entry.Cols.SCORE, QueryOrder.Descending)
                .where().field(User.Entry.Cols.ID).eq(userID)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_RANK_OF_USER, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLeagueUserList(parser.parseLeagueUserList(jsonElement))
                        .setString(leagueID)
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_RANK_OF_USER, MobileServiceData.REQUEST_RESULT_FAILURE)
                        .setLeagueUserList(new ArrayList<LeagueUser>())
                        .setString(leagueID)
                        .setMessage(throwable.getMessage())
                        .create());
            }
        });

        return callback;
    }

    public MobileServiceCallback fetchRankOfUser(final String leagueID, String userID,
                                                 int minMatchNumber, int maxMatchNumber) {

        final MobileServiceCallback callback = new MobileServiceCallback();

        if (!mIsNetworkAvailable) {
            callback.set(buildNetworkFailureMessage(MobileServiceData.INSERT_PREDICTION)
                    .setLeagueUserList(new ArrayList<LeagueUser>())
                    .setString(leagueID)
                    .create());
            return callback;
        }

        MobileServiceJsonTableHelper t = MobileServiceJsonTableHelper
                .instance(User.Entry.TABLE_NAME, mClient);

        if (!LeagueWrapper.OVERALL_ID.equals(leagueID))
            t.parameters(LeagueUser.Entry.LEAGUE_ID, leagueID);

        t.parameters(League.Entry.MIN_MATCH_NUMBER, String.valueOf(minMatchNumber));
        t.parameters(League.Entry.MAX_MATCH_NUMBER, String.valueOf(maxMatchNumber));

        ListenableFuture<JsonElement> i = t
                //.orderBy(User.Entry.Cols.SCORE, QueryOrder.Descending)
                .where().field(User.Entry.Cols.ID).eq(userID)
                .execute();
        Futures.addCallback(i, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_RANK_OF_USER, MobileServiceData.REQUEST_RESULT_SUCCESS)
                        .setLeagueUserList(parser.parseLeagueUserList(jsonElement))
                        .setString(leagueID)
                        .create());
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                callback.set(MobileServiceData.Builder
                        .instance(MobileServiceData.FETCH_RANK_OF_USER, MobileServiceData.REQUEST_RESULT_FAILURE)
                        .setLeagueUserList(new ArrayList<LeagueUser>())
                        .setString(leagueID)
                        .setMessage(throwable.getMessage())
                        .create());
            }
        });

        return callback;
    }

    private static void sendErrorMessage(MobileServiceCallback callback, int requestCode, String errorMessage) {
        callback.set(MobileServiceData.Builder
                .instance(requestCode, MobileServiceData.REQUEST_RESULT_FAILURE)
                .setMessage(errorMessage)
                .create());
    }

    private boolean isNetworkAvailable(final MobileServiceCallback callback, int requestCode) {
        if (!mIsNetworkAvailable) {
            mIsNetworkAvailable = NetworkUtils.isNetworkAvailable(mContext);

            if (!mIsNetworkAvailable)
                callback.set(buildNetworkFailureMessage(requestCode).create());
        }
        return mIsNetworkAvailable;
    }

    private MobileServiceData.Builder buildNetworkFailureMessage(int operationType) {

        String message = "No Network Connection";
        if (mClient != null && mClient.getContext() != null) {
            message = mClient.getContext().getString(R.string.no_network_connection);
        }

        return MobileServiceData.Builder.instance(operationType, MobileServiceData.REQUEST_RESULT_FAILURE)
                .setMessage(message);
    }

    @Override
    public void setNetworkAvailable(boolean isNetworkAvailable) {
        mIsNetworkAvailable = isNetworkAvailable;
    }
}
