package com.reactive.streams.data.utils;

import com.google.common.collect.Lists;
import com.reactive.streams.data.data.DataFlow;
import lombok.experimental.UtilityClass;
import net.datafaker.Faker;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@UtilityClass
public class DataFlowGenerate {
    private static final Faker faker = new Faker();

    public DataFlow generateOnDataFlow() {
        return DataFlow.builder()
                .uniqueId(UUID.randomUUID().toString())
                .open(faker.number().randomDouble(2, 90, 200))
                .close(faker.number().randomDouble(2, 90, 200))
                .volume(faker.number().randomDouble(0, 1000, 1000000))
                .splitFactor(faker.number().randomDouble(2, 0, 1))
                .dividend(faker.number().randomDouble(2, 0, 1))
                .symbol(faker.money().currencyCode())
                .exchange(faker.money().currency())
                .date(LocalDateTime.parse(faker.date().future(1, TimeUnit.DAYS, "YYYY-MM-dd HH:mm:ss"),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public List<Document> generateNDataFlow(Integer itemNumber) {
        return IntStream.range(0, itemNumber).mapToObj(item -> fromDataFlowToDocument(generateOnDataFlow())).toList();
    }

    public <T> List<List<T>> paritionList(List<T> dataFlows, Integer chunkSize) {
        return Lists.partition(dataFlows, chunkSize);
    }

    public Document fromDataFlowToDocument(DataFlow dataFlow) {
        return new Document("uniqueId", dataFlow.getUniqueId())
                .append("open", dataFlow.getOpen())
                .append("close", dataFlow.getClose())
                .append("volume", dataFlow.getVolume())
                .append("splitFactor", dataFlow.getSplitFactor())
                .append("dividend", dataFlow.getDividend())
                .append("symbol", dataFlow.getSymbol())
                .append("exchange", dataFlow.getExchange())
                .append("date", dataFlow.getDate())
                .append("benef", dataFlow.getBenef());
    }
}
