package com.ehsaniara.scs_kafka_intro.scs1002;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.UUID;

@ToString
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private UUID orderUuid;

    @NotBlank
    private String itemName;

    private OrderStatus orderStatus;
}
