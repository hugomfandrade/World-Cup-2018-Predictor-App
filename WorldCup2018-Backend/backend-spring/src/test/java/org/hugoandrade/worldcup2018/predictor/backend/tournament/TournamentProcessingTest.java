package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountriesService;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country.Tournament.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TournamentProcessingTest extends BaseControllerTest {

    @Autowired CountriesService countriesService;
    @Autowired MatchesService matchesService;

    TournamentProcessing tournamentProcessing = new TournamentProcessing();

    @BeforeAll
    public void setUp() throws Exception {
        super.setUp();

        startupScript.startup();
    }

    private final static Map<Integer, Integer[]> SCORES_GROUP_B = new HashMap<>();
    static {
        SCORES_GROUP_B.put(4, new Integer[]{0, 1});
        SCORES_GROUP_B.put(3, new Integer[]{3, 3});
        SCORES_GROUP_B.put(19, new Integer[]{1, 0});
        SCORES_GROUP_B.put(20, new Integer[]{0, 1});
        SCORES_GROUP_B.put(35, new Integer[]{1, 1});
        SCORES_GROUP_B.put(36, new Integer[]{2, 2});
    }

    @Test
    void startUpdateGroupsProcessing_GroupB() {

        final List<Match> matches = matchesService.getAll();
        final List<Country> countries = countriesService.getAll();

        final Map<Integer, Match> matchMap = matches.stream()
                .collect(Collectors.toMap(Match::getMatchNumber, Function.identity()));
        final Map<String, Country> countryMap = countries.stream()
                .collect(Collectors.toMap(Country::getName, Function.identity()));

        for (Map.Entry<Integer, Integer[]> scoreEntry : SCORES_GROUP_B.entrySet()) {

            matchMap.get(scoreEntry.getKey()).setScore(
                    scoreEntry.getValue()[0],
                    scoreEntry.getValue()[1]);
        }

        final Country.Tournament[] expectedUpdatedCountries = Stream.of(Spain, Portugal, Morocco, Iran)
                .sorted()
                .toArray(Country.Tournament[]::new);
        final int[] expectedUpdatedMatchUps = Stream.of(49, 51)
                .sorted()
                .mapToInt(Integer::intValue)
                .toArray();

        final List<Country.Tournament> updatedCountries = new ArrayList<>();
        final List<Integer> updatedMatchUps = new ArrayList<>();

        tournamentProcessing.setListener(new TournamentProcessing.OnProcessingListener() {
            @Override public void onProcessingFinished(List<Country> countries, List<Match> matches) { }

            @Override
            public void updateCountry(Country country) {
                updatedCountries.add(Country.Tournament.valueOf(country.getName()));
            }

            @Override
            public void updateMatchUp(Match match) {
                updatedMatchUps.add(match.getMatchNumber());
            }
        });
        tournamentProcessing.startUpdateGroupsSync(countries, matches);

        Assertions.assertEquals(1, countryMap.get(Spain.name).getPosition());
        Assertions.assertEquals(2, countryMap.get(Portugal.name).getPosition());
        Assertions.assertEquals(3, countryMap.get(Iran.name).getPosition());
        Assertions.assertEquals(4, countryMap.get(Morocco.name).getPosition());

        Assertions.assertArrayEquals(new int[]{3, 1, 2, 0, 6, 5, 1, 5}, standingsDetails(countryMap.get(Spain.name)));
        Assertions.assertArrayEquals(new int[]{3, 1, 2, 0, 5, 4, 1, 5}, standingsDetails(countryMap.get(Portugal.name)));
        Assertions.assertArrayEquals(new int[]{3, 1, 1, 1, 2, 2, 0, 4}, standingsDetails(countryMap.get(Iran.name)));
        Assertions.assertArrayEquals(new int[]{3, 0, 1, 2, 2, 4, -2, 1}, standingsDetails(countryMap.get(Morocco.name)));

        Assertions.assertArrayEquals(expectedUpdatedCountries, updatedCountries.stream().sorted().toArray(Country.Tournament[]::new));
        Assertions.assertArrayEquals(expectedUpdatedMatchUps, updatedMatchUps.stream().mapToInt(i -> i).sorted().toArray());

        Assertions.assertEquals(matchMap.get(49).getAwayTeamID(), countryMap.get(Portugal.name).getID());
        Assertions.assertEquals(matchMap.get(51).getHomeTeamID(), countryMap.get(Spain.name).getID());
    }

    public static int[] standingsDetails(Country country) {
        return new int[]{
                country.getMatchesPlayed(),
                country.getVictories(),
                country.getDraws(),
                country.getDefeats(),
                country.getGoalsFor(),
                country.getGoalsAgainst(),
                country.getGoalsDifference(),
                country.getPoints()
        };
    }
}