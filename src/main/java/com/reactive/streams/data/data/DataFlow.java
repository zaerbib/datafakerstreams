package com.reactive.streams.data.data;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Jacksonized
public class DataFlow {

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
