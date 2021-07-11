package com.ehsaniara.scs_kafka_intro;

import com.ehsaniara.scs_kafka_intro.module.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;


public interface OrderTopology {

    BiFunction<Serde<Order>, String, DefaultKafkaProducerFactory<UUID, Order>> orderJsonSerdeFactoryFunction
            = (serde, bootstrapServer) -> new DefaultKafkaProducerFactory<>(Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
            ProducerConfig.RETRIES_CONFIG, 0,
            ProducerConfig.BATCH_SIZE_CONFIG, 16384,
            ProducerConfig.LINGER_MS_CONFIG, 1,
            ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serde.serializer().getClass()));

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
}
