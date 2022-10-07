package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeagueUserRepository extends CrudRepository<LeagueUser, String> {

    @Query("FROM LeagueUser l WHERE l.mLeagueID = :leagueID AND l.mUserID = :userID")
    LeagueUser findByUserID(String leagueID, String userID);

    @Query("FROM LeagueUser l WHERE l.mUserID = :userID")
    List<LeagueUser> findAllByUserID(String userID);

    @Query("FROM LeagueUser l WHERE l.mLeagueID = :leagueID")
    List<LeagueUser> findAllByLeagueID(String leagueID);
}