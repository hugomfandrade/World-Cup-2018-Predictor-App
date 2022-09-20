package org.hugoandrade.worldcup2018.predictor.backend.config;

import org.hugoandrade.worldcup2018.predictor.backend.model.*;
import org.hugoandrade.worldcup2018.predictor.backend.model.Country.Tournament;
import org.hugoandrade.worldcup2018.predictor.backend.processing.TournamentProcessing;
import org.hugoandrade.worldcup2018.predictor.backend.repository.CountryRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.MatchRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.SystemDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class StartupDatabaseScript {

    @Autowired private SystemDataRepository systemDataRepository;
    @Autowired private CountryRepository countryRepository;
    @Autowired private MatchRepository matchRepository;

    public void startup() {
        startupSystemData();
        startupCountries();
        startupMatches();

        updateOrder();
    }

    private void updateOrder() {

        final List<Match> matches = matchRepository.findAllAsList();
        final List<Country> countries = countryRepository.findAllAsList();

        // first update, the positions
        final TournamentProcessing tournamentProcessing = new TournamentProcessing(new TournamentProcessing.OnProcessingListener() {
            @Override
            public void onProcessingFinished(List<Country> countries, List<Match> matches) {

                for (Country country : countries) {
                    Country dbCountry = countryRepository.findCountryById(country.getID());
                    countryRepository.save(country);
                }

                for (Match match : matches) {
                    Match dbMatch = matchRepository.findByMatchNumber(match.getMatchNumber());
                    matchRepository.save(match);
                }
            }

            @Override public void updateCountry(Country country) { }
            @Override public void updateMatchUp(Match match) { }

        }, countries);
        tournamentProcessing.startUpdateGroupsSync(matches);
    }

    private void startupSystemData() {

        systemDataRepository.deleteAll();
        systemDataRepository.save(new SystemData(null, "0,1,2,4", true, new Date(), new Date()));
    }

    private void startupMatches() {

        final List<Country> countries = countryRepository.findAllAsList();
        final List<Match> matches = configMatches(countries);
        final List<Match> dbMatches = matchRepository.findAllAsList();

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

        final List<Country> dbCountries = countryRepository.findAllAsList();

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
        countries.add(new Country(Tournament.Russia.name, Group.Tournament.A.name, 1));
        countries.add(new Country(Tournament.SaudiArabia.name, Group.Tournament.A.name, 2));
        countries.add(new Country(Tournament.Egypt.name, Group.Tournament.A.name, 3));
        countries.add(new Country(Tournament.Uruguay.name, Group.Tournament.A.name, 4));

        countries.add(new Country(Tournament.Portugal.name, Group.Tournament.B.name, 1));
        countries.add(new Country(Tournament.Spain.name, Group.Tournament.B.name, 2));
        countries.add(new Country(Tournament.Morocco.name, Group.Tournament.B.name, 3));
        countries.add(new Country(Tournament.Iran.name, Group.Tournament.B.name, 4));

        countries.add(new Country(Tournament.France.name, Group.Tournament.C.name, 1));
        countries.add(new Country(Tournament.Australia.name, Group.Tournament.C.name,2 ));
        countries.add(new Country(Tournament.Peru.name, Group.Tournament.C.name, 3));
        countries.add(new Country(Tournament.Denmark.name, Group.Tournament.C.name, 4));

        countries.add(new Country(Tournament.Argentina.name, Group.Tournament.D.name, 1));
        countries.add(new Country(Tournament.Iceland.name, Group.Tournament.D.name, 2));
        countries.add(new Country(Tournament.Croatia.name, Group.Tournament.D.name, 3));
        countries.add(new Country(Tournament.Nigeria.name, Group.Tournament.D.name, 4));

        countries.add(new Country(Tournament.Brazil.name, Group.Tournament.E.name, 1));
        countries.add(new Country(Tournament.Switzerland.name, Group.Tournament.E.name, 2));
        countries.add(new Country(Tournament.CostaRica.name, Group.Tournament.E.name, 3));
        countries.add(new Country(Tournament.Serbia.name, Group.Tournament.E.name, 4));

        countries.add(new Country(Tournament.Germany.name, Group.Tournament.F.name, 1));
        countries.add(new Country(Tournament.Mexico.name, Group.Tournament.F.name, 2));
        countries.add(new Country(Tournament.Sweden.name, Group.Tournament.F.name, 3));
        countries.add(new Country(Tournament.SouthKorea.name, Group.Tournament.F.name, 4));

        countries.add(new Country(Tournament.Belgium.name, Group.Tournament.G.name, 1));
        countries.add(new Country(Tournament.Panama.name, Group.Tournament.G.name, 2));
        countries.add(new Country(Tournament.Tunisia.name, Group.Tournament.G.name, 3));
        countries.add(new Country(Tournament.England.name, Group.Tournament.G.name, 4));

        countries.add(new Country(Tournament.Poland.name, Group.Tournament.H.name, 1));
        countries.add(new Country(Tournament.Senegal.name, Group.Tournament.H.name, 2));
        countries.add(new Country(Tournament.Colombia.name, Group.Tournament.H.name, 3));
        countries.add(new Country(Tournament.Japan.name, Group.Tournament.H.name, 4));
        return countries;
    }

    public static List<Match> configMatches(List<Country> countries) {
        HashMap<String, String> countryIDs = new HashMap<>();
        for (Country c : countries) {
            countryIDs.put(c.getName(), c.getID());
        }

        String idRussia = countryIDs.get(Tournament.Russia.name);
        String idSaudiArabia = countryIDs.get(Tournament.SaudiArabia.name);
        String idEgypt = countryIDs.get(Tournament.Egypt.name);
        String idUruguay = countryIDs.get(Tournament.Uruguay.name);

        String idPortugal = countryIDs.get(Tournament.Portugal.name);
        String idSpain = countryIDs.get(Tournament.Spain.name);
        String idMorocco = countryIDs.get(Tournament.Morocco.name);
        String idIran = countryIDs.get(Tournament.Iran.name);

        String idFrance = countryIDs.get(Tournament.France.name);
        String idAustralia = countryIDs.get(Tournament.Australia.name);
        String idPeru = countryIDs.get(Tournament.Peru.name);
        String idDenmark = countryIDs.get(Tournament.Denmark.name);

        String idArgentina = countryIDs.get(Tournament.Argentina.name);
        String idIceland = countryIDs.get(Tournament.Iceland.name);
        String idCroatia = countryIDs.get(Tournament.Croatia.name);
        String idNigeria = countryIDs.get(Tournament.Nigeria.name);

        String idBrazil = countryIDs.get(Tournament.Brazil.name);
        String idSwitzerland = countryIDs.get(Tournament.Switzerland.name);
        String idCostaRica = countryIDs.get(Tournament.CostaRica.name);
        String idSerbia = countryIDs.get(Tournament.Serbia.name);

        String idGermany = countryIDs.get(Tournament.Germany.name);
        String idMexico = countryIDs.get(Tournament.Mexico.name);
        String idSweden = countryIDs.get(Tournament.Sweden.name);
        String idSouthKorea = countryIDs.get(Tournament.SouthKorea.name);

        String idBelgium = countryIDs.get(Tournament.Belgium.name);
        String idPanama = countryIDs.get(Tournament.Panama.name);
        String idTunisia = countryIDs.get(Tournament.Tunisia.name);
        String idEngland = countryIDs.get(Tournament.England.name);

        String idPoland = countryIDs.get(Tournament.Poland.name);
        String idSenegal = countryIDs.get(Tournament.Senegal.name);
        String idColombia = countryIDs.get(Tournament.Colombia.name);
        String idJapan = countryIDs.get(Tournament.Japan.name);

        List<Match> matchList = new ArrayList<>();

        // Group A
        matchList.add(new Match(1, idRussia, idSaudiArabia, "140620181600",
                Stadium.LuzhnikiStadium.name, Group.Tournament.A.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(2, idEgypt, idUruguay, "150620181300",
                Stadium.CentralStadium.name, Group.Tournament.A.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(17, idRussia, idEgypt, "190620181900",
                Stadium.KrestovskyStadium.name, Group.Tournament.A.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(18, idUruguay, idSaudiArabia, "200620181600",
                Stadium.RostovArena.name, Group.Tournament.A.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(33, idUruguay, idRussia, "250620181500",
                Stadium.CosmosArena.name, Group.Tournament.A.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(34, idSaudiArabia, idEgypt, "250620181500",
                Stadium.VolgogradArena.name, Group.Tournament.A.name, Stage.GROUP_STAGE.name));

        /* ****************************************************************************************************** */

        // Group B
        matchList.add(new Match(4, idMorocco, idIran, "150620181600",
                Stadium.KrestovskyStadium.name, Group.Tournament.B.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(3, idPortugal, idSpain, "150620181900",
                Stadium.FishtOlympicStadium.name, Group.Tournament.B.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(19, idPortugal, idMorocco, "200620181300",
                Stadium.LuzhnikiStadium.name, Group.Tournament.B.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(20, idIran, idSpain, "200620181900",
                Stadium.KazanArena.name, Group.Tournament.B.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(35, idIran, idPortugal, "250620181900",
                Stadium.MordoviaArena.name, Group.Tournament.B.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(36, idSpain, idMorocco, "250620181900",
                Stadium.KaliningradStadium.name, Group.Tournament.B.name, Stage.GROUP_STAGE.name));

        /* ****************************************************************************************************** */

        // Group C
        matchList.add(new Match(5, idFrance, idAustralia, "160620181100",
                Stadium.KazanArena.name, Group.Tournament.C.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(6, idPeru, idDenmark, "160620181700",
                Stadium.MordoviaArena.name, Group.Tournament.C.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(22, idDenmark, idAustralia, "210620181300",
                Stadium.CosmosArena.name, Group.Tournament.C.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(21, idFrance, idPeru, "210620181600",
                Stadium.CentralStadium.name, Group.Tournament.C.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(37, idDenmark, idFrance, "260620181500",
                Stadium.LuzhnikiStadium.name, Group.Tournament.C.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(38, idAustralia, idPeru, "260620181500",
                Stadium.FishtOlympicStadium.name, Group.Tournament.C.name, Stage.GROUP_STAGE.name));

        /* ****************************************************************************************************** */

        // Group D
        matchList.add(new Match(7, idArgentina, idIceland, "160620181400",
                Stadium.OtkritieArena.name, Group.Tournament.D.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(8, idCroatia, idNigeria, "160620182000",
                Stadium.KaliningradStadium.name, Group.Tournament.D.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(23, idArgentina, idCroatia, "210620181900",
                Stadium.NizhnyNovgorodStadium.name, Group.Tournament.D.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(24, idNigeria, idIceland, "220620181600",
                Stadium.VolgogradArena.name, Group.Tournament.D.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(39, idNigeria, idArgentina, "260620181900",
                Stadium.KrestovskyStadium.name, Group.Tournament.D.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(40, idIceland, idCroatia, "260620181900",
                Stadium.RostovArena.name, Group.Tournament.D.name, Stage.GROUP_STAGE.name));

        /* ****************************************************************************************************** */

        // Group E
        matchList.add(new Match(10, idCostaRica, idSerbia, "170620181300",
                Stadium.CosmosArena.name, Group.Tournament.E.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(9, idBrazil, idSwitzerland, "170620181900",
                Stadium.RostovArena.name, Group.Tournament.E.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(25, idBrazil, idCostaRica, "220620181300",
                Stadium.KrestovskyStadium.name, Group.Tournament.E.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(26, idSerbia, idSwitzerland, "220620181900",
                Stadium.KaliningradStadium.name, Group.Tournament.E.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(41, idSerbia, idBrazil, "270620181900",
                Stadium.OtkritieArena.name, Group.Tournament.E.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(42, idSwitzerland, idCostaRica, "270620181900",
                Stadium.NizhnyNovgorodStadium.name, Group.Tournament.E.name, Stage.GROUP_STAGE.name));

        /* ****************************************************************************************************** */

        // Group F
        matchList.add(new Match(11, idGermany, idMexico, "170620181600",
                Stadium.LuzhnikiStadium.name, Group.Tournament.F.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(12, idSweden, idSouthKorea, "180620181300",
                Stadium.NizhnyNovgorodStadium.name, Group.Tournament.F.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(28, idSouthKorea, idMexico, "230620181600",
                Stadium.RostovArena.name, Group.Tournament.F.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(27, idGermany, idSweden, "230620181900",
                Stadium.FishtOlympicStadium.name, Group.Tournament.F.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(43, idSouthKorea, idGermany, "270620181500",
                Stadium.KazanArena.name, Group.Tournament.F.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(44, idMexico, idSweden, "270620181500",
                Stadium.CentralStadium.name, Group.Tournament.F.name, Stage.GROUP_STAGE.name));

        /* ****************************************************************************************************** */

        // Group G
        matchList.add(new Match(13, idBelgium, idPanama, "180620181600",
                Stadium.FishtOlympicStadium.name, Group.Tournament.G.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(14, idTunisia, idEngland, "180620181900",
                Stadium.VolgogradArena.name, Group.Tournament.G.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(29, idBelgium, idTunisia, "230620181300",
                Stadium.OtkritieArena.name, Group.Tournament.G.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(30, idEngland, idPanama, "240620181300",
                Stadium.NizhnyNovgorodStadium.name, Group.Tournament.G.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(45, idEngland, idBelgium, "280620181900",
                Stadium.KaliningradStadium.name, Group.Tournament.G.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(46, idPanama, idTunisia, "280620181900",
                Stadium.MordoviaArena.name, Group.Tournament.G.name, Stage.GROUP_STAGE.name));

        /* ****************************************************************************************************** */

        // Group H
        matchList.add(new Match(16, idColombia, idJapan, "190620181300",
                Stadium.MordoviaArena.name, Group.Tournament.H.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(15, idPoland, idSenegal, "190620181600",
                Stadium.OtkritieArena.name, Group.Tournament.H.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(32, idJapan, idSenegal, "240620181600",
                Stadium.CentralStadium.name, Group.Tournament.H.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(31, idPoland, idColombia, "240620181900",
                Stadium.KazanArena.name, Group.Tournament.H.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(47, idJapan, idPoland, "280620181500",
                Stadium.VolgogradArena.name, Group.Tournament.H.name, Stage.GROUP_STAGE.name));
        matchList.add(new Match(48, idSenegal, idColombia, "280620181500",
                Stadium.CosmosArena.name, Group.Tournament.H.name, Stage.GROUP_STAGE.name));

        /* ****************************************************************************************************** */

        // Round of 16
        matchList.add(new Match(50, "Winner Group C", "Runner-up Group D",
                "300620181500", Stadium.KazanArena.name, null, Stage.ROUND_OF_16.name));
        matchList.add(new Match(49, "Winner Group A", "Runner-up Group B",
                "300620181900", Stadium.FishtOlympicStadium.name, null, Stage.ROUND_OF_16.name));
        matchList.add(new Match(51, "Winner Group B", "Runner-up Group A",
                "010720181500", Stadium.LuzhnikiStadium.name, null, Stage.ROUND_OF_16.name));
        matchList.add(new Match(52, "Winner Group D", "Runner-up Group C",
                "010720181900", Stadium.NizhnyNovgorodStadium.name, null, Stage.ROUND_OF_16.name));
        matchList.add(new Match(53, "Winner Group E", "Runner-up Group F",
                "020720181500", Stadium.CosmosArena.name, null, Stage.ROUND_OF_16.name));
        matchList.add(new Match(54, "Winner Group G", "Runner-up Group H",
                "020720181900", Stadium.RostovArena.name, null, Stage.ROUND_OF_16.name));
        matchList.add(new Match(55, "Winner Group F", "Runner-up Group E",
                "030720181500", Stadium.KrestovskyStadium.name, null, Stage.ROUND_OF_16.name));
        matchList.add(new Match(56, "Winner Group H", "Runner-up Group G",
                "030720181900", Stadium.OtkritieArena.name, null, Stage.ROUND_OF_16.name));

        /* ****************************************************************************************************** */

        // Quarter Finals
        matchList.add(new Match(57, "Winner Match 49", "Winner Match 50",
                "060720181500", Stadium.NizhnyNovgorodStadium.name, null, Stage.QUARTER_FINALS.name));
        matchList.add(new Match(58, "Winner Match 53", "Winner Match 54",
                "060720181900", Stadium.KazanArena.name, null, Stage.QUARTER_FINALS.name));
        matchList.add(new Match(60, "Winner Match 55", "Winner Match 56",
                "070720181500", Stadium.CosmosArena.name, null, Stage.QUARTER_FINALS.name));
        matchList.add(new Match(59, "Winner Match 51", "Winner Match 52",
                "070720181900", Stadium.FishtOlympicStadium.name, null, Stage.QUARTER_FINALS.name));

        /* ****************************************************************************************************** */

        // Semi Finals
        matchList.add(new Match(61, "Winner Match 57", "Winner Match 58",
                "100720181900", Stadium.KrestovskyStadium.name, null, Stage.SEMI_FINALS.name));
        matchList.add(new Match(62, "Winner Match 59", "Winner Match 60",
                "110720181900", Stadium.LuzhnikiStadium.name, null, Stage.SEMI_FINALS.name));

        /* ****************************************************************************************************** */

        // 3rd Place
        matchList.add(new Match(63, "Loser Match 61", "Loser Match 62",
                "140720181500", Stadium.KrestovskyStadium.name, null, Stage.THIRD_PLACE_PLAY_OFF.name));
        /* ****************************************************************************************************** */

        // Final
        matchList.add(new Match(64, "Winner Match 61", "Winner Match 62",
                "150720181600", Stadium.LuzhnikiStadium.name, null, Stage.FINAL.name));
        return matchList;
    }
}
