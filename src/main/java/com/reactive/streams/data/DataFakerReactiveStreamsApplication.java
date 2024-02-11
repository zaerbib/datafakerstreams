package com.reactive.streams.data;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.EnableMongock;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableMongock
@SpringBootApplication
public class DataFakerReactiveStreamsApplication {
    private final MongoDatabase mongoDatabase;

    public DataFakerReactiveStreamsApplication(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public static void main(String[] args) {
        SpringApplication.run(DataFakerReactiveStreamsApplication.class, args);
    }

    @Bean
    public MongoCollection<Document> getCollection() {
        return mongoDatabase.getCollection("dataFlowAsync");
    }

    @Bean
    public Executor executor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public ConnectionDriver connectionDriver(MongoClient mongoClient) {
        MongoReactiveDriver driver = MongoReactiveDriver.withDefaultLock(mongoClient, "datafakerdb");
        driver.setLockRepositoryName("mongockLock");
        return driver;
    }
}
