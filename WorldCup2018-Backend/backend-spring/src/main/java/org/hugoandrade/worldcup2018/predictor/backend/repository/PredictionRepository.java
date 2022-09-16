package org.hugoandrade.worldcup2018.predictor.backend.repository;

import org.hugoandrade.worldcup2018.predictor.backend.model.Prediction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface PredictionRepository extends CrudRepository<Prediction, String> {

    @Query("FROM Prediction p WHERE p.mMatchNo = :matchNumber")
    List<Prediction> findByMatchNumber(int matchNumber);

    @Query("FROM Prediction p WHERE p.mUserID = :userID")
    List<Prediction> findByUserID(String userID);

    @Query("FROM Prediction p WHERE p.mUserID = :userID AND p.mMatchNo = :matchNumber")
    Prediction findByUserIDAndMatchNumber(String userID, int matchNumber);

    @Query("FROM Prediction p WHERE p.mUserID = :userID AND p.mMatchNo in :matchNumbers")
    List<Prediction> findByUserIDAndMatchNumbers(String userID, int[] matchNumbers);

    @Transactional
    @Modifying
    @Query("DELETE FROM Prediction p WHERE p.mUserID = :userID")
    void deleteByUserID(String userID);
}