package org.hugoandrade.worldcup2018.predictor.utils;

import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.Country;

public final class CountryUtils {

    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = CountryUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private CountryUtils() {
        throw new AssertionError();
    }


    public static int getImageID(Country country) {
        if (country == null || country.getName() == null) return 0;

        switch (country.getName()) {
            case "Argentina": return R.drawable.ic_flag_argentina;
            case "Australia": return R.drawable.ic_flag_australia;
            case "Belgium": return R.drawable.ic_flag_belgium;
            case "Brazil": return R.drawable.ic_flag_brazil;
            case "Colombia": return R.drawable.ic_flag_colombia;
            case "Costa Rica": return R.drawable.ic_flag_costa_rica;
            case "Croatia": return R.drawable.ic_flag_croatia;
            case "Denmark": return R.drawable.ic_flag_denmark;
            case "Egypt": return R.drawable.ic_flag_egypt;
            case "England": return R.drawable.ic_flag_england;
            case "France": return R.drawable.ic_flag_france;
            case "Germany": return R.drawable.ic_flag_germany;
            case "Iceland": return R.drawable.ic_flag_iceland;
            case "Iran": return R.drawable.ic_flag_iran;
            case "Japan": return R.drawable.ic_flag_japan;
            case "Mexico": return R.drawable.ic_flag_mexico;
            case "Morocco": return R.drawable.ic_flag_morocco;
            case "Nigeria": return R.drawable.ic_flag_nigeria;
            case "Panama": return R.drawable.ic_flag_panama;
            case "Peru": return R.drawable.ic_flag_peru;
            case "Poland": return R.drawable.ic_flag_poland;
            case "Portugal": return R.drawable.ic_flag_portugal;
            case "Russia": return R.drawable.ic_flag_russia;
            case "Saudi Arabia": return R.drawable.ic_flag_saudi_arabia;
            case "Senegal": return R.drawable.ic_flag_senegal;
            case "Serbia": return R.drawable.ic_flag_serbia;
            case "South Korea": return R.drawable.ic_flag_south_korea;
            case "Spain": return R.drawable.ic_flag_spain;
            case "Sweden": return R.drawable.ic_flag_sweden;
            case "Switzerland": return R.drawable.ic_flag_switzerland;
            case "Tunisia": return R.drawable.ic_flag_tunisia;
            case "Uruguay": return R.drawable.ic_flag_uruguay;
        }
        return 0;
    }
}
