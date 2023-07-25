package com.ehsaniara.scs_kafka_intro;

import com.ehsaniara.scs_kafka_intro.module.Order;
import com.ehsaniara.scs_kafka_intro.module.OrderNotFoundException;
import com.ehsaniara.scs_kafka_intro.module.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static com.ehsaniara.scs_kafka_intro.module.OrderStatus.PENDING;

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

//            return OrderStatus.valueOf(Optional.ofNullable(store.get(orderUuid))
//                    .orElseThrow(() -> new OrderNotFoundException("Order not found")));
            HostInfo hostInfo = interactiveQueryService.getHostInfo(Application.STATE_STORE_NAME,
                    orderUuid, new UUIDSerializer());

            if (interactiveQueryService.getCurrentHostInfo().equals(hostInfo)) {
                //get it from current app store
                return OrderStatus.valueOf(Optional.ofNullable(store.get(orderUuid))
                        .orElseThrow(() -> new OrderNotFoundException("Order not found")));
            } else {
                //get it from remote app store
                return new RestTemplate().getForEntity(
                        String.format("%s://%s:%d/order/status/%s", "http", hostInfo.host(), hostInfo.port(), orderUuid)
                        , OrderStatus.class).getBody();
            }
        };
    }

    public Function<Order, Order> placeOrder() {
        return orderIn -> {
            //create an order
            var order = new Order(UUID.randomUUID(), orderIn.itemName(), PENDING);

            log.info("Create order: {}", order);
            //producer
            new KafkaTemplate<>(orderJsonSerdeFactoryFunction.apply(orderJsonSerde, bootstrapServer), true) {{
                setDefaultTopic(orderTopic);
                sendDefault(order.orderUuid(), order);
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
}
