package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeagueJpaRepository extends LeagueRepository, JpaRepository<League, String> {

    @Query("FROM League l WHERE l.mID = :leagueID AND l.mAdminID = :adminID")
    League findByAdminID(String leagueID, String adminID);

    @Query("FROM League l WHERE l.mCode = :code")
    League findByCode(String code);

    @Query("FROM League l INNER JOIN LeagueUser u ON l.mID = u.mLeagueID WHERE u.mUserID = :userID")
    List<League> findAllByUserID(String userID);
}