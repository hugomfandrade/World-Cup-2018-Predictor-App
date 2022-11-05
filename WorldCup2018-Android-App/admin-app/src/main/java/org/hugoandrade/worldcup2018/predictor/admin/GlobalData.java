package org.hugoandrade.worldcup2018.predictor.admin;

import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.SystemData;

import java.util.ArrayList;

public class GlobalData {

    private static SystemData mSystemData;
    private static ArrayList<Country> countryList;
    private static ArrayList<Match> matchList;

    public static SystemData getSystemData() {
        return mSystemData;
    }

    public static void setSystemData(SystemData systemData) {
        mSystemData = systemData;
    }

    public static void setCountryList(ArrayList<Country> countryList) {
        GlobalData.countryList = countryList;
    }

    public static void setMatchList(ArrayList<Match> matchList) {
        GlobalData.matchList = matchList;
    }

    public static void setMatch(Match match) {

        for (int i = 0; i < matchList.size(); i++) {
            if (matchList.get(i).getID().equals(match.getID())) {
                matchList.set(i, match);
            }
        }
    }

    public static void setCountry(Country country) {

        for (int i = 0; i < countryList.size(); i++) {
            if (countryList.get(i).getID().equals(country.getID())) {
                countryList.set(i, country);
            }
        }
    }

    public static ArrayList<Country> getCountryList() {
        return countryList;
    }
}
