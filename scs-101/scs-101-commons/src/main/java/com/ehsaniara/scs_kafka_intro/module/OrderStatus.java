package com.ehsaniara.scs_kafka_intro.module;

public enum OrderStatus {
    PENDING("PENDING"),
    INVENTORY_CHECKING("INVENTORY_CHECKING"),
    OUT_OF_STOCK("OUT_OF_STOCK"),
    SHIPPED("SHIPPED"),
    CANCELED("CANCELED");

    private final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
