package com.ehsaniara.scs_kafka_intro.scs1002;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface OrderTopology {

    Predicate<UUID, Order> isOrderMadePredicate = (k, v) -> v.getOrderStatus().equals(OrderStatus.PENDING);
    Predicate<UUID, Order> isInventoryCheckedPredicate = (k, v) -> v.getOrderStatus().equals(OrderStatus.INVENTORY_CHECKING);
    Predicate<UUID, Order> isShippedPredicate = (k, v) -> v.getOrderStatus().equals(OrderStatus.SHIPPED);

    Function<KStream<UUID, Order>, KTable<UUID, String>> kStreamKTableStringFunction = input -> input
            .groupBy((s, order) -> order.getOrderUuid(),
                    Grouped.with(null, new JsonSerde<>(Order.class, new ObjectMapper())))
            .aggregate(
                    String::new,
                    (s, order, oldStatus) -> order.getOrderStatus().toString(),
                    Materialized.<UUID, String, KeyValueStore<Bytes, byte[]>>as(Application.STATE_STORE_NAME)
                            .withKeySerde(Serdes.UUID()).
                            withValueSerde(Serdes.String())
            );

    //Just the min req vars
    BiFunction<Serializer<Order>, String, DefaultKafkaProducerFactory<UUID, Order>> orderJsonSerdeFactoryFunction
            = (orderSerde, bootstrapServer) -> new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
            ProducerConfig.RETRIES_CONFIG, 0,
            ProducerConfig.BATCH_SIZE_CONFIG, 16384,
            ProducerConfig.LINGER_MS_CONFIG, 1,
            ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, orderSerde.getClass()));

}
