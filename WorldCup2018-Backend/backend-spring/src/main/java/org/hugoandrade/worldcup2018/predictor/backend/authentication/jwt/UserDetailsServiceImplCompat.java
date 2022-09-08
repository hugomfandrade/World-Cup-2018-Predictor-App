package org.hugoandrade.worldcup2018.predictor.backend.authentication.jwt;

import org.hugoandrade.worldcup2018.predictor.backend.model.Account;
import org.hugoandrade.worldcup2018.predictor.backend.model.Admin;
import org.hugoandrade.worldcup2018.predictor.backend.repository.AccountRepository;
import org.hugoandrade.worldcup2018.predictor.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImplCompat implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }

        Admin admin = adminRepository.findByUserID(account.getId());

        final List<GrantedAuthority> authorities = new ArrayList<>();

        if (admin != null) {
            authorities.add(new SimpleGrantedAuthority("Admin"));
        }

        return new User(account.getUsername(), account.getSalt() + account.getPassword(), authorities);
    }
}