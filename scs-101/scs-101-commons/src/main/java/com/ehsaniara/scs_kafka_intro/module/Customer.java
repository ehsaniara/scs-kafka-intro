package com.ehsaniara.scs_kafka_intro.module;

import lombok.*;

@ToString
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private int customerId;
    private String customerName;
    //.. adn more ..
}
