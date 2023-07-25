package com.ehsaniara.scs_kafka_intro;

import com.ehsaniara.scs_kafka_intro.module.Order;
import com.ehsaniara.scs_kafka_intro.module.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
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
    @SuppressWarnings("unchecked")
    public Function<KStream<UUID, Order>, KStream<UUID, Order>[]> orderProcess() {

        Predicate<UUID, Order> isOrderMadePredicate = (k, v) -> v.orderStatus().equals(OrderStatus.PENDING);
        Predicate<UUID, Order> isInventoryCheckedPredicate = (k, v) -> v.orderStatus().equals(OrderStatus.INVENTORY_CHECKING);
        Predicate<UUID, Order> isShippedPredicate = (k, v) -> v.orderStatus().equals(OrderStatus.SHIPPED);

        return input -> input
                .map((uuid, order) -> {
                    try {
                        //create fake delay
                        Thread.sleep(5_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new KeyValue<>(uuid, order);
                })
                .map(KeyValue::new)
                .split()
                .branch(isOrderMadePredicate)
                .branch(isInventoryCheckedPredicate)
                .branch(isShippedPredicate)
                .noDefaultBranch().values()
                .stream()
                .map(a -> a.mapValues((readOnlyKey, value) -> value))
                .toArray(KStream[]::new);
    }
}
