package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.hugoandrade.worldcup2018.predictor.backend.tournament.MatchUtils;
import org.hugoandrade.worldcup2018.predictor.backend.tournament.Match;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PredictionScoresProcessing {

    private final static String TAG = PredictionScoresProcessing.class.getSimpleName();

    private final OnProcessingListener mOnProcessingFinished;

    private UpdateScoreProcessing mTask;

    private ExecutorService mExecutors;

    public PredictionScoresProcessing(OnProcessingListener onProcessingListener) {
        mOnProcessingFinished = onProcessingListener;
    }

    public void startUpdatePredictionScores(SystemData systemData, Match match, List<Prediction> predictions) {
        // Do processing asynchronously
        mTask = new UpdateScoreProcessing(systemData, mOnProcessingFinished, match, predictions);
        mExecutors = Executors.newCachedThreadPool();
        mExecutors.submit(mTask);
    }

    public void cancel() {
        if (mExecutors != null) mExecutors.shutdownNow();
        mExecutors = null;
        mTask = null;
    }

    public void startUpdatePredictionScoresSync(SystemData systemData, Match match, List<Prediction> predictions) {
        // Do processing synchronously
        mTask = new UpdateScoreProcessing(systemData, mOnProcessingFinished, match, predictions);
        mTask.run();
    }

    public static class UpdateScoreProcessing implements Runnable {

        private final WeakReference<OnProcessingListener> mOnProcessingListener;
        private final Match mMatch;
        private final List<Prediction> mPredictions;
        private final SystemData mSystemData;

        UpdateScoreProcessing(SystemData systemData, OnProcessingListener onProcessingListener, Match match, List<Prediction> predictions) {
            mSystemData = systemData;
            mOnProcessingListener = new WeakReference<>(onProcessingListener);
            mMatch = match;
            mPredictions = predictions;
        }

        @Override
        public void run() {

            final List<Prediction> predictions = mPredictions.stream()
                    .filter(prediction -> prediction.getMatchNumber() == mMatch.getMatchNumber())
                    .collect(Collectors.toList());

            for (Prediction prediction : predictions) {

                int previousScore = prediction.getScore();
                int newScore = computeScore(prediction);
                prediction.setScore(newScore);

                // notify if different
                if (previousScore != newScore) {
                    Optional.ofNullable(mOnProcessingListener.get())
                            .ifPresent(l -> l.updatePrediction(prediction));
                }
            }


            Optional.ofNullable(mOnProcessingListener.get())
                    .ifPresent(l -> l.onProcessingFinished(predictions));
        }

        private int computeScore(Prediction prediction) {

            final Match match = mMatch;
            final SystemData.Rules rules = mSystemData.getRules();

            int incorrectPrediction = rules.getRuleIncorrectPrediction();
            int correctOutcome = rules.getRuleCorrectOutcome();
            int correctMarginOfVictory = rules.getRuleCorrectMarginOfVictory();
            int correctPrediction = rules.getRuleCorrectPrediction();

            if (!MatchUtils.isMatchPlayed(match)) return -1;

            if (!MatchUtils.isPredictionSet(prediction)) return incorrectPrediction;

            // Both (match and prediction) home teams win
            if ((MatchUtils.didHomeTeamWin(match) && MatchUtils.didPredictHomeTeamWin(prediction)) ||
                    (MatchUtils.didAwayTeamWin(match) && MatchUtils.didPredictAwayTeamWin(prediction))) {
                if (MatchUtils.isPredictionCorrect(match, prediction))
                    return correctPrediction;
                else if (MatchUtils.isMarginOfVictoryCorrect(match, prediction))
                    return correctMarginOfVictory;
                else
                    return correctOutcome;
            }
            else if (MatchUtils.didTeamsTied(match) && MatchUtils.didPredictTie(prediction) && !MatchUtils.wasThereAPenaltyShootout(match)) {
                if (MatchUtils.isPredictionCorrect(match, prediction))
                    return correctPrediction;
                else
                    return correctMarginOfVictory;
            }
            else if (MatchUtils.didTeamsTied(match) && MatchUtils.wasThereAPenaltyShootout(match)) {
                if (MatchUtils.didHomeTeamWinByPenaltyShootout(match) && MatchUtils.didPredictHomeTeamWin(prediction)) {
                    return correctOutcome;
                }
                if (MatchUtils.didAwayTeamWinByPenaltyShootout(match) && MatchUtils.didPredictAwayTeamWin(prediction)) {
                    return correctOutcome;
                }
            }
            return incorrectPrediction;
        }
    }

    public interface OnProcessingListener {
        void onProcessingFinished(List<Prediction> predictions);
        void updatePrediction(Prediction prediction);
    }
}
