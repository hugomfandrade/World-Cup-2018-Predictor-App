package org.hugoandrade.worldcup2018.predictor.backend.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang.StringUtils;
import org.hugoandrade.worldcup2018.predictor.backend.security.SecurityConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public final SecurityConstants securityConstants;

    public JWTAuthorizationFilter(AuthenticationManager authManager, SecurityConstants securityConstants) {
        super(authManager);

        this.securityConstants = securityConstants;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(securityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(securityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(securityConstants.HEADER_STRING);
        if (token != null) {
            // parse the token.
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(securityConstants.SECRET.getBytes()))
                    .build()
                    .verify(token.replace(securityConstants.TOKEN_PREFIX + "::", ""));

            String user = decodedJWT.getSubject();
            String role = decodedJWT.getClaim(securityConstants.AUTHORITIES_KEY).asString();

            List<GrantedAuthority> authorities = Stream
                    .of(Optional.ofNullable(role)
                            .filter(StringUtils::isNotEmpty)
                            .map(s -> s.split(","))
                            .orElse(new String[0]))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }
            return null;
        }
        return null;
    }
}