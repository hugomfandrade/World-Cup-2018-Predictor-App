package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AdminRepository extends CrudRepository<Admin, String> {
    Admin findByUserID(String userID);
}