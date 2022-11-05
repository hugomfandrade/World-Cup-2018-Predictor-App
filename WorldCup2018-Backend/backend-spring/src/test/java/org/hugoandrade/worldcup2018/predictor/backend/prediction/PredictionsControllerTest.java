package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.codehaus.jackson.map.util.ISO8601Utils;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataService;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchDto;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchesService;
import org.hugoandrade.worldcup2018.predictor.backend.utils.BaseControllerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hugoandrade.worldcup2018.predictor.backend.utils.ListResultMatchers.list;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.ObjResultMatchers.obj;
import static org.hugoandrade.worldcup2018.predictor.backend.utils.QuickParserUtils.parseList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PredictionsControllerTest extends BaseControllerTest {

    @Autowired private MockMvc mvc;

    @Autowired private SystemDataService systemDataService;
    @Autowired private MatchesService matchesService;
    @Autowired private PredictionRepository predictionRepository;

    @Autowired private ModelMapper modelMapper;

    @BeforeEach
    void beforeEach() {
        SystemData systemData = new SystemData("0,1,2,4", true, ISO8601Utils.parse("2018-06-27T12:00:00Z"));
        systemDataService.setSystemData(systemData);
    }

    @Test
    void enabledMatches() throws Exception {

        doOn(mvc).withHeader(user.getToken())
                .get("/predictions/enabled-matches")
                .andExpect(status().isOk())
                .andExpect(list(MatchDto.class).assertSize(24));
    }

    @Test
    void getAll() throws Exception {

        doOn(mvc).withHeader(user.getToken())
                .get("/predictions/" + user.getUserID())
                .andExpect(status().isOk())
                .andExpect(list(PredictionDto.class).assertSize(0));
    }

    @Test
    void insertOne() throws Exception {

        // get first enabled
        List<MatchDto> enabledMatches = parseList(doOn(mvc).withHeader(admin.getToken())
                .get("/predictions/enabled-matches")
                .andReturn(), MatchDto.class);
        MatchDto match = enabledMatches.get(0);

        PredictionDto prediction = PredictionDto.emptyInstance(match.getMatchNumber(), user.getUserID());
        prediction.setHomeTeamGoals(0);
        prediction.setHomeTeamGoals(2);

        doOn(mvc).post("/predictions/", prediction)
                .andExpect(status().is4xxClientError());

        doOn(mvc).withHeader(user.getToken())
                .post("/predictions/", prediction)
                .andExpect(status().isOk())
                .andExpect(obj(PredictionDto.class).addDo((resPrediction) -> {
                    Assertions.assertEquals(user.getUserID(), resPrediction.getUserID());
                    Assertions.assertEquals(prediction.getMatchNumber(), resPrediction.getMatchNumber());
                    Assertions.assertEquals(prediction.getHomeTeamGoals(), resPrediction.getHomeTeamGoals());
                    Assertions.assertEquals(prediction.getAwayTeamGoals(), resPrediction.getAwayTeamGoals());
                }));

        // update
        prediction.setHomeTeamGoals(-1);
        prediction.setAwayTeamGoals(4);
        doOn(mvc).withHeader(user.getToken())
                .post("/predictions/", prediction)
                .andExpect(status().isOk())
                .andExpect(obj(PredictionDto.class).addDo((resPrediction) -> {
                    Assertions.assertEquals(user.getUserID(), resPrediction.getUserID());
                    Assertions.assertEquals(prediction.getMatchNumber(), resPrediction.getMatchNumber());
                    Assertions.assertEquals(prediction.getHomeTeamGoals(), resPrediction.getHomeTeamGoals());
                    Assertions.assertEquals(prediction.getAwayTeamGoals(), resPrediction.getAwayTeamGoals());
                }));


        // insert another
        PredictionDto predictionOther = PredictionDto.emptyInstance(enabledMatches.get(1).getMatchNumber(), user.getUserID());
        predictionOther.setHomeTeamGoals(1);
        predictionOther.setAwayTeamGoals(3);
        doOn(mvc).withHeader(user.getToken())
                .post("/predictions/", predictionOther)
                .andExpect(status().isOk())
                .andExpect(obj(PredictionDto.class).addDo((resPrediction) -> {
                    Assertions.assertEquals(user.getUserID(), resPrediction.getUserID());
                    Assertions.assertEquals(predictionOther.getMatchNumber(), resPrediction.getMatchNumber());
                    Assertions.assertEquals(predictionOther.getHomeTeamGoals(), resPrediction.getHomeTeamGoals());
                    Assertions.assertEquals(predictionOther.getAwayTeamGoals(), resPrediction.getAwayTeamGoals());
                }));

        // insert for another
        doOn(mvc).withHeader(userOther.getToken())
                .post("/predictions/", prediction)
                .andExpect(status().isOk())
                .andExpect(obj(PredictionDto.class).addDo((resPrediction) -> {
                    Assertions.assertEquals(userOther.getUserID(), resPrediction.getUserID());
                    Assertions.assertEquals(prediction.getMatchNumber(), resPrediction.getMatchNumber());
                    Assertions.assertEquals(prediction.getHomeTeamGoals(), resPrediction.getHomeTeamGoals());
                    Assertions.assertEquals(prediction.getAwayTeamGoals(), resPrediction.getAwayTeamGoals());
                }));

        // assert there are two predictions in repo
        Assertions.assertEquals(3, predictionRepository.findAllAsList().size());

        // make sure there is one prediction
        doOn(mvc).withHeader(user.getToken())
                .get("/predictions/" + user.getUserID())
                .andExpect(status().isOk())
                .andExpect(list(PredictionDto.class).assertSize(2));

        // make sure there is one prediction
        doOn(mvc).withHeader(userOther.getToken())
                .get("/predictions/" + userOther.getUserID())
                .andExpect(status().isOk())
                .andExpect(list(PredictionDto.class).assertSize(1));

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