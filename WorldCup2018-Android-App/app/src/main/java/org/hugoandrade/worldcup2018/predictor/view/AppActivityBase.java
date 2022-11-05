package org.hugoandrade.worldcup2018.predictor.view;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.common.ContextView;
import org.hugoandrade.worldcup2018.predictor.common.PresenterOps;
import org.hugoandrade.worldcup2018.predictor.utils.NetworkBroadcastReceiverUtils;
import org.hugoandrade.worldcup2018.predictor.utils.NetworkUtils;
import org.hugoandrade.worldcup2018.predictor.utils.ViewUtils;

import java.util.Locale;

public abstract class AppActivityBase<RequiredViewOps,
                                   ProvidedPresenterOps,
                                   PresenterType extends PresenterOps<RequiredViewOps>>
        extends ActivityBase<RequiredViewOps, ProvidedPresenterOps, PresenterType>
        implements ContextView {

    private BroadcastReceiver mNetworkBroadcastReceiver;
    private View tvNoNetworkConnection;

    /**
     * Initialize or reinitialize the Presenter layer.  This must be
     * called *after* the onCreate(Bundle saveInstanceState) method.
     *
     * @param opsType
     *            Class object that's used to build a Presenter object.
     * @param view
     *            Reference to the RequiredViewOps object in the View layer.
     */
    public void onCreate(Class<PresenterType> opsType, RequiredViewOps view) {
        super.onCreate(opsType, view);

        setupNoNetworkUtility();
    }

    /**
     * Hook method called by Android when this Activity becomes
     * invisible.
     */
    @Override
    protected void onDestroy() {

        if (mNetworkBroadcastReceiver != null) {
            NetworkBroadcastReceiverUtils.unregister(getActivityContext(), mNetworkBroadcastReceiver);
            mNetworkBroadcastReceiver = null;
        }

        super.onDestroy();
    }

    private void setupNoNetworkUtility() {

        mNetworkBroadcastReceiver = NetworkBroadcastReceiverUtils.register(getActivityContext(), iNetworkListener);

        tvNoNetworkConnection = findViewById(R.id.tv_no_network_connection);

        ViewUtils.setHeightDp(this, tvNoNetworkConnection,
                NetworkUtils.isNetworkAvailable(this)? 0 : 20);
    }

    private NetworkBroadcastReceiverUtils.INetworkBroadcastReceiver iNetworkListener
            = new NetworkBroadcastReceiverUtils.INetworkBroadcastReceiver() {
        @Override
        public void setNetworkAvailable(boolean isNetworkAvailable) {
            if (isNetworkAvailable)
                notifyNetworkIsAvailable();
            ViewUtils.setHeightDpAnim(getApplicationContext(), tvNoNetworkConnection,
                    NetworkUtils.isNetworkAvailable(getActivityContext())? 0 : 20);
        }
    };

    protected void notifyNetworkIsAvailable() {

    }
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }

    private Context updateBaseContextLocale(Context context) {
        Locale locale = Locale.US;
        if (Locale.getDefault().getLanguage().equals("pt")) {
            locale = Locale.getDefault();
        }
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}
