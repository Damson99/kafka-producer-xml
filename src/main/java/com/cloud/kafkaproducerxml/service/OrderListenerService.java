package com.cloud.kafkaproducerxml.service;

import com.cloud.kafkaproducerxml.binder.OrderListenerBinding;
import com.cloud.kafkaproducerxml.model.v1.Order;
import com.cloud.kafkaproducerxml.web.model.v1.ProcessedOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableBinding(OrderListenerBinding.class)
public class OrderListenerService
{

    @Value("${application.configs.topic.name.error}")
    private String ERROR_TOPIC;


    @StreamListener("xml-input-channel")
    @SendTo({"poland-orders-channel", "foreign-orders-channel"})
    public KStream<String, Order>[] process(KStream<String, String> inputStream){

        inputStream.foreach((key, value) ->
                log.info(String.format("Received XML Order Key: %s | Value: %s", key, value)));

//        todo KStream<String, ProcessedOrder> =
        return null;
    }
}
