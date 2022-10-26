package org.hugoandrade.worldcup2018.predictor.backend.repos.mongodb;

import com.mongodb.lang.NonNull;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AccountMongoRepository extends AccountRepository, MongoRepository<Account, String> {

    @Override
    @NonNull
    Page<Account> findAll(@NonNull Pageable pageable);

    // @Query("FROM Account a INNER JOIN LeagueUser l ON l.mUserID = a.id WHERE l.mLeagueID = :leagueID")
    @Deprecated
    @Query("{ $lookup: { from: LeagueUser, localField: _id, foreignField: mUserID, as: LeagueUsers," +
            " pipeline: [ { $match: { $and: [{ mLeagueID: ?0 }] } } ] } " +
            "}")
    List<Account> findAllByLeagueID(String leagueID);

    // @Query("FROM Account a INNER JOIN LeagueUser l ON l.mUserID = a.id WHERE l.mLeagueID = :leagueID")
    @Deprecated
    @Query("{ $lookup: { from: LeagueUser, localField: _id, foreignField: mUserID, as: LeagueUsers," +
            "            pipeline: [ { $match: { $and: [{ mLeagueID: ?0 }] } } ] } " +
            "}")
    List<Account> findAllByLeagueID(String leagueID, Pageable pageable);

}