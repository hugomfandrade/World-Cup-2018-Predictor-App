package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoRepositoryBean
public interface AccountRepository extends CrudRepository<Account, String> {

    Account findByUsername(String username);

    List<Account> findAllByIdIn(Iterable<String> ids, Pageable pageable);

    default List<Account> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    Page<Account> findAll(Pageable pageable);

    List<Account> findAllByOrderByScoreDesc();

    List<Account> findAllByIdInOrderByScoreDesc(Iterable<String> ids);

    List<Account> findAllByLeagueID(String leagueID);

    List<Account> findAllByLeagueID(String leagueID, Pageable pageable);
}