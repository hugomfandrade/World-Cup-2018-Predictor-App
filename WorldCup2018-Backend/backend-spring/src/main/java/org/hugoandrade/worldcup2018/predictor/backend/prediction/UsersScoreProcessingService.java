package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersScoreProcessingService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private PredictionsService predictionsService;

    private final UsersScoreProcessing usersScoreProcessing
            = new UsersScoreProcessing();

    public void resetScores() {

        this.setResetListener();

        final List<Prediction> predictions = predictionsService.getAll();
        final Account[] accounts = accountRepository.findAllAsList().toArray(new Account[0]);

        usersScoreProcessing.startUpdateUsersScoresSync(predictions, accounts);
    }

    public void resetScoresAsync() {

        this.setResetListener();

        final List<Prediction> predictions = predictionsService.getAll();
        final Account[] accounts = accountRepository.findAllAsList().toArray(new Account[0]);

        usersScoreProcessing.startUpdateUsersScoresAsync(predictions, accounts);
    }

    public void updateScores() {

        this.setUpdateListener();

        final List<Prediction> predictions = predictionsService.getAll();
        final Account[] accounts = accountRepository.findAllAsList().toArray(new Account[0]);

        usersScoreProcessing.startUpdateUsersScoresSync(predictions, accounts);
    }

    public void updateScoresAsync() {

        this.setUpdateListener();

        final List<Prediction> predictions = predictionsService.getAll();
        final Account[] accounts = accountRepository.findAllAsList().toArray(new Account[0]);

        usersScoreProcessing.startUpdateUsersScoresAsync(predictions, accounts);
    }

    private void setResetListener() {

        usersScoreProcessing.setListener(new UsersScoreProcessing.OnProcessingListener() {

            @Override
            public void onProcessingFinished(List<Account> accounts) {

                for (Account account : accounts) {
                    Account dbAccount = accountRepository.findByUsername(account.getUsername());
                    accountRepository.save(account);
                }
            }

            @Override public void updateAccount(Account account) { }
        });
    }

    private void setUpdateListener() {

        usersScoreProcessing.setListener(new UsersScoreProcessing.OnProcessingListener() {

            @Override public void onProcessingFinished(List<Account> accounts) {}

            @Override
            public void updateAccount(Account account) {
                Account dbAccount = accountRepository.findByUsername(account.getUsername());
                accountRepository.save(account);
            }
        });
    }
}
