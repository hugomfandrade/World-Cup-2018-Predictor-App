package org.hugoandrade.worldcup2018.predictor.backend.config;

import org.hugoandrade.worldcup2018.predictor.backend.model.Country;
import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.hugoandrade.worldcup2018.predictor.backend.repository.CountryRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.hugoandrade.worldcup2018.predictor.backend.config.StaticVariableUtils.SCountry;
import static org.hugoandrade.worldcup2018.predictor.backend.config.StaticVariableUtils.SGroup;
import static org.hugoandrade.worldcup2018.predictor.backend.config.StaticVariableUtils.SStage;
import static org.hugoandrade.worldcup2018.predictor.backend.config.StaticVariableUtils.Stadium;

@Service
public class StartupDatabaseScript {

    @Autowired private CountryRepository countryRepository;
    @Autowired private MatchRepository matchRepository;

    public void startup() {
        startupCountries();
        startupMatches();
    }

    private void startupMatches() {

        final List<Country> countries = StreamSupport
                .stream(countryRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        final List<Match> matches = configMatches(countries);

        final List<Match> dbMatches = StreamSupport
                .stream(matchRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        final Comparator<Match> matchSorter = Comparator.comparingInt(Match::getMatchNumber);
        final Comparator<Match> matchComparator = (o1, o2) -> {
            int matchNumber = o1.getMatchNumber() - o2.getMatchNumber();
            if (matchNumber != 0) return matchNumber;

            if (o1.getHomeTeamID() == null) return -1;
            if (o2.getHomeTeamID() == null) return 1;
            int homeTeam = o1.getHomeTeamID().compareTo(o2.getHomeTeamID());
            if (homeTeam != 0) return homeTeam;

            if (o1.getAwayTeamID() == null) return -1;
            if (o2.getAwayTeamID() == null) return 1;
            int awayTeam = o1.getAwayTeamID().compareTo(o2.getAwayTeamID());
            if (awayTeam != 0) return awayTeam;

            return 0;
        };

        matches.sort(matchSorter);
        dbMatches.sort(matchSorter);

        boolean areEqual = countries.size() == dbMatches.size() &&
                IntStream.range(0, countries.size())
                        .allMatch(i -> matchComparator.compare(matches.get(i), dbMatches.get(i)) == 0);

        if (!areEqual) {
            matchRepository.deleteAll();
            matchRepository.saveAll(matches);
        }
    }

    private void startupCountries() {

        final List<Country> countries = configCountries();

        final List<Country> dbCountries = StreamSupport
                .stream(countryRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        final Comparator<Country> countrySorter = (o1, o2) -> {
            if (o1.getName() == null) return -1;
            if (o2.getName() == null) return 1;
            return o1.getName().compareTo(o2.getName());
        };
        final Comparator<Country> countryComparator = (o1, o2) -> {
            if (o1.getName() == null) return -1;
            if (o2.getName() == null) return 1;
            int name = o1.getName().compareTo(o2.getName());
            if (name != 0) return name;

            if (o1.getGroup() == null) return -1;
            if (o2.getGroup() == null) return 1;
            int group = o1.getGroup().compareTo(o2.getGroup());
            if (group != 0) return group;

            return o1.getDrawingOfLots() - o2.getDrawingOfLots();
        };

        countries.sort(countrySorter);
        dbCountries.sort(countrySorter);

        boolean areEqual = countries.size() == dbCountries.size() &&
                IntStream.range(0, countries.size())
                        .allMatch(i -> countryComparator.compare(countries.get(i), dbCountries.get(i)) == 0);

        if (!areEqual) {
            countryRepository.deleteAll();
            countryRepository.saveAll(countries);
        }
    }

    public static List<Country> configCountries() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country(SCountry.Russia.name, SGroup.A.name, 1));
        countries.add(new Country(SCountry.SaudiArabia.name, SGroup.A.name, 2));
        countries.add(new Country(SCountry.Egypt.name, SGroup.A.name, 3));
        countries.add(new Country(SCountry.Uruguay.name, SGroup.A.name, 4));

        countries.add(new Country(SCountry.Portugal.name, SGroup.B.name, 1));
        countries.add(new Country(SCountry.Spain.name, SGroup.B.name, 2));
        countries.add(new Country(SCountry.Morocco.name, SGroup.B.name, 3));
        countries.add(new Country(SCountry.Iran.name, SGroup.B.name, 4));

        countries.add(new Country(SCountry.France.name, SGroup.C.name, 1));
        countries.add(new Country(SCountry.Australia.name, SGroup.C.name,2 ));
        countries.add(new Country(SCountry.Peru.name, SGroup.C.name, 3));
        countries.add(new Country(SCountry.Denmark.name, SGroup.C.name, 4));

        countries.add(new Country(SCountry.Argentina.name, SGroup.D.name, 1));
        countries.add(new Country(SCountry.Iceland.name, SGroup.D.name, 2));
        countries.add(new Country(SCountry.Croatia.name, SGroup.D.name, 3));
        countries.add(new Country(SCountry.Nigeria.name, SGroup.D.name, 4));

        countries.add(new Country(SCountry.Brazil.name, SGroup.E.name, 1));
        countries.add(new Country(SCountry.Switzerland.name, SGroup.E.name, 2));
        countries.add(new Country(SCountry.CostaRica.name, SGroup.E.name, 3));
        countries.add(new Country(SCountry.Serbia.name, SGroup.E.name, 4));

        countries.add(new Country(SCountry.Germany.name, SGroup.F.name, 1));
        countries.add(new Country(SCountry.Mexico.name, SGroup.F.name, 2));
        countries.add(new Country(SCountry.Sweden.name, SGroup.F.name, 3));
        countries.add(new Country(SCountry.SouthKorea.name, SGroup.F.name, 4));

        countries.add(new Country(SCountry.Belgium.name, SGroup.G.name, 1));
        countries.add(new Country(SCountry.Panama.name, SGroup.G.name, 2));
        countries.add(new Country(SCountry.Tunisia.name, SGroup.G.name, 3));
        countries.add(new Country(SCountry.England.name, SGroup.G.name, 4));

        countries.add(new Country(SCountry.Poland.name, SGroup.H.name, 1));
        countries.add(new Country(SCountry.Senegal.name, SGroup.H.name, 2));
        countries.add(new Country(SCountry.Colombia.name, SGroup.H.name, 3));
        countries.add(new Country(SCountry.Japan.name, SGroup.H.name, 4));
        return countries;
    }

    public static List<Match> configMatches(List<Country> countryList) {
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
        matchList.add(new Match(1, idRussia, idSaudiArabia, "140620181600",
                Stadium.LuzhnikiStadium.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(new Match(2, idEgypt, idUruguay, "150620181300",
                Stadium.CentralStadium.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(new Match(17, idRussia, idEgypt, "190620181900",
                Stadium.KrestovskyStadium.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(new Match(18, idUruguay, idSaudiArabia, "200620181600",
                Stadium.RostovArena.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(new Match(33, idUruguay, idRussia, "250620181500",
                Stadium.CosmosArena.name, SGroup.A.name, SStage.groupStage.name));
        matchList.add(new Match(34, idSaudiArabia, idEgypt, "250620181500",
                Stadium.VolgogradArena.name, SGroup.A.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group B
        matchList.add(new Match(4, idMorocco, idIran, "150620181600",
                Stadium.KrestovskyStadium.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(new Match(3, idPortugal, idSpain, "150620181900",
                Stadium.FishtOlympicStadium.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(new Match(19, idPortugal, idMorocco, "200620181300",
                Stadium.LuzhnikiStadium.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(new Match(20, idIran, idSpain, "200620181900",
                Stadium.KazanArena.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(new Match(35, idIran, idPortugal, "250620181900",
                Stadium.MordoviaArena.name, SGroup.B.name, SStage.groupStage.name));
        matchList.add(new Match(36, idSpain, idMorocco, "250620181900",
                Stadium.KaliningradStadium.name, SGroup.B.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group C
        matchList.add(new Match(5, idFrance, idAustralia, "160620181100",
                Stadium.KazanArena.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(new Match(6, idPeru, idDenmark, "160620181700",
                Stadium.MordoviaArena.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(new Match(22, idDenmark, idAustralia, "210620181300",
                Stadium.CosmosArena.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(new Match(21, idFrance, idPeru, "210620181600",
                Stadium.CentralStadium.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(new Match(37, idDenmark, idFrance, "260620181500",
                Stadium.LuzhnikiStadium.name, SGroup.C.name, SStage.groupStage.name));
        matchList.add(new Match(38, idAustralia, idPeru, "260620181500",
                Stadium.FishtOlympicStadium.name, SGroup.C.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group D
        matchList.add(new Match(7, idArgentina, idIceland, "160620181400",
                Stadium.OtkritieArena.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(new Match(8, idCroatia, idNigeria, "160620182000",
                Stadium.KaliningradStadium.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(new Match(23, idArgentina, idCroatia, "210620181900",
                Stadium.NizhnyNovgorodStadium.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(new Match(24, idNigeria, idIceland, "220620181600",
                Stadium.VolgogradArena.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(new Match(39, idNigeria, idArgentina, "260620181900",
                Stadium.KrestovskyStadium.name, SGroup.D.name, SStage.groupStage.name));
        matchList.add(new Match(40, idIceland, idCroatia, "260620181900",
                Stadium.RostovArena.name, SGroup.D.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group E
        matchList.add(new Match(10, idCostaRica, idSerbia, "170620181300",
                Stadium.CosmosArena.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(new Match(9, idBrazil, idSwitzerland, "170620181900",
                Stadium.RostovArena.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(new Match(25, idBrazil, idCostaRica, "220620181300",
                Stadium.KrestovskyStadium.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(new Match(26, idSerbia, idSwitzerland, "220620181900",
                Stadium.KaliningradStadium.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(new Match(41, idSerbia, idBrazil, "270620181900",
                Stadium.OtkritieArena.name, SGroup.E.name, SStage.groupStage.name));
        matchList.add(new Match(42, idSwitzerland, idCostaRica, "270620181900",
                Stadium.NizhnyNovgorodStadium.name, SGroup.E.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group F
        matchList.add(new Match(11, idGermany, idMexico, "170620181600",
                Stadium.LuzhnikiStadium.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(new Match(12, idSweden, idSouthKorea, "180620181300",
                Stadium.NizhnyNovgorodStadium.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(new Match(28, idSouthKorea, idMexico, "230620181600",
                Stadium.RostovArena.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(new Match(27, idGermany, idSweden, "230620181900",
                Stadium.FishtOlympicStadium.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(new Match(43, idSouthKorea, idGermany, "270620181500",
                Stadium.KazanArena.name, SGroup.F.name, SStage.groupStage.name));
        matchList.add(new Match(44, idMexico, idSweden, "270620181500",
                Stadium.CentralStadium.name, SGroup.F.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group G
        matchList.add(new Match(13, idBelgium, idPanama, "180620181600",
                Stadium.FishtOlympicStadium.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(new Match(14, idTunisia, idEngland, "180620181900",
                Stadium.VolgogradArena.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(new Match(29, idBelgium, idTunisia, "230620181300",
                Stadium.OtkritieArena.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(new Match(30, idEngland, idPanama, "240620181300",
                Stadium.NizhnyNovgorodStadium.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(new Match(45, idEngland, idBelgium, "280620181900",
                Stadium.KaliningradStadium.name, SGroup.G.name, SStage.groupStage.name));
        matchList.add(new Match(46, idPanama, idTunisia, "280620181900",
                Stadium.MordoviaArena.name, SGroup.G.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Group H
        matchList.add(new Match(16, idColombia, idJapan, "190620181300",
                Stadium.MordoviaArena.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(new Match(15, idPoland, idSenegal, "190620181600",
                Stadium.OtkritieArena.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(new Match(32, idJapan, idSenegal, "240620181600",
                Stadium.CentralStadium.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(new Match(31, idPoland, idColombia, "240620181900",
                Stadium.KazanArena.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(new Match(47, idJapan, idPoland, "280620181500",
                Stadium.VolgogradArena.name, SGroup.H.name, SStage.groupStage.name));
        matchList.add(new Match(48, idSenegal, idColombia, "280620181500",
                StaticVariableUtils.Stadium.CosmosArena.name, SGroup.H.name, SStage.groupStage.name));

        /* ****************************************************************************************************** */

        // Round of 16
        matchList.add(new Match(50, "Winner Group C", "Runner-up Group D",
                "300620181500", Stadium.KazanArena.name, null, SStage.roundOf16.name));
        matchList.add(new Match(49, "Winner Group A", "Runner-up Group B",
                "300620181900", Stadium.FishtOlympicStadium.name, null, SStage.roundOf16.name));
        matchList.add(new Match(51, "Winner Group B", "Runner-up Group A",
                "010720181500", Stadium.LuzhnikiStadium.name, null, SStage.roundOf16.name));
        matchList.add(new Match(52, "Winner Group D", "Runner-up Group C",
                "010720181900", Stadium.NizhnyNovgorodStadium.name, null, SStage.roundOf16.name));
        matchList.add(new Match(53, "Winner Group E", "Runner-up Group F",
                "020720181500", Stadium.CosmosArena.name, null, SStage.roundOf16.name));
        matchList.add(new Match(54, "Winner Group G", "Runner-up Group H",
                "020720181900", Stadium.RostovArena.name, null, SStage.roundOf16.name));
        matchList.add(new Match(55, "Winner Group F", "Runner-up Group E",
                "030720181500", Stadium.KrestovskyStadium.name, null, SStage.roundOf16.name));
        matchList.add(new Match(56, "Winner Group H", "Runner-up Group G",
                "030720181900", Stadium.OtkritieArena.name, null, SStage.roundOf16.name));

        /* ****************************************************************************************************** */

        // Quarter Finals
        matchList.add(new Match(57, "Winner Match 49", "Winner Match 50",
                "060720181500", Stadium.NizhnyNovgorodStadium.name, null, SStage.quarterFinals.name));
        matchList.add(new Match(58, "Winner Match 53", "Winner Match 54",
                "060720181900", Stadium.KazanArena.name, null, SStage.quarterFinals.name));
        matchList.add(new Match(60, "Winner Match 55", "Winner Match 56",
                "070720181500", Stadium.CosmosArena.name, null, SStage.quarterFinals.name));
        matchList.add(new Match(59, "Winner Match 51", "Winner Match 52",
                "070720181900", Stadium.FishtOlympicStadium.name, null, SStage.quarterFinals.name));

        /* ****************************************************************************************************** */

        // Semi Finals
        matchList.add(new Match(61, "Winner Match 57", "Winner Match 58",
                "100720181900", Stadium.KrestovskyStadium.name, null, SStage.semiFinals.name));
        matchList.add(new Match(62, "Winner Match 59", "Winner Match 60",
                "110720181900", Stadium.LuzhnikiStadium.name, null, SStage.semiFinals.name));

        /* ****************************************************************************************************** */

        // 3rd Place
        matchList.add(new Match(63, "Loser Match 61", "Loser Match 62",
                "140720181500", Stadium.KrestovskyStadium.name, null, SStage.thirdPlacePlayOff.name));
        /* ****************************************************************************************************** */

        // Final
        matchList.add(new Match(64, "Winner Match 61", "Winner Match 62",
                "150720181600", Stadium.LuzhnikiStadium.name, null, SStage.finals.name));
        return matchList;
    }
}
