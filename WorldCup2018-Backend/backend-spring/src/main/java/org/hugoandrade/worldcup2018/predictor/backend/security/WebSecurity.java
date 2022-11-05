package org.hugoandrade.worldcup2018.predictor.backend.security;

import org.hugoandrade.worldcup2018.predictor.backend.authentication.AccountService;
import org.hugoandrade.worldcup2018.predictor.backend.security.jwt.JWTAuthenticationFilter;
import org.hugoandrade.worldcup2018.predictor.backend.security.jwt.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;

import static org.hugoandrade.worldcup2018.predictor.backend.security.SecurityConstants.SIGN_UP_URL;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SecurityConstants securityConstants;

    @Autowired
    private AccountService accountService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/auth/").permitAll() // to ping
                .antMatchers(HttpMethod.POST, "/auth/sign-up/").permitAll()
                .antMatchers(HttpMethod.GET, "/auth/login/").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/login/").permitAll()
                .antMatchers(HttpMethod.POST, "/accounts").hasAuthority("Admin")

                .antMatchers(HttpMethod.GET, "/countries").authenticated()
                .antMatchers(HttpMethod.POST, "/countries").hasAuthority("Admin")
                .antMatchers(HttpMethod.POST, "/countries/").hasAuthority("Admin")
                .antMatchers(HttpMethod.DELETE, "/countries").hasAuthority("Admin")
                .antMatchers(HttpMethod.DELETE, "/countries/").hasAuthority("Admin")
                .antMatchers(HttpMethod.GET, "/countries/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/countries/**").hasAuthority("Admin")
                .antMatchers(HttpMethod.DELETE, "/countries/**").hasAuthority("Admin")

                .antMatchers(HttpMethod.GET, "/matches").authenticated()
                .antMatchers(HttpMethod.POST, "/matches").hasAuthority("Admin")
                .antMatchers(HttpMethod.POST, "/matches/").hasAuthority("Admin")
                .antMatchers(HttpMethod.DELETE, "/matches").hasAuthority("Admin")
                .antMatchers(HttpMethod.DELETE, "/matches/").hasAuthority("Admin")
                .antMatchers(HttpMethod.GET, "/matches/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/matches/**").hasAuthority("Admin")
                .antMatchers(HttpMethod.DELETE, "/matches/**").hasAuthority("Admin")

                .antMatchers("/leagues/**").authenticated()

                .antMatchers(HttpMethod.POST, "/system-data").hasAuthority("Admin")
                .antMatchers(HttpMethod.POST, "/system-data/").hasAuthority("Admin")
                .antMatchers(HttpMethod.GET, "/system-data").permitAll()
                .antMatchers(HttpMethod.GET, "/system-data/").permitAll()
                .antMatchers(HttpMethod.POST, "/reset-all").hasAuthority("Admin")

                .antMatchers("/users/**").authenticated()

                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), securityConstants, accountService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), securityConstants))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}