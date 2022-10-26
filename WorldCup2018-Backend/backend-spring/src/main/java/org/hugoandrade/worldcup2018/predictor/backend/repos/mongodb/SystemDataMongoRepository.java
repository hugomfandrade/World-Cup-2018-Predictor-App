package org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb;

import org.hugoandrade.worldcup2018.predictor.backend.system.SystemData;
import org.hugoandrade.worldcup2018.predictor.backend.system.SystemDataRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemDataMongoRepository extends SystemDataRepository, MongoRepository<SystemData, String> {

}