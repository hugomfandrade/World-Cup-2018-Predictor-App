package org.hugoandrade.worldcup2018.predictor.admin;

import org.hugoandrade.worldcup2018.predictor.common.ContextView;
import org.hugoandrade.worldcup2018.predictor.common.ModelOps;
import org.hugoandrade.worldcup2018.predictor.common.PresenterOps;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.LoginData;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.SystemData;
import org.hugoandrade.worldcup2018.predictor.admin.view.main.MainFragComm;

import java.util.ArrayList;

public interface MVP {

    interface RequiredViewBaseOps extends ContextView {

        /**
         * Disable UI by displaying over all layout a "Loading" progress bar
         */
        void disableUI();

        /**
         * Enable UI by dismissing the "Loading" progress bar
         */
        void enableUI();

        /**
         * Show a message, usually as a SnackBar.
         */
        void reportMessage(String message);
    }
    interface RequiredPresenterBaseOps extends ContextView {
        void notifyServiceIsBound();
    }

    interface RequiredLoginViewOps extends RequiredViewBaseOps {
        void successfulLogin();
    }
    interface ProvidedLoginPresenterOps extends PresenterOps<RequiredLoginViewOps> {
        void login(String username, String password);
    }
    interface RequiredLoginPresenterOps extends RequiredPresenterBaseOps {
        void loginRequestResult(boolean isOk, String message, LoginData loginData);

        void getSystemDataRequestResult(boolean isOk, String message, SystemData systemData);
    }
    interface ProvidedLoginModelOps extends ModelOps<RequiredLoginPresenterOps> {
        boolean login(String username, String password);

        boolean getSystemData();
    }


    interface RequiredViewOps extends RequiredViewBaseOps,
                                      MainFragComm.ProvidedGroupsChildFragmentOps,
                                      MainFragComm.ProvidedMatchesFragmentOps {
    }
    interface ProvidedPresenterOps extends PresenterOps<RequiredViewOps> {
        void setMatch(Match match);

        void updateSystemData(SystemData systemData);

        void reset();

        void updateScoresOfPredictions();

        void updateCountry(Country country);
    }
    interface RequiredPresenterOps extends RequiredPresenterBaseOps {

        void getAllInfoRequestResult(boolean isRetrieved,
                                     String message,
                                     ArrayList<Country> countryList,
                                     ArrayList<Match> matchList);

        void updateCountryRequestResult(boolean isRetrieved,
                                        String message,
                                        Country country);

        void updateMatchRequestResult(boolean isRetrieved,
                                      String message,
                                      Match match);

        void updateMatchUpRequestResult(boolean isRetrieved,
                                        String message,
                                        Match match);

        void updateSystemDataRequestResult(boolean isRetrieved,
                                           String message,
                                           SystemData systemData);

        void updateScoresOfPredictionsRequestResult(boolean isRetrieved, String message);
    }
    interface ProvidedModelOps extends ModelOps<RequiredPresenterOps> {
        boolean getInfo();

        boolean reset();

        boolean updateScoresOfPredictions();

        boolean updateMatchUp(Match match);

        boolean updateMatch(Match match);

        boolean updateCountry(Country country);

        boolean updateSystemData(SystemData systemData);
    }
}
