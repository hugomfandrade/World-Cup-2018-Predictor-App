package org.hugoandrade.worldcup2018.predictor.backend.league;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface LeagueRepository extends CrudRepository<League, String> {

    League findByAdminID(String leagueID, String adminID);

    League findByCode(String code);

    List<League> findAllByUserID(String userID);
}