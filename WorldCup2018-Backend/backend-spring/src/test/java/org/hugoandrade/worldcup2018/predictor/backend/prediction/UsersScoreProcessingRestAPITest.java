package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import com.fasterxml.jackson.core.type.TypeReference;
import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.LoginData;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchDto;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BiConsumerException;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.format;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersScoreProcessingRestAPITest extends BaseControllerTest {

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

        // update system date to before start of tournament, so that predictions can be accepted
        final Date date = ISO8601Utils.parse("2018-05-27T12:00:00Z");
        final SystemData expectedSystemData = new SystemData("0,1,2,4", true, date);

        mvc.perform(MockMvcRequestBuilders.post("/system-data/")
                        .header(securityConstants.HEADER_STRING, admin.getToken())
                        .content(format(expectedSystemData))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //
        // update predictions
        final BiConsumerException<LoginData, PredictionDto> putPrediction = (loginData, prediction) -> {

            prediction.setUserID(loginData.getUserID());

            mvc.perform(MockMvcRequestBuilders.post("/predictions/")
                            .content(format(prediction))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header(securityConstants.HEADER_STRING, loginData.getToken()))
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

        //
        // update matches
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

        final SystemData systemData = getSystemData();
        final SystemData.Rules rules = systemData.getRules();

        int incorrectPrediction = rules.getRuleIncorrectPrediction();
        int correctOutcome = rules.getRuleCorrectOutcome();
        int correctMarginOfVictory = rules.getRuleCorrectMarginOfVictory();
        int correctPrediction = rules.getRuleCorrectPrediction();
        int expectedScore = incorrectPrediction + correctOutcome + correctMarginOfVictory + correctPrediction;

        Assertions.assertEquals(0, getAccount(admin.getUsername()).getScore());
        Assertions.assertEquals(expectedScore, getAccount(user.getUsername()).getScore());
        Assertions.assertEquals(expectedScore, getAccount(userOther.getUsername()).getScore());
    }

    private SystemData getSystemData() throws Exception {

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/system-data/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        return parse(mvcResult.getResponse().getContentAsString(), SystemData.class);
    }

    private AccountDto getAccount(String username) throws Exception {

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/auth/accounts")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        return parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<AccountDto>>() {})
                .stream()
                .filter(account -> username.equals(account.getUsername()))
                .findAny()
                .orElse(null);
    }

    private List<MatchDto> getMatches() throws Exception {

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/matches/")
                        .header(securityConstants.HEADER_STRING, admin.getToken()))
                .andExpect(status().isOk())
                .andReturn();

        return parse(mvcResult.getResponse().getContentAsString(), new TypeReference<List<MatchDto>>() {});
    }
}