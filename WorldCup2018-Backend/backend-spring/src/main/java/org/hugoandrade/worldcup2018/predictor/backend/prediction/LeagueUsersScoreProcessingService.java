package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountDto;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.hugoandrade.worldcup2018.predictor.backend.league.League;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueRepository;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUser;
import org.hugoandrade.worldcup2018.predictor.backend.league.LeagueUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class LeagueUsersScoreProcessingService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private LeagueRepository leagueRepository;
    @Autowired private LeagueUserRepository leagueUserRepository;

    @Autowired private ModelMapper modelMapper;

    private final LeagueUsersScoreProcessing leagueUsersScoreProcessing
            = new LeagueUsersScoreProcessing();

    private AccountDto[] getAccounts() {
        return accountRepository.findAllAsList()
                .stream()
                .map(account -> modelMapper.map(account, AccountDto.class))
                .toArray(AccountDto[]::new);
    }

    private LeagueUser[] getLeagueUsers() {
        return StreamSupport.stream(leagueUserRepository.findAll().spliterator(), false)
                .toArray(LeagueUser[]::new);
    }

    private League[] getLeagues() {
        return StreamSupport.stream(leagueRepository.findAll().spliterator(), false)
                .toArray(League[]::new);
    }

    public void resetScores() {

        this.setResetListener();

        final League[] leagues = this.getLeagues();
        final LeagueUser[] leagueUsers = this.getLeagueUsers();
        final AccountDto[] accounts = this.getAccounts();

        leagueUsersScoreProcessing.startUpdateUsersScoresSync(leagues, leagueUsers, accounts);
    }

    public void resetScoresAsync() {

        this.setResetListener();

        final League[] leagues = this.getLeagues();
        final LeagueUser[] leagueUsers = this.getLeagueUsers();
        final AccountDto[] accounts = this.getAccounts();

        leagueUsersScoreProcessing.startUpdateUsersScoresAsync(leagues, leagueUsers, accounts);
    }

    public void updateScores() {

        this.setUpdateListener();

        final League[] leagues = this.getLeagues();
        final LeagueUser[] leagueUsers = this.getLeagueUsers();
        final AccountDto[] accounts = this.getAccounts();

        leagueUsersScoreProcessing.startUpdateUsersScoresSync(leagues, leagueUsers, accounts);
    }

    public void updateScoresAsync() {

        this.setUpdateListener();

        final League[] leagues = this.getLeagues();
        final LeagueUser[] leagueUsers = this.getLeagueUsers();
        final AccountDto[] accounts = this.getAccounts();

        leagueUsersScoreProcessing.startUpdateUsersScoresAsync(leagues, leagueUsers, accounts);
    }

    private void setResetListener() {

        leagueUsersScoreProcessing.setListener(new LeagueUsersScoreProcessing.OnProcessingListener() {

            @Override
            public void onProcessingFinished(List<LeagueUser> leagueUsers) {

                for (LeagueUser leagueUser : leagueUsers) {
                    LeagueUser dbLeagueUser = leagueUserRepository.findById(leagueUser.getID()).orElse(null);
                    if (dbLeagueUser == null) continue;
                    dbLeagueUser.setRank(leagueUser.getRank());
                    leagueUserRepository.save(dbLeagueUser);
                }
            }

            @Override public void updateLeagueUser(LeagueUser leagueUser) { }
        });
    }

    private void setUpdateListener() {

        leagueUsersScoreProcessing.setListener(new LeagueUsersScoreProcessing.OnProcessingListener() {

            @Override public void onProcessingFinished(List<LeagueUser> leagueUsers) {}

            @Override
            public void updateLeagueUser(LeagueUser leagueUser) {
                LeagueUser dbLeagueUser = leagueUserRepository.findById(leagueUser.getID()).orElse(null);
                if (dbLeagueUser == null) return;
                dbLeagueUser.setRank(leagueUser.getRank());
                leagueUserRepository.save(dbLeagueUser);
            }
        });
    }
}
