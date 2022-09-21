package org.hugoandrade.worldcup2018.predictor.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hugoandrade.worldcup2018.predictor.backend.config.StartupDatabaseScript;
import org.hugoandrade.worldcup2018.predictor.backend.model.Country;
import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.hugoandrade.worldcup2018.predictor.backend.repository.CountryRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.MatchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchesControllerTest extends BaseControllerTest {

    @Autowired private MockMvc mvc;

    @Autowired private MatchRepository matchRepository;
    @Autowired private CountryRepository countryRepository;

    @Test
    void all() throws Exception {

        final List<Country> countries = countryRepository.findAllAsList();

        mvc.perform(MockMvcRequestBuilders.get("/matches/"))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/matches/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<Match> matches = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Match>>(){});

                    Assertions.assertTrue(areEqual(matches, StartupDatabaseScript.configMatches(countries)));
                });

        mvc.perform(MockMvcRequestBuilders.get("/matches/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<Match> matches = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Match>>(){});

                    Assertions.assertTrue(areEqual(matches, StartupDatabaseScript.configMatches(countries)));
                });
    }

    @Test
    void addOne() throws Exception {

        Match match = new Match(1000, "1000", "1000", null, null, null, null);

        mvc.perform(MockMvcRequestBuilders.post("/matches/")
                        .content(format(match))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/matches/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(match))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/matches/")
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(match))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Match resMatch = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Match>(){});

                    Assertions.assertEquals(match.getHomeTeam(), resMatch.getHomeTeam());
                    Assertions.assertEquals(match.getAwayTeam(), resMatch.getAwayTeam());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                })
                .andDo(mvcResult -> {

                    // clean repo
                    matchRepository.deleteByMatchNumber(match.getMatchNumber());
                });
    }

    @Test
    void deleteAll() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/matches/"))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/matches/")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/matches/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andDo(mvcResult -> Assertions.assertEquals(0, matchRepository.count()))
                .andDo(mvcResult -> startupScript.startup())
                .andDo(mvcResult -> Assertions.assertEquals(64, matchRepository.count()));
    }

    @Test
    void getOne() throws Exception {

        Match match = matchRepository.findAll().iterator().next();

        mvc.perform(MockMvcRequestBuilders.get("/matches/" + match.getMatchNumber()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.get("/matches/" + match.getMatchNumber())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Match resMatch = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Match>(){});

                    Assertions.assertEquals(match.getHomeTeam(), resMatch.getHomeTeam());
                    Assertions.assertEquals(match.getAwayTeam(), resMatch.getAwayTeam());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                });

        mvc.perform(MockMvcRequestBuilders.get("/matches/" + match.getMatchNumber())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Match resMatch = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Match>(){});

                    Assertions.assertEquals(match.getHomeTeam(), resMatch.getHomeTeam());
                    Assertions.assertEquals(match.getAwayTeam(), resMatch.getAwayTeam());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                });
    }

    @Test
    void deleteOne() throws Exception {

        Match match = matchRepository.findAll().iterator().next();

        mvc.perform(MockMvcRequestBuilders.delete("/matches/" + match.getMatchNumber()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/matches/" + match.getMatchNumber())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.delete("/matches/" + match.getMatchNumber())
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Match resMatch = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Match>(){});

                    Assertions.assertEquals(match.getHomeTeam(), resMatch.getHomeTeam());
                    Assertions.assertEquals(match.getAwayTeam(), resMatch.getAwayTeam());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);

                    Assertions.assertEquals(63, matchRepository.count());
                })
                .andDo(mvcResult -> startupScript.startup());
    }

    @Test
    void updateOne() throws Exception {

        Match match = matchRepository.findAll().iterator().next();
        match.setHomeTeamGoals(1);
        match.setAwayTeamGoals(2);
        match.setAwayTeamNotes("p");

        mvc.perform(MockMvcRequestBuilders.put("/matches/" + match.getMatchNumber())
                        .content(format(match))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.put("/matches/" + match.getMatchNumber())
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(match))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.put("/matches/" + match.getMatchNumber())
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(match))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Match resMatch = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Match>(){});

                    Assertions.assertEquals(match.getHomeTeam(), resMatch.getHomeTeam());
                    Assertions.assertEquals(match.getAwayTeam(), resMatch.getAwayTeam());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                })
                // .andDo(mvcResult -> startupScript.startup())
        ;

        mvc.perform(MockMvcRequestBuilders.get("/matches/" + match.getMatchNumber())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Match resMatch = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Match>(){});

                    Assertions.assertEquals(match.getHomeTeam(), resMatch.getHomeTeam());
                    Assertions.assertEquals(match.getAwayTeam(), resMatch.getAwayTeam());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                });

        mvc.perform(MockMvcRequestBuilders.get("/matches/" + match.getMatchNumber())
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Match resMatch = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Match>(){});

                    Assertions.assertEquals(match.getHomeTeam(), resMatch.getHomeTeam());
                    Assertions.assertEquals(match.getAwayTeam(), resMatch.getAwayTeam());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                });

        startupScript.startup();
    }

    public static boolean areEqual(List<Match> matches1, List<Match> matches2) {

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

        matches1.sort(matchSorter);
        matches2.sort(matchSorter);

        boolean areEqual = matches1.size() == matches2.size() &&
                IntStream.range(0, matches1.size())
                        .allMatch(i -> matchComparator.compare(matches1.get(i), matches2.get(i)) == 0);

        return areEqual;
    }
}