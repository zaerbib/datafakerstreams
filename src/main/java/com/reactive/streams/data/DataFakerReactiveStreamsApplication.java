package com.reactive.streams.data;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

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
	@Qualifier("fakerExecution")
	public Executor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(500);
		executor.setMaxPoolSize(1000);
		executor.setQueueCapacity(5000);
		executor.setThreadNamePrefix("faker-");
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.initialize();
		return executor;
	}

}
