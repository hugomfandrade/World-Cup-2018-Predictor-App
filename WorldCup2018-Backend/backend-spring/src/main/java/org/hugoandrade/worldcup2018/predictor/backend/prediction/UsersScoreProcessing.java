package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsersScoreProcessing {

    private final static String TAG = UsersScoreProcessing.class.getSimpleName();

    private OnProcessingListener mOnProcessingFinished;

    private UpdateScoreProcessing mTask;

    private ExecutorService mExecutors;

    public void setListener(OnProcessingListener onProcessingListener) {
        mOnProcessingFinished = onProcessingListener;
    }

    public void startUpdateUsersScoresSync(List<Prediction> predictions, AccountDto... accounts) {
        // Do processing synchronously
        mTask = new UpdateScoreProcessing(predictions, Arrays.asList(accounts), mOnProcessingFinished);
        mTask.run();
    }

    public void startUpdateUsersScoresAsync(List<Prediction> predictions, AccountDto... accounts) {
        // Do processing asynchronously
        mTask = new UpdateScoreProcessing(predictions, Arrays.asList(accounts), mOnProcessingFinished);
        mExecutors = Executors.newCachedThreadPool();
        mExecutors.submit(mTask);
    }

    public void cancel() {
        if (mExecutors != null) mExecutors.shutdownNow();
        mExecutors = null;
        mTask = null;
    }

    public static class UpdateScoreProcessing implements Runnable {

        private final WeakReference<OnProcessingListener> mOnProcessingListener;
        private final List<AccountDto> mAccounts;
        private final List<Prediction> mPredictions;

        UpdateScoreProcessing(List<Prediction> predictions, List<AccountDto> accounts, OnProcessingListener onProcessingListener) {
            mOnProcessingListener = new WeakReference<>(onProcessingListener);
            mAccounts = accounts;
            mPredictions = predictions;
        }

        @Override
        public void run() {

            for (AccountDto account : mAccounts) {

                final int score = mPredictions.stream()
                        .filter(prediction -> account.getId().equals(prediction.getUserID()))
                        .mapToInt(Prediction::getScore)
                        .filter(predictionScore -> predictionScore > 0)
                        .sum();

                int previousScore = account.getScore();
                account.setScore(score);

                // notify if different
                if (previousScore != score) {
                    Optional.ofNullable(mOnProcessingListener.get())
                            .ifPresent(l -> l.updateAccount(account));
                }
            }

            Optional.ofNullable(mOnProcessingListener.get())
                    .ifPresent(l -> l.onProcessingFinished(mAccounts));
        }
    }

    public interface OnProcessingListener {
        void onProcessingFinished(List<AccountDto> accounts);
        void updateAccount(AccountDto account);
    }
}
