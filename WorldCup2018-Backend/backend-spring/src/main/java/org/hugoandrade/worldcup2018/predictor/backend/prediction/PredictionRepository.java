package org.hugoandrade.worldcup2018.predictor.backend.prediction;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoRepositoryBean
public interface PredictionRepository extends CrudRepository<Prediction, String> {

    List<Prediction> findByMatchNumber(int matchNumber);

    List<Prediction> findByUserID(String userID);

    Prediction findByUserIDAndMatchNumber(String userID, int matchNumber);

    List<Prediction> findByUserIDAndMatchNumbers(String userID, int[] matchNumbers);

    void deleteByUserID(String userID);

    default List<Prediction> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}