package org.hugoandrade.worldcup2018.predictor.backend.tournament;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.country.CountryDto;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hugoandrade.worldcup2018.predictor.backend.tournament.TournamentProcessingTest.standingsDetails;
import static org.hugoandrade.worldcup2018.predictor.backend.tournament.country.Country.Tournament.*;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TournamentProcessingRestAPITest extends BaseControllerTest {

    @Autowired
    private MockMvc mvc;

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
    void startUpdateGroupsProcessing_GroupB() throws Exception {

        final List<MatchDto> matches = getMatches();
        final Map<Integer, MatchDto> matchMap = matches.stream()
                .collect(Collectors.toMap(MatchDto::getMatchNumber, Function.identity()));

        for (Map.Entry<Integer, Integer[]> scoreEntry : SCORES_GROUP_B.entrySet()) {

            final MatchDto match = matchMap.get(scoreEntry.getKey());

            match.setScore(scoreEntry.getValue()[0], scoreEntry.getValue()[1]);

            mvc.perform(MockMvcRequestBuilders.put("/matches/" + match.getMatchNumber())
                            .content(format(match))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header(securityConstants.HEADER_STRING, admin.getToken()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
        }

        // get again from rest api
        final List<CountryDto> countries = getCountries();
        final Map<String, CountryDto> countryMap = countries.stream()
                .collect(Collectors.toMap(CountryDto::getName, Function.identity()));

        matches.clear();
        matches.addAll(getMatches());
        matchMap.clear();
        matchMap.putAll(matches.stream()
                .collect(Collectors.toMap(MatchDto::getMatchNumber, Function.identity())));


        Assertions.assertEquals(1, countryMap.get(Spain.name).getPosition());
        Assertions.assertEquals(2, countryMap.get(Portugal.name).getPosition());
        Assertions.assertEquals(3, countryMap.get(Iran.name).getPosition());
        Assertions.assertEquals(4, countryMap.get(Morocco.name).getPosition());

        Assertions.assertArrayEquals(new int[]{3, 1, 2, 0, 6, 5, 1, 5}, standingsDetails(countryMap.get(Spain.name)));
        Assertions.assertArrayEquals(new int[]{3, 1, 2, 0, 5, 4, 1, 5}, standingsDetails(countryMap.get(Portugal.name)));
        Assertions.assertArrayEquals(new int[]{3, 1, 1, 1, 2, 2, 0, 4}, standingsDetails(countryMap.get(Iran.name)));
        Assertions.assertArrayEquals(new int[]{3, 0, 1, 2, 2, 4, -2, 1}, standingsDetails(countryMap.get(Morocco.name)));

        Assertions.assertEquals(matchMap.get(49).getAwayTeamID(), countryMap.get(Portugal.name).getID());
        Assertions.assertEquals(matchMap.get(51).getHomeTeamID(), countryMap.get(Spain.name).getID());
    }

    private List<MatchDto> getMatches() throws Exception {

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/matches/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        return parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<MatchDto>>() {});
    }

    private List<CountryDto> getCountries() throws Exception {

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/countries/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        return parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<CountryDto>>() {});
    }


}