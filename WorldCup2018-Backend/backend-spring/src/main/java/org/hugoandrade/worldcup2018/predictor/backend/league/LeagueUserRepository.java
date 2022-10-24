package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface LeagueUserRepository extends CrudRepository<LeagueUser, String> {

    LeagueUser findByUserID(String leagueID, String userID);

    List<LeagueUser> findAllByUserID(String userID);

    List<LeagueUser> findAllByUserID(String userID, Pageable pageable);

    List<LeagueUser> findAllByLeagueID(String leagueID);

    @Deprecated
    List<Account> findAllAccountsByLeagueID(String leagueID);
}