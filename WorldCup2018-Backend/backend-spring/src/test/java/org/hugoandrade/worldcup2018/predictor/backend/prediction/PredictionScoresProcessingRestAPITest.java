package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.LoginData;
import org.hugoandrade.worldcup2018.predictor.backend.system.Rules;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataDto;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchDto;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BiConsumerException;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BiFunctionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parseList;
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
        final SystemDataDto expectedSystemData = new SystemDataDto("0,1,2,4", true, date);

        doOn(mvc).withHeader(admin.getToken())
                .post("/system-data/", expectedSystemData)
                .andExpect(status().isOk());

        final BiConsumerException<LoginData, PredictionDto> putPrediction = (loginData, prediction) -> {

            prediction.setUserID(loginData.getUserID());

            doOn(mvc).withHeader(loginData.getToken())
                    .post("/predictions/", prediction)
                    .andExpect(status().isOk());
        };

        // for user
        //
        // correct prediction
        putPrediction.accept(user, new PredictionDto(0, 1, 4));
        // correct margin of victory
        putPrediction.accept(user, new PredictionDto(2, 2, 3));
        // correct outcome
        putPrediction.accept(user, new PredictionDto(2, 0, 19));
        // incorrect
        putPrediction.accept(user, new PredictionDto(2, 1, 20));
        // incomplete
        putPrediction.accept(user, new PredictionDto(-1, 1, 35));

        // for userOther
        //
        // correct prediction
        putPrediction.accept(userOther, new PredictionDto(3, 3, 3));
        // correct margin of victory
        putPrediction.accept(userOther, new PredictionDto(2, 1, 19));
        // correct outcome
        putPrediction.accept(userOther, new PredictionDto(0, 2, 20));
        // incorrect
        putPrediction.accept(userOther, new PredictionDto(0, 1, 35));
        // incomplete
        putPrediction.accept(userOther, new PredictionDto(0, -1, 36));
    }

    @Test
    void startUpdatePredictionScoreProcessing_GroupB_RestAPI() throws Exception {

        final List<MatchDto> matches = parseList(doOn(mvc).withHeader(user.getToken())
                        .get("/matches/")
                        .andExpect(status().isOk())
                        .andReturn(),
                MatchDto.class);

        final Map<Integer, MatchDto> matchMap = matches.stream()
                .collect(Collectors.toMap(MatchDto::getMatchNumber, Function.identity()));

        // update scores
        for (Map.Entry<Integer, Integer[]> scoreEntry : SCORES_GROUP_B.entrySet()) {

            final MatchDto match = matchMap.get(scoreEntry.getKey());

            match.setScore(scoreEntry.getValue()[0], scoreEntry.getValue()[1]);

            doOn(mvc).withHeader(admin.getToken())
                    .put("/matches/" + match.getMatchNumber(), match)
                    .andExpect(status().isOk());
        }

        // verify
        final SystemDataDto systemData = parse(doOn(mvc).withHeader(user.getToken())
                        .get("/system-data")
                        .andExpect(status().isOk())
                        .andReturn(),
                SystemDataDto.class);
        final Rules rules = systemData.getRules();

        int incorrectPrediction = rules.getRuleIncorrectPrediction();
        int correctOutcome = rules.getRuleCorrectOutcome();
        int correctMarginOfVictory = rules.getRuleCorrectMarginOfVictory();
        int correctPrediction = rules.getRuleCorrectPrediction();

        final BiFunctionException<Integer, LoginData, PredictionDto> getPrediction = (matchNumber, loginData) -> {

            return parseList(doOn(mvc).withHeader(loginData.getToken())
                            .get("/predictions/" + loginData.getUserID())
                            .andExpect(status().isOk())
                            .andReturn(),
                    PredictionDto.class)
                    .stream()
                    .filter(prediction -> prediction.getMatchNumber() == matchNumber)
                    .findAny()
                    .orElse(null);
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