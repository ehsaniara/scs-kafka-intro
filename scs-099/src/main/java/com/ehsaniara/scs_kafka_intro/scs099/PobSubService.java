package com.ehsaniara.scs_kafka_intro.scs099;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PobSubService {

    private final MyBinder myBinder;

    @Value("${server.port:8080}")
    private int port;

    //counter for every Scheduled attempt
    private int counter;

    @Scheduled(initialDelay = 5_000, fixedDelay = 5_000)
    public void producer() {
        //the producer works just for the app that runs at port 8080,
        //(only one producer app at the time)
        //alternatively you can use @ConditionalOnProperty
        if (port == 8080) {

            // 10 iterative loop
            IntStream.range(0, 10)
                    .forEach(value -> {
                        //this is our Message payload
                        String message = String.format("TestString of %s - %s", counter, value);

                        //here is out message publisher in the given channel into topic "scs-099.order"
                        myBinder.orderOut().send(MessageBuilder.withPayload(message).build());

                        //to show what we have produced in kafka ("WARN" to show in different color on the console)
                        log.warn("message produced: {}", message);
                    });
            //for logging purpose, per Scheduled run
            counter++;
        }
    }

    @SneakyThrows
    @StreamListener(MyBinder.ORDER_IN)
    public void consumer(@Payload String message) {
        //simulate 200ms delay when consumer is working in some task
        Thread.sleep(200);
        log.debug("message consumed: {}", message);
    }
}
