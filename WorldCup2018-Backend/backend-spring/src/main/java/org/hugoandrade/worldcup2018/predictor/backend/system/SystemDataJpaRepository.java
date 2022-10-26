package org.hugoandrade.worldcup2018.predictor.backend.system;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemDataJpaRepository extends SystemDataRepository, JpaRepository<SystemData, String> {

}