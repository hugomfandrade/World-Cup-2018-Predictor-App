package org.hugoandrade.worldcup2018.predictor.view.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class FragmentBase<RequiredParentActivityOps extends FragComm.RequiredActivityBaseOps>
        extends Fragment {

    protected final String TAG = getClass().getSimpleName();

    private RequiredParentActivityOps mCommChListener;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(Context context) {
        super.onAttach(context);
        mCommChListener = (RequiredParentActivityOps) context;
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
