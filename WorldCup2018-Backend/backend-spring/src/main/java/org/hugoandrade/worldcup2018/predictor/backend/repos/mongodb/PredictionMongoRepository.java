package org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb;

import org.hugoandrade.worldcup2018.predictor.backend.prediction.Prediction;
import org.hugoandrade.worldcup2018.predictor.backend.prediction.PredictionRepository;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PredictionMongoRepository extends PredictionRepository, MongoRepository<Prediction, String> {

    @Query("{ 'mMatchNo' : ?0 }")
    List<Prediction> findByMatchNumber(int matchNumber);

    @Query("{ 'mUserID' : ?0 }")
    List<Prediction> findByUserID(String userID);

    @Query("{ 'mUserID' : ?0 , 'mMatchNo' : ?1 }")
    Prediction findByUserIDAndMatchNumber(String userID, int matchNumber);

    @Query("{ 'mUserID' : ?0 , 'mMatchNo' : { $in: ?1 } }")
    List<Prediction> findByUserIDAndMatchNumbers(String userID, int[] matchNumbers);

    @DeleteQuery("{ 'mUserID' : ?0 }")
    void deleteByUserID(String userID);
}