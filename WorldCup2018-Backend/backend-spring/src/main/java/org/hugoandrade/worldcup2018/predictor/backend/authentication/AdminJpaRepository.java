package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminJpaRepository extends AdminRepository, JpaRepository<Admin, String> {

}