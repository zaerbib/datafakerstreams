package com.reactive.streams.data.data;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Jacksonized
public class DataFlow {

    private String uniqueId;
    private Double open;
    private Double close;
    private Double volume;
    private Double splitFactor;
    private Double dividend;
    private String symbol;
    private String exchange;
    private LocalDateTime date;
    private Benef benef;
}
