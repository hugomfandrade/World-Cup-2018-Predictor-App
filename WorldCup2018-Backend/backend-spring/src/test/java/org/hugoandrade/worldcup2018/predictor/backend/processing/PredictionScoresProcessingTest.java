package org.hugoandrade.worldcup2018.predictor.backend.processing;

import org.hugoandrade.worldcup2018.predictor.backend.controller.BaseControllerTest;
import org.hugoandrade.worldcup2018.predictor.backend.model.LoginData;
import org.hugoandrade.worldcup2018.predictor.backend.model.Match;
import org.hugoandrade.worldcup2018.predictor.backend.model.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.model.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.repository.MatchRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.PredictionRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.SystemDataRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PredictionScoresProcessingTest extends BaseControllerTest {

    @Autowired SystemDataRepository systemDataRepository;
    @Autowired MatchRepository matchRepository;
    @Autowired PredictionRepository predictionRepository;

    private final static Map<Integer, Integer[]> SCORES_GROUP_B = new HashMap<>();
    static {
        SCORES_GROUP_B.put(4, new Integer[]{0, 1});
        SCORES_GROUP_B.put(3, new Integer[]{3, 3});
        SCORES_GROUP_B.put(19, new Integer[]{1, 0});
        SCORES_GROUP_B.put(20, new Integer[]{0, 1});
        SCORES_GROUP_B.put(35, new Integer[]{1, 1});
        SCORES_GROUP_B.put(36, new Integer[]{2, 2});
    }

    @BeforeAll
    public void setUp() throws Exception {
        super.setUp();

        startupScript.startup();

        // for user
        //
        // correct prediction
        predictionRepository.save(new Prediction(0, 1, 4, user.getUserID()));
        // correct margin of victory
        predictionRepository.save(new Prediction(2, 2, 3, user.getUserID()));
        // correct outcome
        predictionRepository.save(new Prediction(2, 0, 19, user.getUserID()));
        // incorrect
        predictionRepository.save(new Prediction(2, 1, 20, user.getUserID()));
        // incomplete
        predictionRepository.save(new Prediction(-1, 1, 35, user.getUserID()));

        // for userOther
        //
        // correct prediction
        predictionRepository.save(new Prediction(3, 3, 3, userOther.getUserID()));
        // correct margin of victory
        predictionRepository.save(new Prediction(2, 1, 19, userOther.getUserID()));
        // correct outcome
        predictionRepository.save(new Prediction(0, 2, 20, userOther.getUserID()));
        // incorrect
        predictionRepository.save(new Prediction(0, 1, 35, userOther.getUserID()));
        // incomplete
        predictionRepository.save(new Prediction(0, -1, 36, userOther.getUserID()));
    }

    @Test
    void startUpdatePredictionScoreProcessing_GroupB() {

        final SystemData systemData = systemDataRepository.findAllAsList().stream().findFirst().get();
        final SystemData.Rules rules = systemData.getRules();

        int incorrectPrediction = rules.getRuleIncorrectPrediction();
        int correctOutcome = rules.getRuleCorrectOutcome();
        int correctMarginOfVictory = rules.getRuleCorrectMarginOfVictory();
        int correctPrediction = rules.getRuleCorrectPrediction();

        final List<Match> matches = matchRepository.findAllAsList();
        final Map<Integer, Match> matchMap = matches.stream()
                .collect(Collectors.toMap(Match::getMatchNumber, Function.identity()));

        final Consumer<Match> updatePredictionScoreFunction = match -> {

            final PredictionScoresProcessing scoresProcessing = new PredictionScoresProcessing(new PredictionScoresProcessing.OnProcessingListener() {

                @Override public void onProcessingFinished(List<Prediction> predictions) {}

                @Override
                public void updatePrediction(Prediction prediction) {
                    Prediction dbPrediction = predictionRepository.findById(prediction.getID()).get();
                    Prediction savedPrediction = predictionRepository.save(prediction);
                }
            });
            scoresProcessing.startUpdatePredictionScoresSync(systemData, match, predictionRepository.findByMatchNumber(match.getMatchNumber()));
        };
        // update scores
        for (Map.Entry<Integer, Integer[]> scoreEntry : SCORES_GROUP_B.entrySet()) {

            final Match match = matchMap.get(scoreEntry.getKey());

            match.setScore(scoreEntry.getValue()[0], scoreEntry.getValue()[1]);

            matchRepository.save(match);

            updatePredictionScoreFunction.accept(match);
        }

        final BiFunction<Integer, LoginData, Prediction> getPrediction = (matchNumber, loginData) -> predictionRepository.findByUserIDAndMatchNumber(loginData.getUserID(), matchNumber);

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