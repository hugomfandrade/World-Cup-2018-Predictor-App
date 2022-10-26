package org.hugoandrade.worldcup2018.predictor.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.lang.NonNull;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

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


    //
    // SQLServer
    //
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource source = new DriverManagerDataSource();
        if (!StringUtils.isEmpty(datasourceUrl)) source.setUrl(datasourceUrl);
        if (!StringUtils.isEmpty(dbUsername)) source.setUsername(dbUsername);
        if (!StringUtils.isEmpty(dbPassword)) source.setPassword(dbPassword);
        if (!StringUtils.isEmpty(dbDriverClassName)) source.setDriverClassName(dbDriverClassName);
        return source;
    }

    //
    // MongoDB
    //

    @Value("${spring.data.mongodb.host}")
    private String mongoDbHost;

    @Value("${spring.data.mongodb.username}")
    private String mongoDbUsername;

    @Value("${spring.data.mongodb.password}")
    private String mongoDbPassword;

    @Value("${spring.data.mongodb.database}")
    private String mongoDbDatabase;

    @Bean
    public MongoClient mongo() {
        String url = "mongodb+srv://" + mongoDbUsername + ":" + mongoDbPassword + "@" +
                mongoDbHost + "/?retryWrites=true&w=majority";

        ConnectionString connectionString = new ConnectionString(url);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Autowired MongoDbFactory mongoDbFactory;

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext()) {
            @Override
            public Object convertId(Object id, @NonNull Class<?> targetType) {
                if (targetType == ObjectId.class) return super.convertId(id, targetType);
                return id == null ? null : new ObjectId((String) id);
            }
        };
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();

        return new MongoTemplate(new SimpleMongoClientDbFactory(mongo(), "test"), converter);
    }

    private MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        return new MongoCustomConversions(converters);
    }
}