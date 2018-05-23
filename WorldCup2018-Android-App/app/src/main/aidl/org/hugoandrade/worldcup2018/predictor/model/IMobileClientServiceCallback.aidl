// IMobileClientServiceCallback.aidl
package org.hugoandrade.worldcup2018.predictor.model;

import org.hugoandrade.worldcup2018.predictor.model.parser.MobileClientData;

/**
 * Interface defining the method that receives callbacks from the
 * MobileClientService.
 */
interface IMobileClientServiceCallback {

    /**
     * This one-way (non-blocking) method allows MobileClientService
     * to return the MobileClientData results associated with the one-way
     * IMobileClientService calls.
     */
    oneway void sendResults(in MobileClientData mobileClientData);

}
