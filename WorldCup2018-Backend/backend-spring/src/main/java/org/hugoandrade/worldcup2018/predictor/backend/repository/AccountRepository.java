package org.hugoandrade.worldcup2018.predictor.backend.repository;

import org.hugoandrade.worldcup2018.predictor.backend.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByUsername(String username);
}