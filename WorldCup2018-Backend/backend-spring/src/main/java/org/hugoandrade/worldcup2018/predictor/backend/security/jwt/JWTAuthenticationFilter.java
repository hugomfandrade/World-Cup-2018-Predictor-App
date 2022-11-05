package org.hugoandrade.worldcup2018.predictor.backend.security.jwt;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountService;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.LoginData;
import org.hugoandrade.worldcup2018.predictor.backend.security.SecurityConstants;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final SecurityConstants securityConstants;

    private final AuthenticationManager authenticationManager;

    private final AccountService accountService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, SecurityConstants securityConstants, AccountService accountService) {
        this.authenticationManager = authenticationManager;
        this.securityConstants = securityConstants;
        this.accountService = accountService;

        this.setRequiresAuthenticationRequestMatcher(new OrRequestMatcher(
                new AntPathRequestMatcher("/auth/login"),
                new AntPathRequestMatcher("/auth/Login")
        ));

        this.setPostOnly(true);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        if (!req.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + req.getMethod());
        }
        try {
            ServletInputStream inputStream = req.getInputStream();
            if (inputStream.available() == 0) throw new AuthenticationCredentialsNotFoundException("credentials not provided");
            LoginData creds = new ObjectMapper().readValue(inputStream, LoginData.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        final String username = ((User) auth.getPrincipal()).getUsername();
        final Collection<GrantedAuthority> authorities = ((User) auth.getPrincipal()).getAuthorities();
        final String userID = accountService.getByUsername(username).getId();

        String token = JWT.create()
                .withSubject(username)
                .withSubject(userID)
                .withExpiresAt(new Date(System.currentTimeMillis() + securityConstants.EXPIRATION_TIME))
                .withClaim(securityConstants.AUTHORITIES_KEY, authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .sign(HMAC512(securityConstants.SECRET.getBytes()));
        res.addHeader(securityConstants.HEADER_STRING, securityConstants.TOKEN_PREFIX + "::" + token);

        LoginData loginData = new LoginData();
        loginData.setUsername(((User) auth.getPrincipal()).getUsername());
        loginData.setToken(token);
        loginData.setUserID(userID);

        res.resetBuffer();
        res.getOutputStream()
                .print(new ObjectMapper().writeValueAsString(loginData));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.err.println("unsuccessfulAuthentication " + request + " - " + failed.getMessage());
        super.unsuccessfulAuthentication(request, response, failed);
    }
}