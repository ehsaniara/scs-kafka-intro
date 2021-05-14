package com.ehsaniara.scs_kafka_intro.scs100;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatus {
    PENDING("PENDING"),
    INVENTORY_CHECKING("INVENTORY_CHECKING"),
    INSUFFICIENT_INVENTORY("INSUFFICIENT_INVENTORY"),
    SHIPPED("SHIPPED"),
    CANCELED("CANCELED");

    private final String name;

    public String toString() {
        return this.name;
    }
}
