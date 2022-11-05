package org.hugoandrade.worldcup2018.predictor.view;

import org.hugoandrade.worldcup2018.predictor.common.ContextView;

public interface FragComm {


    /**
     * The base interface that an Activity class that has
     * child Fragments must implement.
     */
    interface RequiredActivityBaseOps extends ContextView {
        /**
         * The child fragment sends the message to the Parent activity, MainActivity,
         * to be displayed as a SnackBar.
         *
         * @param message
         *      Message to be sent to the Parent Activity.
         */
        void reportMessage(String message);
    }
}
