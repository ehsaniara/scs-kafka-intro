package com.ehsaniara.scs_kafka_intro.scs1002;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements OrderTopology {

    private final InteractiveQueryService interactiveQueryService;

    private final Serde<Order> orderJsonSerde;

    @Value("${spring.cloud.stream.bindings.orderAggConsumer-in-0.destination}")
    private String orderTopic;

    @Value("${spring.cloud.stream.kafka.streams.binder.brokers}")
    private String bootstrapServer;

    public Function<UUID, OrderStatus> statusCheck() {
        return orderUuid -> {
            final ReadOnlyKeyValueStore<UUID, String> store =
                    interactiveQueryService.getQueryableStore(Application.STATE_STORE_NAME, QueryableStoreTypes.keyValueStore());

            return OrderStatus.valueOf(Optional.ofNullable(store.get(orderUuid))
                    .orElseThrow(() -> new OrderNotFoundException("Order not found")));
        };
    }

    public Function<Order, Order> placeOrder() {
        return orderIn -> {
            //create an order
            var order = Order.builder()//
                    .itemName(orderIn.getItemName())//
                    .orderUuid(UUID.randomUUID())//
                    .orderStatus(OrderStatus.PENDING)//
                    .build();

            //producer
            new KafkaTemplate<>(orderJsonSerdeFactoryFunction
                    .apply(orderJsonSerde.serializer(), bootstrapServer), true) {{
                setDefaultTopic(orderTopic);
                sendDefault(order.getOrderUuid(), order);
            }};
            return order;
        };
    }

    @Bean
    public Function<KStream<UUID, Order>, KStream<UUID, Order>> orderAggConsumer() {
        return uuidOrderKStream -> {
            KTable<UUID, String> uuidStringKTable = kStreamKTableStringFunction.apply(uuidOrderKStream);

            //then join the stream with its original stream to keep the flow
            return uuidOrderKStream.leftJoin(uuidStringKTable,
                    (order, status) -> order,
                    Joined.with(Serdes.UUID(), orderJsonSerde, Serdes.String()));
        };
    }

    @Bean
    @SuppressWarnings("unchecked")
    public Function<KStream<UUID, Order>, KStream<UUID, Order>[]> orderProcess() {

        return input -> input
                .peek((uuid, order) -> log.debug("Routing Order: {} [status: {}]", uuid, order.getOrderStatus()))
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
                .branch(isOrderMadePredicate, isInventoryCheckedPredicate, isShippedPredicate);
    }

    @Bean
    public Function<KStream<UUID, Order>, KStream<UUID, Order>> inventoryCheck() {
        return input -> input
                .peek((uuid, order) -> log.debug("Checking order inventory, Order: {}", uuid))
                .peek((key, value) -> value.setOrderStatus(OrderStatus.INVENTORY_CHECKING))
                .map(KeyValue::new);
    }

    @Bean
    public Function<KStream<UUID, Order>, KStream<UUID, Order>> shipping() {
        return input -> input
                .peek((uuid, order) -> log.debug("Applying Shipping Process, Order: {}", uuid))
                .peek((key, value) -> value.setOrderStatus(OrderStatus.SHIPPED))
                .map(KeyValue::new);
    }

    @Bean
    public Consumer<KStream<UUID, Order>> shippedConsumer() {
        return input -> input
                .foreach((key, value) -> log.debug("THIS IS THE END! key: {} value: {}", key, value));
    }
}
