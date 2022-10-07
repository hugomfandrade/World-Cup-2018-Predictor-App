package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Long> {
    Admin findByUserID(String userID);
}