package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataService;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchDto;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionScoresProcessingService {

    @Autowired PredictionRepository predictionRepository;

    @Autowired MatchesService matchesService;
    @Autowired SystemDataService systemDataService;

    @Autowired ModelMapper modelMapper;

    private final PredictionScoresProcessing predictionScoresProcessing
            = new PredictionScoresProcessing();

    private List<MatchDto> getMatches() {
        return matchesService.getAll().stream()
                .map(match -> modelMapper.map(match, MatchDto.class))
                .collect(Collectors.toList());
    }

    public void resetScores(MatchDto match) {
        this.resetScores(Collections.singletonList(match));
    }

    public void resetScores(int matchNumber) {
        Match match = matchesService.getOne(matchNumber);
        if (match == null) this.resetScores(Collections.emptyList());
        this.resetScores(modelMapper.map(match, MatchDto.class));
    }

    public void resetScores() {
        this.resetScores(getMatches());
    }

    private void resetScores(List<MatchDto> matches) {

        this.setResetListener();

        final SystemData systemData = systemDataService.getSystemData();
        final List<Prediction> predictions = predictionRepository.findAllAsList();

        for (MatchDto match : matches) {
            predictionScoresProcessing.startUpdatePredictionScoresSync(systemData, match, predictions);
        }
    }

    public void resetScoresAsync() {

        this.setResetListener();

        final SystemData systemData = systemDataService.getSystemData();
        final List<MatchDto> matches = getMatches();
        final List<Prediction> predictions = predictionRepository.findAllAsList();

        for (MatchDto match : matches) {
            predictionScoresProcessing.startUpdatePredictionScores(systemData, match, predictions);
        }
    }

    public void updateScores() {

        this.setUpdateListener();

        final SystemData systemData = systemDataService.getSystemData();
        final List<MatchDto> matches = getMatches();
        final List<Prediction> predictions = predictionRepository.findAllAsList();

        for (MatchDto match : matches) {
            predictionScoresProcessing.startUpdatePredictionScoresSync(systemData, match, predictions);
        }
    }

    public void updateScoresAsync() {

        this.setUpdateListener();

        final SystemData systemData = systemDataService.getSystemData();
        final List<MatchDto> matches = getMatches();
        final List<Prediction> predictions = predictionRepository.findAllAsList();

        for (MatchDto match : matches) {
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
