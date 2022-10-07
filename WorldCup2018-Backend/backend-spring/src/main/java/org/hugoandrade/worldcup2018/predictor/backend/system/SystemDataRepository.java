package org.hugoandrade.worldcup2018.predictor.backend.system;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface SystemDataRepository extends CrudRepository<SystemData, String> {

    default List<SystemData> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    default SystemData findOne() {
        return findAllAsList().stream().findAny().get();
    }
}