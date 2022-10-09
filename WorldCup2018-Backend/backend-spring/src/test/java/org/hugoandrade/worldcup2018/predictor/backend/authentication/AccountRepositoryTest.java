package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testFetchData(){
        /*Test data retrieval*/
        Iterable<Account> accounts = accountRepository.findAll();

        accounts.forEach(System.err::println);
    }
}