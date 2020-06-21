package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByUsername(String username);
}