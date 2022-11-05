package org.hugoandrade.worldcup2018.predictor.view.fragment;

import org.hugoandrade.worldcup2018.predictor.common.ServiceManager;
import org.hugoandrade.worldcup2018.predictor.view.FragComm;

public interface MainFragComm {

    /**
     * This interface defines the minimum API needed by any child
     * Fragment class to interact with MainActivity. It extends the
     * GenericRequiredActivityOps interface so that the Fragment can
     * report a message to the Parent activity to be displayed as
     * a SnackBar.
     */
    interface RequiredActivityOps extends FragComm.RequiredActivityBaseOps {

        ServiceManager getServiceManager();

        void disableUI();

        void enableUI();
    }
}
