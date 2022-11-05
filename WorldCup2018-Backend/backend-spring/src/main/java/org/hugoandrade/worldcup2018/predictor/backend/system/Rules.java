package org.hugoandrade.worldcup2018.predictor.backend.system;

public class Rules {

    private int mRuleCorrectPrediction;
    private int mRuleCorrectMarginOfVictory;
    private int mRuleCorrectOutcome;
    private int mRuleIncorrectPrediction;

    public Rules() {}

    public Rules(int incorrectPrediction,
                 int correctOutcome,
                 int correctMarginOfVictory,
                 int correctPrediction) {
        mRuleIncorrectPrediction = incorrectPrediction;
        mRuleCorrectOutcome = correctOutcome;
        mRuleCorrectMarginOfVictory = correctMarginOfVictory;
        mRuleCorrectPrediction = correctPrediction;
    }

    public int getRuleCorrectPrediction() {
        return mRuleCorrectPrediction;
    }

    public int getRuleCorrectOutcome() {
        return mRuleCorrectOutcome;
    }

    public int getRuleCorrectMarginOfVictory() {
        return mRuleCorrectMarginOfVictory;
    }

    public int getRuleIncorrectPrediction() {
        return mRuleIncorrectPrediction;
    }
}