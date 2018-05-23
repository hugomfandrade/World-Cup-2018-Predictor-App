package org.hugoandrade.worldcup2018.predictor.model;


import org.hugoandrade.worldcup2018.predictor.MVP;
import org.hugoandrade.worldcup2018.predictor.model.parser.MobileClientData;

public class MobileClientModel extends MobileClientModelBase<MVP.RequiredMobileClientPresenterOps>

        implements MVP.ProvidedMobileClientModelOps {

    @Override
    public IMobileClientService getService() {
        return super.getService();
    }

    @Override
    public void sendResults(MobileClientData data) {
        getPresenter().sendResults(data);
    }
}
