package org.hugoandrade.worldcup2018.predictor.admin;

import org.hugoandrade.worldcup2018.predictor.admin.data.SystemData;

public class GlobalData {

    private static SystemData mSystemData;

    public static SystemData getSystemData() {
        return mSystemData;
    }

    public static void setSystemData(SystemData systemData) {
        mSystemData = systemData;
    }
}
