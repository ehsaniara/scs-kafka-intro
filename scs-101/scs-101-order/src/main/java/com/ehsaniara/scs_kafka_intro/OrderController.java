package com.ehsaniara.scs_kafka_intro;

import com.ehsaniara.scs_kafka_intro.module.Order;
import com.ehsaniara.scs_kafka_intro.module.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("order")
    public Order placeOrder(@RequestBody @NotNull(message = "Invalid Order") Order order) {
        return orderService.placeOrder().apply(order);
    }

    @GetMapping("order/status/{orderUuid}")
    public OrderStatus statusCheck(@PathVariable("orderUuid") UUID orderUuid) {
        return orderService.statusCheck().apply(orderUuid);
    }
}
