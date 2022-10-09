package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataService;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PredictionScoresProcessingService {

    @Autowired PredictionRepository predictionRepository;

    @Autowired MatchesService matchesService;
    @Autowired SystemDataService systemDataService;

    private final PredictionScoresProcessing predictionScoresProcessing
            = new PredictionScoresProcessing();

    public void resetScores(Match match) {
        this.resetScores(Collections.singletonList(match));
    }

    public void resetScores(int matchNumber) {
        Match match = matchesService.getOne(matchNumber);
        if (match == null) this.resetScores(Collections.emptyList());
        this.resetScores(match);
    }

    public void resetScores() {
        this.resetScores(matchesService.getAll());
    }

    private void resetScores(List<Match> matches) {

        this.setResetListener();

        final SystemData systemData = systemDataService.getSystemData();
        final List<Prediction> predictions = predictionRepository.findAllAsList();

        for (Match match : matches) {
            predictionScoresProcessing.startUpdatePredictionScoresSync(systemData, match, predictions);
        }
    }

    public void resetScoresAsync() {

        this.setResetListener();

        final SystemData systemData = systemDataService.getSystemData();
        final List<Match> matches = matchesService.getAll();
        final List<Prediction> predictions = predictionRepository.findAllAsList();

        for (Match match : matches) {
            predictionScoresProcessing.startUpdatePredictionScores(systemData, match, predictions);
        }
    }

    public void updateScores() {

        this.setUpdateListener();

        final SystemData systemData = systemDataService.getSystemData();
        final List<Match> matches = matchesService.getAll();
        final List<Prediction> predictions = predictionRepository.findAllAsList();

        for (Match match : matches) {
            predictionScoresProcessing.startUpdatePredictionScoresSync(systemData, match, predictions);
        }
    }

    public void updateScoresAsync() {

        this.setUpdateListener();

        final SystemData systemData = systemDataService.getSystemData();
        final List<Match> matches = matchesService.getAll();
        final List<Prediction> predictions = predictionRepository.findAllAsList();

        for (Match match : matches) {
            predictionScoresProcessing.startUpdatePredictionScores(systemData, match, predictions);
        }
    }

    private void setResetListener() {

        predictionScoresProcessing.setListener(new PredictionScoresProcessing.OnProcessingListener() {

            @Override
            public void onProcessingFinished(List<Prediction> predictions) {
                for (Prediction prediction : predictions) {
                    Prediction dbPrediction = predictionRepository.findById(prediction.getID()).get();
                    predictionRepository.save(prediction);
                }
            }

            @Override public void updatePrediction(Prediction prediction) {}
        });
    }

    private void setUpdateListener() {

        predictionScoresProcessing.setListener(new PredictionScoresProcessing.OnProcessingListener() {

            @Override public void onProcessingFinished(List<Prediction> predictions) { }

            @Override
            public void updatePrediction(Prediction prediction) {
                Prediction dbPrediction = predictionRepository.findById(prediction.getID()).get();
                predictionRepository.save(prediction);
            }
        });
    }
}
