package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountriesService;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static org.hugoandrade.worldcup2018.predictor.backend.config.StartupDatabaseScript.configMatches;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ListResultMatchers.list;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ObjResultMatchers.obj;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchesControllerTest extends BaseControllerTest {

    @Autowired private MockMvc mvc;

    @Autowired private MatchRepository matchRepository;
    @Autowired private CountriesService countriesService;

    @Autowired private ModelMapper modelMapper;

    @Test
    void all() throws Exception {

        final List<Country> countries = countriesService.getAll();
        final List<MatchDto> expectedMatches = configMatches(countries).stream()
                .map(m -> modelMapper.map(m, MatchDto.class))
                .collect(Collectors.toList());

        doOn(mvc).get("/matches/")
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).get("/matches/")
                .andExpect(status().isOk())
                .andExpect(list(MatchDto.class)
                        .assertEquals(expectedMatches, comparingInt(MatchDto::getMatchNumber), MATCH_COMPARATOR));

        mvc.perform(MockMvcRequestBuilders.get("/matches/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andExpect(list(MatchDto.class)
                        .assertEquals(expectedMatches, comparingInt(MatchDto::getMatchNumber), MATCH_COMPARATOR));
    }

    @Test
    void addOne() throws Exception {

        MatchDto match = new MatchDto(1000, "1000", "1000", null, null, null, null);

        doOn(mvc).post("/matches/", match)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).post("/matches/", match)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).post("/matches/", match)
                .andExpect(status().isOk())
                .andExpect(obj(MatchDto.class).addDo((resMatch) -> {
                    Assertions.assertEquals(match.getHomeTeamID(), resMatch.getHomeTeamID());
                    Assertions.assertEquals(match.getAwayTeamID(), resMatch.getAwayTeamID());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                }))
                .andDo(mvcResult -> {
                    // clean repo
                    matchRepository.deleteByMatchNumber(match.getMatchNumber());
                });
    }

    @Test
    void deleteAll() throws Exception {

        doOn(mvc).delete("/matches/")
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).delete("/matches/")
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).delete("/matches/")
                .andExpect(status().isOk())
                .andDo(mvcResult -> Assertions.assertEquals(0, matchRepository.count()))
                .andDo(mvcResult -> startupScript.startup())
                .andDo(mvcResult -> Assertions.assertEquals(64, matchRepository.count()));
    }

    @Test
    void getOne() throws Exception {

        MatchDto match = modelMapper.map(matchRepository.findAll().iterator().next(), MatchDto.class);

        doOn(mvc).get("/matches/" + match.getMatchNumber())
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).get("/matches/" + match.getMatchNumber())
                .andExpect(status().isOk())
                .andExpect(obj(MatchDto.class).addDo((resMatch) -> {
                    Assertions.assertEquals(match.getHomeTeamID(), resMatch.getHomeTeamID());
                    Assertions.assertEquals(match.getAwayTeamID(), resMatch.getAwayTeamID());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                }));

        doOn(mvc).withHeader(admin.getToken()).get("/matches/" + match.getMatchNumber())
                .andExpect(status().isOk())
                .andExpect(obj(MatchDto.class).addDo((resMatch) -> {
                    Assertions.assertEquals(match.getHomeTeamID(), resMatch.getHomeTeamID());
                    Assertions.assertEquals(match.getAwayTeamID(), resMatch.getAwayTeamID());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                }));
    }

    @Test
    void deleteOne() throws Exception {

        MatchDto match = modelMapper.map(matchRepository.findAll().iterator().next(), MatchDto.class);

        doOn(mvc).delete("/matches/" + match.getMatchNumber())
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).delete("/matches/" + match.getMatchNumber())
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).delete("/matches/" + match.getMatchNumber())
                .andExpect(status().isOk())
                .andExpect(obj(MatchDto.class).addDo((resMatch) -> {
                    Assertions.assertEquals(match.getHomeTeamID(), resMatch.getHomeTeamID());
                    Assertions.assertEquals(match.getAwayTeamID(), resMatch.getAwayTeamID());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);

                    Assertions.assertEquals(63, matchRepository.count());
                }))
                .andDo(mvcResult -> startupScript.startup());
    }

    @Test
    void updateOne() throws Exception {

        MatchDto match = modelMapper.map(matchRepository.findAll().iterator().next(), MatchDto.class);
        match.setHomeTeamGoals(1);
        match.setAwayTeamGoals(2);
        match.setAwayTeamNotes("p");

        doOn(mvc).put("/matches/" + match.getMatchNumber(), match)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken()).put("/matches/" + match.getMatchNumber(), match)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(admin.getToken()).put("/matches/" + match.getMatchNumber(), match)
                .andExpect(status().isOk())
                .andExpect(obj(MatchDto.class).addDo((resMatch) -> {
                    Assertions.assertEquals(match.getHomeTeamID(), resMatch.getHomeTeamID());
                    Assertions.assertEquals(match.getAwayTeamID(), resMatch.getAwayTeamID());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                }));

        doOn(mvc).withHeader(user.getToken()).get("/matches/" + match.getMatchNumber())
                .andExpect(status().isOk())
                .andExpect(obj(MatchDto.class).addDo((resMatch) -> {
                    Assertions.assertEquals(match.getHomeTeamID(), resMatch.getHomeTeamID());
                    Assertions.assertEquals(match.getAwayTeamID(), resMatch.getAwayTeamID());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                }));

        doOn(mvc).withHeader(admin.getToken()).get("/matches/" + match.getMatchNumber())
                .andExpect(status().isOk())
                .andExpect(obj(MatchDto.class).addDo((resMatch) -> {
                    Assertions.assertEquals(match.getHomeTeamID(), resMatch.getHomeTeamID());
                    Assertions.assertEquals(match.getAwayTeamID(), resMatch.getAwayTeamID());
                    Assertions.assertEquals(match.getMatchNumber(), resMatch.getMatchNumber());
                    Assertions.assertEquals(match, resMatch);
                }));

        startupScript.startup();
    }

    private final static Comparator<MatchDto> MATCH_COMPARATOR = (o1, o2) -> {

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
}