package org.hugoandrade.worldcup2018.predictor.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.hugoandrade.worldcup2018.predictor.common.ContextView;

public abstract class FragmentBase<RequiredParentActivityOps extends FragComm.RequiredActivityBaseOps>
        extends Fragment
        implements ContextView {

    protected final String TAG = getClass().getSimpleName();

    private RequiredParentActivityOps mCommChListener;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(Context context) {
        super.onAttach(context);
        mCommChListener = (RequiredParentActivityOps) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommChListener = null;
    }

    @Override
    public Context getActivityContext() {
        if (mCommChListener != null)
            return mCommChListener.getActivityContext();
        return null;
    }

    @Override
    public Context getApplicationContext() {
        if (mCommChListener != null)
            return mCommChListener.getApplicationContext();
        return null;
    }

    protected RequiredParentActivityOps getParentActivity() {
        return mCommChListener;
    }

    protected void reportMessage(String message) {
        if (mCommChListener != null)
            mCommChListener.reportMessage(message);
        else
            Log.e(TAG, "Error: communication channel not set. Message was: " + message);
    }
}
