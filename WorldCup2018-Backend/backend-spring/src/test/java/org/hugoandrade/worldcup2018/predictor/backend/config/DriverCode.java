package org.hugoandrade.worldcup2018.predictor.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ClusterDescription;
import org.hugoandrade.worldcup2018.predictor.backend.authentication.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DriverCode {

    @Value("${spring.data.mongodb.host}")
    private String mongoDbHost;

    @Value("${spring.data.mongodb.username}")
    private String mongoDbUsername;

    @Value("${spring.data.mongodb.password}")
    private String mongoDbPassword;

    @Value("${spring.data.mongodb.database}")
    private static String mongoDbDatabase;

    @Value("${spring.data.mongodb.database}")
    public void setDatabase(String name){
        mongoDbDatabase = name;
    }

    private String url = "mongodb+srv://" + mongoDbUsername + ":" + mongoDbPassword + "@" +
                mongoDbHost + "/?retryWrites=true&w=majority";


    public static void main(String... args) {
        ApplicationContext context = SpringApplication.run(DriverCode.class, args);
        DriverCode p = context.getBean(DriverCode.class);
        p.start();
    }

    private DriverCode start() {

        ConnectionString connectionString = new ConnectionString(url);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("test");

        // MongoCollection<Document> collection = database.getCollection("testCol");
        // BsonDocument filter = new BsonDocument();
        // collection.countDocuments(filter);
        List<String> i = StreamSupport.stream(mongoClient.listDatabaseNames().spliterator(), false)
                .collect(Collectors.toList());
        ClusterDescription o1 = mongoClient.getClusterDescription();
        List<String> o = StreamSupport.stream(database.listCollectionNames().spliterator(), false)
                .collect(Collectors.toList());

        mongoClient.close();

        return this;
    }

    public interface UserRepository extends MongoRepository<Account, String> {
        //
    }
}
