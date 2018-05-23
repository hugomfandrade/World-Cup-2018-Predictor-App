package org.hugoandrade.worldcup2018.predictor.presenter;

import android.content.Context;

import org.hugoandrade.worldcup2018.predictor.MVP;
import org.hugoandrade.worldcup2018.predictor.common.ContextView;
import org.hugoandrade.worldcup2018.predictor.common.PresenterOps;
import org.hugoandrade.worldcup2018.predictor.model.IMobileClientService;
import org.hugoandrade.worldcup2018.predictor.model.MobileClientModel;

public abstract class MobileClientPresenterBase<RequiredMainOps extends ContextView>

        extends PresenterBase<RequiredMainOps,
                              MVP.RequiredMobileClientPresenterOps,
                              MVP.ProvidedMobileClientModelOps,
                              MobileClientModel>

        implements PresenterOps<RequiredMainOps>,
        MVP.RequiredMobileClientPresenterOps {

    @Override
    public void onCreate(RequiredMainOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this MVP.RequiredModelOps
        // instance.
        super.onCreate(view, MobileClientModel.class, this);
    }

    @Override
    public void onResume() {
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(RequiredMainOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        getModel().onDestroy(isChangingConfiguration);
    }

    protected boolean isServiceBound() {
        return getModel() != null && getModel().isServiceBound();
    }

    IMobileClientService getMobileClientService() {
        return getModel().getService();
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
