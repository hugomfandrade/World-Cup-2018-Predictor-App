package org.hugoandrade.worldcup2018.predictor.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConstants {

    public static final String SIGN_UP_URL = "/sign-up";

    @Value("${spring.jwt.secret}")
    public String SECRET;

    @Value("${spring.jwt.expiration_time}")
    public long EXPIRATION_TIME;

    @Value("${spring.jwt.token_prefix}")
    public String TOKEN_PREFIX;

    @Value("${spring.jwt.header_string}")
    public String HEADER_STRING;

    @Value("${spring.jwt.authorities_key}")
    public String AUTHORITIES_KEY;

    @Value("${spring.jwt.iterations}")
    public int iterations;

    @Value("${spring.jwt.bytes}")
    public int bytes;
}