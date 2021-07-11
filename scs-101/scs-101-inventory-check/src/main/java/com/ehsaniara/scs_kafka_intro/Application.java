package com.ehsaniara.scs_kafka_intro;

import com.ehsaniara.scs_kafka_intro.module.Order;
import com.ehsaniara.scs_kafka_intro.module.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;
import java.util.function.Function;

@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<KStream<UUID, Order>, KStream<UUID, Order>> inventoryCheck() {
        return input -> input
                .peek((key, value) -> value.setOrderStatus(OrderStatus.INVENTORY_CHECKING))
                .peek((uuid, order) -> log.debug("Order {} is getting checked in the inventory", uuid))
                .map(KeyValue::new);
    }
}
