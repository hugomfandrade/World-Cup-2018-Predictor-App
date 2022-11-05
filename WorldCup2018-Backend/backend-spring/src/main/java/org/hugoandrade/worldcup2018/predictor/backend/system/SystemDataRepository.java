package org.hugoandrade.worldcup2018.predictor.backend.system;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoRepositoryBean
public interface SystemDataRepository extends CrudRepository<SystemData, String> {

    default List<SystemData> findAllAsList() {

        return StreamSupport.stream(this.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    default SystemData findOne() {
        return findAllAsList().stream().findAny().get();
    }
}