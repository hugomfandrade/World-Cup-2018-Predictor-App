package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountJpaRepository extends AccountRepository, JpaRepository<Account, String> {

    @Query("FROM Account a INNER JOIN LeagueUser l ON l.mUserID = a.id WHERE l.mLeagueID = :leagueID")
    List<Account> findAllByLeagueID(String leagueID);

    @Query("FROM Account a INNER JOIN LeagueUser l ON l.mUserID = a.id WHERE l.mLeagueID = :leagueID")
    List<Account> findAllByLeagueID(String leagueID, Pageable pageable);

}