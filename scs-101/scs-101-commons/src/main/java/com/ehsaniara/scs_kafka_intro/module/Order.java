package com.ehsaniara.scs_kafka_intro.module;

import java.util.UUID;

public record Order(UUID orderUuid, String itemName, OrderStatus orderStatus) {

}
