package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import com.fasterxml.jackson.core.type.TypeReference;
import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataService;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchesService;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PredictionsControllerTest extends BaseControllerTest {

    @Autowired private MockMvc mvc;

    @Autowired private SystemDataService systemDataService;
    @Autowired private MatchesService matchesService;
    @Autowired private PredictionRepository predictionRepository;

    @BeforeEach
    void beforeEach() {
        SystemData systemData = new SystemData("0,1,2,4", true, ISO8601Utils.parse("2018-06-27T12:00:00Z"));
        systemDataService.setSystemData(systemData);
    }

    @Test
    void enabledMatches() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/predictions/" + "enabled-matches")
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<Match> matches = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Match>>(){});

                    Assertions.assertEquals(24, matches.size());
                });
    }

    @Test
    void getAll() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/predictions/" + user.getUserID())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<Prediction> predictions = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Prediction>>(){});

                    Assertions.assertEquals(0, predictions.size());
                });
    }

    @Test
    void insertOne() throws Exception {

        Match match;

        // get first
        match = matchesService.getAll().iterator().next();

        // get first enabled
        MvcResult matchesMvcResult = mvc.perform(MockMvcRequestBuilders.get("/predictions/enabled-matches")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andReturn();
        List<Match> enabledMatches = parse(matchesMvcResult.getResponse().getContentAsString(), new TypeReference<List<Match>>(){});
        match = enabledMatches.get(0);

        Prediction prediction = Prediction.emptyInstance(match.getMatchNumber(), user.getUserID());
        prediction.setHomeTeamGoals(0);
        prediction.setHomeTeamGoals(2);

        mvc.perform(MockMvcRequestBuilders.post("/predictions/")
                        .content(format(prediction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mvc.perform(MockMvcRequestBuilders.post("/predictions/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(prediction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Prediction resPrediction = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Prediction>(){});

                    Assertions.assertEquals(user.getUserID(), resPrediction.getUserID());
                    Assertions.assertEquals(prediction.getMatchNumber(), resPrediction.getMatchNumber());
                    Assertions.assertEquals(prediction.getHomeTeamGoals(), resPrediction.getHomeTeamGoals());
                    Assertions.assertEquals(prediction.getAwayTeamGoals(), resPrediction.getAwayTeamGoals());
                });

        // update
        prediction.setHomeTeamGoals(-1);
        prediction.setAwayTeamGoals(4);
        mvc.perform(MockMvcRequestBuilders.post("/predictions/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(prediction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Prediction resPrediction = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Prediction>(){});

                    Assertions.assertEquals(user.getUserID(), resPrediction.getUserID());
                    Assertions.assertEquals(prediction.getMatchNumber(), resPrediction.getMatchNumber());
                    Assertions.assertEquals(prediction.getHomeTeamGoals(), resPrediction.getHomeTeamGoals());
                    Assertions.assertEquals(prediction.getAwayTeamGoals(), resPrediction.getAwayTeamGoals());
                });


        // insert another
        Prediction predictionOther = Prediction.emptyInstance(enabledMatches.get(1).getMatchNumber(), user.getUserID());
        predictionOther.setHomeTeamGoals(1);
        predictionOther.setAwayTeamGoals(3);
        mvc.perform(MockMvcRequestBuilders.post("/predictions/")
                        .header(securityConstants.HEADER_STRING, user.getToken())
                        .content(format(predictionOther))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Prediction resPrediction = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Prediction>(){});

                    Assertions.assertEquals(user.getUserID(), resPrediction.getUserID());
                    Assertions.assertEquals(predictionOther.getMatchNumber(), resPrediction.getMatchNumber());
                    Assertions.assertEquals(predictionOther.getHomeTeamGoals(), resPrediction.getHomeTeamGoals());
                    Assertions.assertEquals(predictionOther.getAwayTeamGoals(), resPrediction.getAwayTeamGoals());
                });

        // insert for another
        mvc.perform(MockMvcRequestBuilders.post("/predictions/")
                        .header(securityConstants.HEADER_STRING, userOther.getToken())
                        .content(format(prediction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Prediction resPrediction = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<Prediction>(){});

                    Assertions.assertEquals(userOther.getUserID(), resPrediction.getUserID());
                    Assertions.assertEquals(prediction.getMatchNumber(), resPrediction.getMatchNumber());
                    Assertions.assertEquals(prediction.getHomeTeamGoals(), resPrediction.getHomeTeamGoals());
                    Assertions.assertEquals(prediction.getAwayTeamGoals(), resPrediction.getAwayTeamGoals());
                });

        // assert there are two predictions in repo
        Assertions.assertEquals(3, (int) predictionRepository.findAllAsList().size());

        // make sure there is one prediction
        mvc.perform(MockMvcRequestBuilders.get("/predictions/" + user.getUserID())
                        .header(securityConstants.HEADER_STRING, user.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<Prediction> predictions = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Prediction>>(){});

                    Assertions.assertEquals(2, predictions.size());
                });

        // make sure there is one prediction
        mvc.perform(MockMvcRequestBuilders.get("/predictions/" + userOther.getUserID())
                        .header(securityConstants.HEADER_STRING, userOther.getToken()))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    List<Prediction> predictions = parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Prediction>>(){});

                    Assertions.assertEquals(1, predictions.size());
                });

        // clean repo
        predictionRepository.deleteByUserID(user.getUserID());
        predictionRepository.deleteByUserID(userOther.getUserID());

        Assertions.assertEquals(0, predictionRepository.findAllAsList().size());
    }

    @Test
    void updateOne() throws Exception {
        this.insertOne();
    }
}