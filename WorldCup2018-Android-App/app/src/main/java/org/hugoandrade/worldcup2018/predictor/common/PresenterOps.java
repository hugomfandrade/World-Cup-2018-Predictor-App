package org.hugoandrade.worldcup2018.predictor.common;


/**
 * The base interface that an operations ("Ops") class in the
 * Presenter layer must implement.
 */
public interface PresenterOps<ViewOps> {
    /**
     * Hook method dispatched by the ActivityBase framework to
     * initialize an operations ("Ops") object after it's been
     * instantiated.
     *
     * @param view
     *        The currently active RequiredViewOps.
     */
    void onCreate(ViewOps view);

    void onResume();
    void onPause();

    /**
     * Hook method dispatched by the ActivityBase framework to
     * update an operations ("Ops") object after a runtime
     * configuration change has occurred in the View layer.
     *
     * @param view
     *        The currently active RequiredViewOps.
     */
    void onConfigurationChange(ViewOps view);

    /**
     * Hook method called when an Ops object in the Presenter layer is
     * destroyed.
     *
     * @param isChangingConfiguration
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    void onDestroy(boolean isChangingConfiguration);
}
