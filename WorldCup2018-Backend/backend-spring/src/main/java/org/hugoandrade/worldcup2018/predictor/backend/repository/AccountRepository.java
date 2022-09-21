package org.hugoandrade.worldcup2018.predictor.backend.repository;

import org.hugoandrade.worldcup2018.predictor.backend.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findByUsername(String username);

    default List<Account> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}