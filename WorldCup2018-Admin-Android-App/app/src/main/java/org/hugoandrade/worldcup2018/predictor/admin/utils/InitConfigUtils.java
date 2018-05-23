package org.hugoandrade.worldcup2018.predictor.admin.utils;

import android.util.Log;

import org.hugoandrade.worldcup2018.predictor.admin.data.Country;
import org.hugoandrade.worldcup2018.predictor.admin.data.Match;
import org.hugoandrade.worldcup2018.predictor.admin.data.SystemData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.hugoandrade.worldcup2018.predictor.admin.utils.StaticVariableUtils.*;

public final class InitConfigUtils {

    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = MatchUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private InitConfigUtils() {
        throw new AssertionError();
    }

    public static SystemData buildInitSystemData() {
        Calendar date = Calendar.getInstance();
        return new SystemData(null, "0,1,2,4", true, date, date);
    }

    public static List<Match> buildInitMatchList(List<Country> countryList) {
        HashMap<String, String> countryIDs = new HashMap<>();
        for (Country c : countryList) {
            countryIDs.put(c.getName(), c.getID());
        }

        String idRussia = countryIDs.get(SCountry.Russia.name);
        String idSaudiArabia = countryIDs.get(SCountry.SaudiArabia.name);
        String idEgypt = countryIDs.get(SCountry.Egypt.name);
        String idUruguay = countryIDs.get(SCountry.Uruguay.name);

        String idPortugal = countryIDs.get(SCountry.Portugal.name);
        String idSpain = countryIDs.get(SCountry.Spain.name);
        String idMorocco = countryIDs.get(SCountry.Morocco.name);
        String idIran = countryIDs.get(SCountry.Iran.name);

        String idFrance = countryIDs.get(SCountry.France.name);
        String idAustralia = countryIDs.get(SCountry.Australia.name);
        String idPeru = countryIDs.get(SCountry.Peru.name);
        String idDenmark = countryIDs.get(SCountry.Denmark.name);

        String idArgentina = countryIDs.get(SCountry.Argentina.name);
        String idIceland = countryIDs.get(SCountry.Iceland.name);
        String idCroatia = countryIDs.get(SCountry.Croatia.name);
        String idNigeria = countryIDs.get(SCountry.Nigeria.name);

        String idBrazil = countryIDs.get(SCountry.Brazil.name);
        String idSwitzerland = countryIDs.get(SCountry.Switzerland.name);
        String idCostaRica = countryIDs.get(SCountry.CostaRica.name);
        String idSerbia = countryIDs.get(SCountry.Serbia.name);

        String idGermany = countryIDs.get(SCountry.Germany.name);
        String idMexico = countryIDs.get(SCountry.Mexico.name);
        String idSweden = countryIDs.get(SCountry.Sweden.name);
        String idSouthKorea = countryIDs.get(SCountry.SouthKorea.name);

        String idBelgium = countryIDs.get(SCountry.Belgium.name);
        String idPanama = countryIDs.get(SCountry.Panama.name);
        String idTunisia = countryIDs.get(SCountry.Tunisia.name);
        String idEngland = countryIDs.get(SCountry.England.name);

        String idPoland = countryIDs.get(SCountry.Poland.name);
        String idSenegal = countryIDs.get(SCountry.Senegal.name);
        String idColombia = countryIDs.get(SCountry.Colombia.name);
        String idJapan = countryIDs.get(SCountry.Japan.name);

        List<Match> matchList = new ArrayList<>();

        // Group A
        matchList.add(emptyMatchInstance(1, idRussia, idSaudiArabia, "140620181600",
                Stadium.LuzhnikiStadium.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(2, idEgypt, idUruguay, "150620181300",
                Stadium.CentralStadium.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(17, idRussia, idEgypt, "190620181900",
                Stadium.KrestovskyStadium.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(18, idUruguay, idSaudiArabia, "200620181600",
                Stadium.RostovArena.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(33, idUruguay, idRussia, "250620181500",
                Stadium.CosmosArena.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(34, idSaudiArabia, idEgypt, "250620181500",
                Stadium.VolgogradArena.name, SGroup.A.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group B
        matchList.add(emptyMatchInstance(4, idMorocco, idIran, "150620181600",
                Stadium.KrestovskyStadium.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(3, idPortugal, idSpain, "150620181900",
                Stadium.FishtOlympicStadium.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(19, idPortugal, idMorocco, "200620181300",
                Stadium.LuzhnikiStadium.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(20, idIran, idSpain, "200620181900",
                Stadium.KazanArena.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(35, idIran, idPortugal, "250620181900",
                Stadium.MordoviaArena.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(36, idSpain, idMorocco, "250620181900",
                Stadium.KaliningradStadium.name, SGroup.B.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group C
        matchList.add(emptyMatchInstance(5, idFrance, idAustralia, "160620181100",
                Stadium.KazanArena.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(6, idPeru, idDenmark, "160620181700",
                Stadium.MordoviaArena.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(22, idDenmark, idAustralia, "210620181300",
                Stadium.CosmosArena.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(21, idFrance, idPeru, "210620181600",
                Stadium.CentralStadium.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(37, idDenmark, idFrance, "260620181500",
                Stadium.LuzhnikiStadium.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(38, idAustralia, idPeru, "260620181500",
                Stadium.FishtOlympicStadium.name, SGroup.C.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group D
        matchList.add(emptyMatchInstance(7, idArgentina, idIceland, "160620181400",
                Stadium.OtkritieArena.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(8, idCroatia, idNigeria, "160620182000",
                Stadium.KaliningradStadium.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(23, idArgentina, idCroatia, "210620181900",
                Stadium.NizhnyNovgorodStadium.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(24, idNigeria, idIceland, "220620181600",
                Stadium.VolgogradArena.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(39, idNigeria, idArgentina, "260620181900",
                Stadium.KrestovskyStadium.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(40, idIceland, idCroatia, "260620181900",
                Stadium.RostovArena.name, SGroup.D.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group E
        matchList.add(emptyMatchInstance(10, idCostaRica, idSerbia, "170620181300",
                Stadium.CosmosArena.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(9, idBrazil, idSwitzerland, "170620181900",
                Stadium.RostovArena.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(25, idBrazil, idCostaRica, "220620181300",
                Stadium.KrestovskyStadium.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(26, idSerbia, idSwitzerland, "220620181900",
                Stadium.KaliningradStadium.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(41, idSerbia, idBrazil, "270620181900",
                Stadium.OtkritieArena.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(42, idSwitzerland, idCostaRica, "270620181900",
                Stadium.NizhnyNovgorodStadium.name, SGroup.E.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group F
        matchList.add(emptyMatchInstance(11, idGermany, idMexico, "170620181600",
                Stadium.LuzhnikiStadium.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(12, idSweden, idSouthKorea, "180620181300",
                Stadium.NizhnyNovgorodStadium.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(28, idSouthKorea, idMexico, "230620181600",
                Stadium.RostovArena.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(27, idGermany, idSweden, "230620181900",
                Stadium.FishtOlympicStadium.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(43, idSouthKorea, idGermany, "270620181500",
                Stadium.KazanArena.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(44, idMexico, idSweden, "270620181500",
                Stadium.CentralStadium.name, SGroup.F.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group G
        matchList.add(emptyMatchInstance(13, idBelgium, idPanama, "180620181600",
                Stadium.FishtOlympicStadium.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(14, idTunisia, idEngland, "180620181900",
                Stadium.VolgogradArena.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(29, idBelgium, idTunisia, "230620181300",
                Stadium.OtkritieArena.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(30, idEngland, idPanama, "240620181300",
                Stadium.NizhnyNovgorodStadium.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(45, idEngland, idBelgium, "280620181900",
                Stadium.KaliningradStadium.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(46, idPanama, idTunisia, "280620181900",
                Stadium.MordoviaArena.name, SGroup.G.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group H
        matchList.add(emptyMatchInstance(16, idColombia, idJapan, "190620181300",
                Stadium.MordoviaArena.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(15, idPoland, idSenegal, "190620181600",
                Stadium.OtkritieArena.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(32, idJapan, idSenegal, "240620181600",
                Stadium.CentralStadium.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(31, idPoland, idColombia, "240620181900",
                Stadium.KazanArena.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(47, idJapan, idPoland, "280620181500",
                Stadium.VolgogradArena.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(emptyMatchInstance(48, idSenegal, idColombia, "280620181500",
                Stadium.CosmosArena.name, SGroup.H.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Round of 16
        matchList.add(emptyMatchInstance(50, "Winner Group C", "Runner-up Group D",
                "300620181500", Stadium.KazanArena.name, null, SStage.roundOf16.name));
        matchList.add(emptyMatchInstance(49, "Winner Group A", "Runner-up Group B",
                "300620181900", Stadium.FishtOlympicStadium.name, null, SStage.roundOf16.name));
        matchList.add(emptyMatchInstance(51, "Winner Group B", "Runner-up Group A",
                "010720181500", Stadium.LuzhnikiStadium.name, null, SStage.roundOf16.name));
        matchList.add(emptyMatchInstance(52, "Winner Group D", "Runner-up Group C",
                "010720181900", Stadium.NizhnyNovgorodStadium.name, null, SStage.roundOf16.name));
        matchList.add(emptyMatchInstance(53, "Winner Group E", "Runner-up Group F",
                "020720181500", Stadium.CosmosArena.name, null, SStage.roundOf16.name));
        matchList.add(emptyMatchInstance(54, "Winner Group G", "Runner-up Group H",
                "020720181900", Stadium.RostovArena.name, null, SStage.roundOf16.name));
        matchList.add(emptyMatchInstance(55, "Winner Group F", "Runner-up Group E",
                "030720181500", Stadium.KrestovskyStadium.name, null, SStage.roundOf16.name));
        matchList.add(emptyMatchInstance(56, "Winner Group H", "Runner-up Group G",
                "030720181900", Stadium.OtkritieArena.name, null, SStage.roundOf16.name));

        /* ****************************************************************************************************** */

        // Quarter Finals
        matchList.add(emptyMatchInstance(57, "Winner Match 49", "Winner Match 50",
                "060720181500", Stadium.NizhnyNovgorodStadium.name, null, SStage.quarterFinals.name));
        matchList.add(emptyMatchInstance(58, "Winner Match 53", "Winner Match 54",
                "060720181900", Stadium.KazanArena.name, null, SStage.quarterFinals.name));
        matchList.add(emptyMatchInstance(60, "Winner Match 55", "Winner Match 56",
                "070720181500", Stadium.CosmosArena.name, null, SStage.quarterFinals.name));
        matchList.add(emptyMatchInstance(59, "Winner Match 51", "Winner Match 52",
                "070720181900", Stadium.FishtOlympicStadium.name, null, SStage.quarterFinals.name));

        /* ****************************************************************************************************** */

        // Semi Finals
        matchList.add(emptyMatchInstance(61, "Winner Match 57", "Winner Match 58",
                "100720181900", Stadium.KrestovskyStadium.name, null, SStage.semiFinals.name));
        matchList.add(emptyMatchInstance(62, "Winner Match 59", "Winner Match 60",
                "110720181900", Stadium.LuzhnikiStadium.name, null, SStage.semiFinals.name));

        /* ****************************************************************************************************** */

        // 3rd Place
        matchList.add(emptyMatchInstance(63, "Loser Match 61", "Loser Match 62",
                "140720181500", Stadium.KrestovskyStadium.name, null, SStage.thirdPlacePlayOff.name));
        /* ****************************************************************************************************** */

        // Final
        matchList.add(emptyMatchInstance(64, "Winner Match 61", "Winner Match 62",
                "150720181600", Stadium.LuzhnikiStadium.name, null, SStage.finals.name));
        return matchList;
    }

    public static List<Country> buildInitCountryList() {
        List<Country> countryList = new ArrayList<>();
        countryList.add(emptyCountryInstance(SCountry.Russia.name, SGroup.A.name, 1));
        countryList.add(emptyCountryInstance(SCountry.SaudiArabia.name, SGroup.A.name, 2));
        countryList.add(emptyCountryInstance(SCountry.Egypt.name, SGroup.A.name, 3));
        countryList.add(emptyCountryInstance(SCountry.Uruguay.name, SGroup.A.name, 4));

        countryList.add(emptyCountryInstance(SCountry.Portugal.name, SGroup.B.name, 1));
        countryList.add(emptyCountryInstance(SCountry.Spain.name, SGroup.B.name, 2));
        countryList.add(emptyCountryInstance(SCountry.Morocco.name, SGroup.B.name, 3));
        countryList.add(emptyCountryInstance(SCountry.Iran.name, SGroup.B.name, 4));

        countryList.add(emptyCountryInstance(SCountry.France.name, SGroup.C.name, 1));
        countryList.add(emptyCountryInstance(SCountry.Australia.name, SGroup.C.name,2 ));
        countryList.add(emptyCountryInstance(SCountry.Peru.name, SGroup.C.name, 3));
        countryList.add(emptyCountryInstance(SCountry.Denmark.name, SGroup.C.name, 4));

        countryList.add(emptyCountryInstance(SCountry.Argentina.name, SGroup.D.name, 1));
        countryList.add(emptyCountryInstance(SCountry.Iceland.name, SGroup.D.name, 2));
        countryList.add(emptyCountryInstance(SCountry.Croatia.name, SGroup.D.name, 3));
        countryList.add(emptyCountryInstance(SCountry.Nigeria.name, SGroup.D.name, 4));

        countryList.add(emptyCountryInstance(SCountry.Brazil.name, SGroup.E.name, 1));
        countryList.add(emptyCountryInstance(SCountry.Switzerland.name, SGroup.E.name, 2));
        countryList.add(emptyCountryInstance(SCountry.CostaRica.name, SGroup.E.name, 3));
        countryList.add(emptyCountryInstance(SCountry.Serbia.name, SGroup.E.name, 4));

        countryList.add(emptyCountryInstance(SCountry.Germany.name, SGroup.F.name, 1));
        countryList.add(emptyCountryInstance(SCountry.Mexico.name, SGroup.F.name, 2));
        countryList.add(emptyCountryInstance(SCountry.Sweden.name, SGroup.F.name, 3));
        countryList.add(emptyCountryInstance(SCountry.SouthKorea.name, SGroup.F.name, 4));

        countryList.add(emptyCountryInstance(SCountry.Belgium.name, SGroup.G.name, 1));
        countryList.add(emptyCountryInstance(SCountry.Panama.name, SGroup.G.name, 2));
        countryList.add(emptyCountryInstance(SCountry.Tunisia.name, SGroup.G.name, 3));
        countryList.add(emptyCountryInstance(SCountry.England.name, SGroup.G.name, 4));

        countryList.add(emptyCountryInstance(SCountry.Poland.name, SGroup.H.name, 1));
        countryList.add(emptyCountryInstance(SCountry.Senegal.name, SGroup.H.name, 2));
        countryList.add(emptyCountryInstance(SCountry.Colombia.name, SGroup.H.name, 3));
        countryList.add(emptyCountryInstance(SCountry.Japan.name, SGroup.H.name, 4));
        return countryList;
    }

    private static Country emptyCountryInstance(String name,
                                                String group,
                                                int drawingOfLots) {
        int z = 0;
        return new Country(null, name, z, z, z, z, z, z, z, group, z, z, z, drawingOfLots);
    }

    private static Match emptyMatchInstance(int matchNumber, String homeTeamID, String awayTeamID,
                                            String date, String stadium, String group, String stage) {
        int z = -1;
        return new Match(null, matchNumber, homeTeamID, awayTeamID, z, z, null, null, group, stage, stadium,
                parseDate(date, TEMPLATE, Locale.UK));
    }

    private final static String TEMPLATE = "ddMMyyyyHHmm";

    private static Date parseDate(String date, String template, Locale locale) {
        DateFormat DATE_FORMATTER = new SimpleDateFormat(template, locale);
        try {
            return DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            Log.e(TAG, "ParseException: " + e.getMessage());
        }
        return null;
    }
}
