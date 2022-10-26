package org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.Admin;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AdminRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminMongoRepository extends AdminRepository, MongoRepository<Admin, String> {

}