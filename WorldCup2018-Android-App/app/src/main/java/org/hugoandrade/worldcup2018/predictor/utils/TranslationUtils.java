package org.hugoandrade.worldcup2018.predictor.utils;

import android.content.Context;

import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.Match;

import java.util.Locale;

public final class TranslationUtils {
    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = TranslationUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private TranslationUtils() {
        throw new AssertionError();
    }

    public static String translateStage(Context context, String stage) {

        if (context == null) return stage;

        if (stage.equals(StaticVariableUtils.SStage.groupStage.name)) return context.getString(R.string.group_stage);
        if (stage.equals(StaticVariableUtils.SStage.roundOf16.name)) return context.getString(R.string.round_of_16);
        if (stage.equals(StaticVariableUtils.SStage.quarterFinals.name)) return context.getString(R.string.quarter_finals);
        if (stage.equals(StaticVariableUtils.SStage.semiFinals.name)) return context.getString(R.string.semi_finals);
        if (stage.equals(StaticVariableUtils.SStage.thirdPlacePlayOff.name)) return context.getString(R.string.third_place_playoff);
        if (stage.equals(StaticVariableUtils.SStage.finals.name)) return context.getString(R.string.finals);
        return stage;
    }

    public static String translateCountryName(Context context, String countryName) {

        if (context == null) return countryName;

        if (countryName.equals(StaticVariableUtils.SCountry.Argentina.name)) return context.getString(R.string.country_argentina);
        if (countryName.equals(StaticVariableUtils.SCountry.Australia.name)) return context.getString(R.string.country_australia);
        if (countryName.equals(StaticVariableUtils.SCountry.Belgium.name)) return context.getString(R.string.country_belgium);
        if (countryName.equals(StaticVariableUtils.SCountry.Brazil.name)) return context.getString(R.string.country_brazil);
        if (countryName.equals(StaticVariableUtils.SCountry.Colombia.name)) return context.getString(R.string.country_colombia);
        if (countryName.equals(StaticVariableUtils.SCountry.CostaRica.name)) return context.getString(R.string.country_costa_rica);
        if (countryName.equals(StaticVariableUtils.SCountry.Croatia.name)) return context.getString(R.string.country_croatia);
        if (countryName.equals(StaticVariableUtils.SCountry.Denmark.name)) return context.getString(R.string.country_denmark);
        if (countryName.equals(StaticVariableUtils.SCountry.Egypt.name)) return context.getString(R.string.country_egypt);
        if (countryName.equals(StaticVariableUtils.SCountry.England.name)) return context.getString(R.string.country_england);
        if (countryName.equals(StaticVariableUtils.SCountry.France.name)) return context.getString(R.string.country_france);
        if (countryName.equals(StaticVariableUtils.SCountry.Germany.name)) return context.getString(R.string.country_germany);
        if (countryName.equals(StaticVariableUtils.SCountry.Iceland.name)) return context.getString(R.string.country_iceland);
        if (countryName.equals(StaticVariableUtils.SCountry.Iran.name)) return context.getString(R.string.country_iran);
        if (countryName.equals(StaticVariableUtils.SCountry.Japan.name)) return context.getString(R.string.country_japan);
        if (countryName.equals(StaticVariableUtils.SCountry.Mexico.name)) return context.getString(R.string.country_mexico);
        if (countryName.equals(StaticVariableUtils.SCountry.Morocco.name)) return context.getString(R.string.country_morocco);
        if (countryName.equals(StaticVariableUtils.SCountry.Nigeria.name)) return context.getString(R.string.country_nigeria);
        if (countryName.equals(StaticVariableUtils.SCountry.Panama.name)) return context.getString(R.string.country_panama);
        if (countryName.equals(StaticVariableUtils.SCountry.Peru.name)) return context.getString(R.string.country_peru);

        if (countryName.equals(StaticVariableUtils.SCountry.Poland.name)) return context.getString(R.string.country_poland);
        if (countryName.equals(StaticVariableUtils.SCountry.Portugal.name)) return context.getString(R.string.country_portugal);
        if (countryName.equals(StaticVariableUtils.SCountry.Russia.name)) return context.getString(R.string.country_russia);
        if (countryName.equals(StaticVariableUtils.SCountry.SaudiArabia.name)) return context.getString(R.string.country_saudi_arabia);

        if (countryName.equals(StaticVariableUtils.SCountry.Senegal.name)) return context.getString(R.string.country_senegal);
        if (countryName.equals(StaticVariableUtils.SCountry.Serbia.name)) return context.getString(R.string.country_serbia);
        if (countryName.equals(StaticVariableUtils.SCountry.SouthKorea.name)) return context.getString(R.string.country_south_korea);
        if (countryName.equals(StaticVariableUtils.SCountry.Spain.name)) return context.getString(R.string.country_spain);

        if (countryName.equals(StaticVariableUtils.SCountry.Sweden.name)) return context.getString(R.string.country_sweden);
        if (countryName.equals(StaticVariableUtils.SCountry.Switzerland.name)) return context.getString(R.string.country_switzerland);
        if (countryName.equals(StaticVariableUtils.SCountry.Tunisia.name)) return context.getString(R.string.country_tunisia);
        if (countryName.equals(StaticVariableUtils.SCountry.Uruguay.name)) return context.getString(R.string.country_uruguay);

        if (countryName.contains("Winner")) {
            int i = countryName.indexOf("Winner");
            countryName = countryName.substring(0, i)
                    + context.getString(R.string.winner)
                    + countryName.substring(i + "Winner".length(), countryName.length());
        }
        if (countryName.contains("Loser")) {
            int i = countryName.indexOf("Loser");
            countryName = countryName.substring(0, i)
                    + context.getString(R.string.loser)
                    + countryName.substring(i + "Loser".length(), countryName.length());
        }
        if (countryName.contains("Runner-up")) {
            int i = countryName.indexOf("Runner-up");
            countryName = countryName.substring(0, i)
                    + context.getString(R.string.runner_up)
                    + countryName.substring(i + "Runner-up".length(), countryName.length());
        }
        if (countryName.contains("Group")) {
            int i = countryName.indexOf("Group");
            countryName = countryName.substring(0, i)
                    + context.getString(R.string.group)
                    + countryName.substring(i + "Group".length(), countryName.length());
        }
        if (countryName.contains(" or ")) {
            int i = countryName.indexOf(" or ");
            countryName = countryName.substring(0, i)
                    + " "
                    + context.getString(R.string.or)
                    + " "
                    + countryName.substring(i + " or ".length(), countryName.length());
        }
        if (countryName.contains("Match")) {
            int i = countryName.indexOf("Match");
            countryName = countryName.substring(0, i)
                    + context.getString(R.string.match)
                    + countryName.substring(i + "Match".length(), countryName.length());
        }
        return countryName;
    }

    public static String translateStadium(Context context, String stadium) {

        if (context == null) return stadium;

        if (stadium.equals(StaticVariableUtils.Stadium.LuzhnikiStadium.name)) return context.getString(R.string.stadium_luzhniki_stadium);
        if (stadium.equals(StaticVariableUtils.Stadium.OtkritieArena.name)) return context.getString(R.string.stadium_otkritie_arena);
        if (stadium.equals(StaticVariableUtils.Stadium.KrestovskyStadium.name)) return context.getString(R.string.stadium_krestovsky_stadium);
        if (stadium.equals(StaticVariableUtils.Stadium.FishtOlympicStadium.name)) return context.getString(R.string.stadium_fisht_olympic_stadium);
        if (stadium.equals(StaticVariableUtils.Stadium.CosmosArena.name)) return context.getString(R.string.stadium_cosmos_arena);
        if (stadium.equals(StaticVariableUtils.Stadium.RostovArena.name)) return context.getString(R.string.stadium_rostov_arena);
        if (stadium.equals(StaticVariableUtils.Stadium.KazanArena.name)) return context.getString(R.string.stadium_kazan_arena);
        if (stadium.equals(StaticVariableUtils.Stadium.VolgogradArena.name)) return context.getString(R.string.stadium_volgograd_arena);
        if (stadium.equals(StaticVariableUtils.Stadium.NizhnyNovgorodStadium.name)) return context.getString(R.string.stadium_nizhny_novgorod_tadium);
        if (stadium.equals(StaticVariableUtils.Stadium.MordoviaArena.name)) return context.getString(R.string.stadium_mordovia_arena);
        if (stadium.equals(StaticVariableUtils.Stadium.CentralStadium.name)) return context.getString(R.string.stadium_central_stadium);
        if (stadium.equals(StaticVariableUtils.Stadium.KaliningradStadium.name)) return context.getString(R.string.stadium_kaliningrad_stadium);

        return stadium;
    }

    public static String getSuffix(int placeFinish) {
        if (Locale.getDefault().getLanguage().equals("pt")) {
            return "º";
        }
        else {/**/
            if (placeFinish == 1)
                return "st";
            else if (placeFinish == 2)
                return "nd";
            else if (placeFinish == 3)
                return "rd";
            else
                return "th";
        }
    }

    public static String getAsString(Context context, Match match) {
        return String.format("%s%s",
                TranslationUtils.translateStage(context, match.getStage()),
                match.getGroup() == null ? "" : (" - " + match.getGroup()));
    }
}
