package org.hugoandrade.worldcup2018.predictor.backend.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassName;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource source = new DriverManagerDataSource();
        if (!StringUtils.isEmpty(datasourceUrl)) source.setUrl(datasourceUrl);
        if (!StringUtils.isEmpty(dbUsername)) source.setUsername(dbUsername);
        if (!StringUtils.isEmpty(dbPassword)) source.setPassword(dbPassword);
        if (!StringUtils.isEmpty(dbDriverClassName)) source.setDriverClassName(dbDriverClassName);
        return source;
    }
}