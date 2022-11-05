package org.hugoandrade.worldcup2018.predictor.view;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import org.hugoandrade.worldcup2018.predictor.common.ContextView;
import org.hugoandrade.worldcup2018.predictor.common.PresenterOps;
import org.hugoandrade.worldcup2018.predictor.common.RetainedFragmentManager;

public abstract class ActivityBase<RequiredViewOps,
                                   ProvidedPresenterOps,
                                   PresenterType extends PresenterOps<RequiredViewOps>>
        extends AppCompatActivity
        implements ContextView {

    protected String TAG = getClass().getSimpleName();

    /**
     * Used to retain the ProvidedPresenterOps state between runtime
     * configuration changes.
     */
    private final RetainedFragmentManager mRetainedFragmentManager
            = new RetainedFragmentManager(this.getFragmentManager(),
            TAG);

    private PresenterType mPresenterInstance;

    /**
     * Initialize or reinitialize the Presenter layer.  This must be
     * called *after* the onCreate(Bundle saveInstanceState) method.
     *
     * @param opsType
     *            Class object that's used to create a Presenter object.
     * @param view
     *            Reference to the RequiredViewOps object in the View layer.
     */
    public void onCreate(Class<PresenterType> opsType,
                         RequiredViewOps view) {
        // Handle configuration-related events, including the initial
        // creation of an Activity and any subsequent runtime
        // configuration changes.
        try {
            // If this method returns true it's the first time the
            // Activity has been created.
            if (mRetainedFragmentManager.firstTimeIn()) {
                Log.d(TAG,
                        "First time calling onCreate()");

                // Initialize the ActivityBase fields.
                initialize(opsType,
                        view);
            } else {
                Log.d(TAG,
                        "Second (or subsequent) time calling onCreate()");

                // The RetainedFragmentManager was previously
                // initialized, which means that a runtime
                // configuration change occurred.
                reinitialize(opsType,
                        view);
            }
        } catch (Exception e) {
            Log.d(TAG,
                    "onCreate() "
                            + e);
            // Propagate this as a runtime exception.
            throw new RuntimeException(e);
        }
    }

    /**
     * Return the initialized ProvidedPresenterOps instance for use by
     * application logic in the View layer.
     */
    @SuppressWarnings("unchecked")
    public ProvidedPresenterOps getPresenter() {
        return (ProvidedPresenterOps) mPresenterInstance;
    }

    /**
     * Return the RetainedFragmentManager.
     */
    public RetainedFragmentManager getRetainedFragmentManager() {
        return mRetainedFragmentManager;
    }

    /**
     * Initialize the ActivityBase fields.
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void initialize(Class<PresenterType> opsType,
                            RequiredViewOps view)
            throws InstantiationException, IllegalAccessException {
        // Create the PresenterType object.
        mPresenterInstance = opsType.newInstance();

        // Put the PresenterInstance into the RetainedFragmentManager under
        // the simple mName.
        mRetainedFragmentManager.put(opsType.getSimpleName(),
                mPresenterInstance);

        // Perform the first initialization.
        mPresenterInstance.onCreate(view);
    }

    /**
     * Reinitialize the ActivityBase fields after a runtime
     * configuration change.
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void reinitialize(Class<PresenterType> opsType,
                              RequiredViewOps view)
            throws InstantiationException, IllegalAccessException {
        // Try to obtain the PresenterType instance from the
        // RetainedFragmentManager.
        mPresenterInstance =
                mRetainedFragmentManager.get(opsType.getSimpleName());

        // This check shouldn't be necessary under normal
        // circumstances, but it's better to lose state than to
        // crash!
        if (mPresenterInstance == null)
            // Initialize the ActivityBase fields.
            initialize(opsType,
                    view);
        else
            // Inform it that the runtime configuration change has
            // completed.
            mPresenterInstance.onConfigurationChange(view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenterInstance.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPresenterInstance.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPresenterInstance.onDestroy(isChangingConfigurations());
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return this;
    }

    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

}
