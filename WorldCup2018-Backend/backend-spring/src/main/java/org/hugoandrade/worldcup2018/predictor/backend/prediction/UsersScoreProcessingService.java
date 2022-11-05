package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersScoreProcessingService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private PredictionsService predictionsService;

    @Autowired private ModelMapper modelMapper;

    private final UsersScoreProcessing usersScoreProcessing
            = new UsersScoreProcessing();

    private AccountDto[] getAccounts() {
        return accountRepository.findAllAsList()
                .stream()
                .map(account -> modelMapper.map(account, AccountDto.class))
                .toArray(AccountDto[]::new);
    }

    public void resetScores() {

        this.setResetListener();

        final List<Prediction> predictions = predictionsService.getAll();
        final AccountDto[] accounts = getAccounts();

        usersScoreProcessing.startUpdateUsersScoresSync(predictions, accounts);
    }

    public void resetScoresAsync() {

        this.setResetListener();

        final List<Prediction> predictions = predictionsService.getAll();
        final AccountDto[] accounts = getAccounts();

        usersScoreProcessing.startUpdateUsersScoresAsync(predictions, accounts);
    }

    public void updateScores() {

        this.setUpdateListener();

        final List<Prediction> predictions = predictionsService.getAll();
        final AccountDto[] accounts = getAccounts();

        usersScoreProcessing.startUpdateUsersScoresSync(predictions, accounts);
    }

    public void updateScoresAsync() {

        this.setUpdateListener();

        final List<Prediction> predictions = predictionsService.getAll();
        final AccountDto[] accounts = getAccounts();

        usersScoreProcessing.startUpdateUsersScoresAsync(predictions, accounts);
    }

    private void setResetListener() {

        usersScoreProcessing.setListener(new UsersScoreProcessing.OnProcessingListener() {

            @Override
            public void onProcessingFinished(List<AccountDto> accounts) {

                for (AccountDto account : accounts) {
                    Account dbAccount = accountRepository.findByUsername(account.getUsername());
                    dbAccount.setScore(account.getScore());
                    dbAccount.setRank(account.getRank());
                    accountRepository.save(dbAccount);
                }
            }

            @Override public void updateAccount(AccountDto account) { }
        });
    }

    private void setUpdateListener() {

        usersScoreProcessing.setListener(new UsersScoreProcessing.OnProcessingListener() {

            @Override public void onProcessingFinished(List<AccountDto> accounts) {}

            @Override
            public void updateAccount(AccountDto account) {
                Account dbAccount = accountRepository.findByUsername(account.getUsername());
                dbAccount.setScore(account.getScore());
                dbAccount.setRank(account.getRank());
                accountRepository.save(dbAccount);
            }
        });
    }
}
