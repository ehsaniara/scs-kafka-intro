package com.ehsaniara.scs_kafka_intro.scs100;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class ProducerController {

    private final PurchaseService purchaseService;

    @PostMapping("order")
    public Order placeOrder(@RequestBody @NotNull(message = "Invalid Order") Order order) {

        return purchaseService.placeOrder(order);
    }

    @GetMapping("order/status/{orderUuid}")
    public OrderStatus statusCheck(@PathVariable("orderUuid") UUID orderUuid) {

        return purchaseService.statusCheck(orderUuid);
    }

    @ControllerAdvice
    public static class RestResponseEntityExceptionHandler
            extends ResponseEntityExceptionHandler {

        @ExceptionHandler({OrderFailedException.class})
        public ResponseEntity<Object> orderErrorException(
                Exception ex, WebRequest request) {
            return new ResponseEntity<Object>(
                    "I_AM_A_TEAPOT", new HttpHeaders(), HttpStatus.I_AM_A_TEAPOT);
        }

        @ExceptionHandler({OrderNotFoundException.class})
        public ResponseEntity<Object> orderNotFoundException(
                Exception ex, WebRequest request) {
            return new ResponseEntity<Object>(
                    "NOT_FOUND", new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

    }
}
