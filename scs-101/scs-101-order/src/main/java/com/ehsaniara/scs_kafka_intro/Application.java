package com.ehsaniara.scs_kafka_intro;

import com.ehsaniara.scs_kafka_intro.module.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;

@SpringBootApplication
public class Application {

    final static String STATE_STORE_NAME = "scs-101-order-events";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Serde<Order> orderJsonSerde() {
        return new JsonSerde<>(Order.class, new ObjectMapper());
    }

}
