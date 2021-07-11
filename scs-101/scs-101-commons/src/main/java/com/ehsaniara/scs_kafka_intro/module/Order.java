package com.ehsaniara.scs_kafka_intro.module;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@ToString
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private UUID orderUuid;

    private String itemName;

    private OrderStatus orderStatus;
}
