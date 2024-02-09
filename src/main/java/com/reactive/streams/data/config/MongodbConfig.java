package com.reactive.streams.data.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongodbConfig {

    @Bean
    public MongoDatabase mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://zaerbib:admin@tuto-cluster-1.vlw5dws.mongodb.net/datafakerdb?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient client = MongoClients.create(settings);
        return client.getDatabase("datafakerdb");
    }
}
