package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeagueUserJpaRepository extends LeagueUserRepository, JpaRepository<LeagueUser, String> {

    @Query("FROM LeagueUser l WHERE l.mLeagueID = :leagueID AND l.mUserID = :userID")
    LeagueUser findByUserID(String leagueID, String userID);

    @Query("FROM LeagueUser l WHERE l.mUserID = :userID")
    List<LeagueUser> findAllByUserID(String userID);

    @Query("FROM LeagueUser l WHERE l.mUserID = :userID")
    List<LeagueUser> findAllByUserID(String userID, Pageable pageable);

    @Query("FROM LeagueUser l WHERE l.mLeagueID = :leagueID")
    List<LeagueUser> findAllByLeagueID(String leagueID);

    @Deprecated
    @Query(value = "SELECT a.* FROM league_user l INNER JOIN account a ON l.m_userid = a.id WHERE l.m_leagueid = :leagueID",
            nativeQuery = true)
    List<Account> findAllAccountsByLeagueID(String leagueID);
}