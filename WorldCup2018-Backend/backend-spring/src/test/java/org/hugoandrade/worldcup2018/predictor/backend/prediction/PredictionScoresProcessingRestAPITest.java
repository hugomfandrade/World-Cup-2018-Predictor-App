package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import com.fasterxml.jackson.core.type.TypeReference;
import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.LoginData;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PredictionScoresProcessingRestAPITest extends BaseControllerTest {

    private final static Map<Integer, Integer[]> SCORES_GROUP_B = new HashMap<>();
    static {
        SCORES_GROUP_B.put(4, new Integer[]{0, 1});
        SCORES_GROUP_B.put(3, new Integer[]{3, 3});
        SCORES_GROUP_B.put(19, new Integer[]{1, 0});
        SCORES_GROUP_B.put(20, new Integer[]{0, 1});
        SCORES_GROUP_B.put(35, new Integer[]{1, 1});
        SCORES_GROUP_B.put(36, new Integer[]{2, 2});
    }

    @Autowired private MockMvc mvc;

    @BeforeAll
    public void setUp() throws Exception {
        super.setUp();

        startupScript.startup();

        // update system date to before start of tournament, so that predictions can be accepted
        final Date date = ISO8601Utils.parse("2018-05-27T12:00:00Z");
        final SystemData expectedSystemData = new SystemData(null, "0,1,2,4", true, date);

        mvc.perform(MockMvcRequestBuilders.post("/system-data/")
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(expectedSystemData))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final BiConsumer<LoginData, Prediction> putPrediction = (loginData, prediction) -> {

            try {
                prediction.setUserID(prediction.getUserID());

                mvc.perform(MockMvcRequestBuilders.post("/predictions/")
                                .content(format(prediction))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(securityConstants.HEADER_STRING, loginData.getToken()))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        // for user
        //
        // correct prediction
        putPrediction.accept(user, new Prediction(0, 1, 4));
        // correct margin of victory
        putPrediction.accept(user, new Prediction(2, 2, 3));
        // correct outcome
        putPrediction.accept(user, new Prediction(2, 0, 19));
        // incorrect
        putPrediction.accept(user, new Prediction(2, 1, 20));
        // incomplete
        putPrediction.accept(user, new Prediction(-1, 1, 35));

        // for userOther
        //
        // correct prediction
        putPrediction.accept(userOther, new Prediction(3, 3, 3));
        // correct margin of victory
        putPrediction.accept(userOther, new Prediction(2, 1, 19));
        // correct outcome
        putPrediction.accept(userOther, new Prediction(0, 2, 20));
        // incorrect
        putPrediction.accept(userOther, new Prediction(0, 1, 35));
        // incomplete
        putPrediction.accept(userOther, new Prediction(0, -1, 36));
    }

    @Test
    void startUpdatePredictionScoreProcessing_GroupB_RestAPI() throws Exception {

        final List<Match> matches = parse(
                mvc.perform(MockMvcRequestBuilders.get("/matches/")
                                .header(securityConstants.HEADER_STRING, user.getToken()))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                new TypeReference<List<Match>>() {});

        final Map<Integer, Match> matchMap = matches.stream()
                .collect(Collectors.toMap(Match::getMatchNumber, Function.identity()));

        // update scores
        for (Map.Entry<Integer, Integer[]> scoreEntry : SCORES_GROUP_B.entrySet()) {

            final Match match = matchMap.get(scoreEntry.getKey());

            match.setScore(scoreEntry.getValue()[0], scoreEntry.getValue()[1]);

            mvc.perform(MockMvcRequestBuilders.put("/matches/" + match.getMatchNumber())
                            .content(format(match))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header(securityConstants.HEADER_STRING, admin.getToken()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
        }

        // verify
        final SystemData systemData = parse(
                mvc.perform(MockMvcRequestBuilders.get("/system-data")
                                .header(securityConstants.HEADER_STRING, user.getToken()))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString(),
                SystemData.class);
        final SystemData.Rules rules = systemData.getRules();

        int incorrectPrediction = rules.getRuleIncorrectPrediction();
        int correctOutcome = rules.getRuleCorrectOutcome();
        int correctMarginOfVictory = rules.getRuleCorrectMarginOfVictory();
        int correctPrediction = rules.getRuleCorrectPrediction();

        final BiFunction<Integer, LoginData, Prediction> getPrediction = (matchNumber, loginData) -> {

            try {
                return parse(
                        mvc.perform(MockMvcRequestBuilders.get("/predictions/" + loginData.getUserID())
                                        .header(securityConstants.HEADER_STRING, loginData.getToken()))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString(),
                        new TypeReference<List<Prediction>>(){})
                        .stream()
                        .filter(prediction -> prediction.getMatchNumber() == matchNumber)
                        .findAny()
                        .orElse(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Assertions.assertEquals(correctPrediction, getPrediction.apply(4, user).getScore());
        Assertions.assertEquals(correctMarginOfVictory, getPrediction.apply(3, user).getScore());
        Assertions.assertEquals(correctOutcome, getPrediction.apply(19, user).getScore());
        Assertions.assertEquals(incorrectPrediction, getPrediction.apply(20, user).getScore());
        Assertions.assertEquals(0, getPrediction.apply(35, user).getScore());

        Assertions.assertEquals(correctPrediction, getPrediction.apply(3, userOther).getScore());
        Assertions.assertEquals(correctMarginOfVictory, getPrediction.apply(19, userOther).getScore());
        Assertions.assertEquals(correctOutcome, getPrediction.apply(20, userOther).getScore());
        Assertions.assertEquals(incorrectPrediction, getPrediction.apply(35, userOther).getScore());
        Assertions.assertEquals(0, getPrediction.apply(36, userOther).getScore());
    }
}