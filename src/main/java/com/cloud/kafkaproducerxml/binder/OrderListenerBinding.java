package com.cloud.kafkaproducerxml.binder;


import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface OrderListenerBinding {

    @Input("xml-input-channel")
    KStream<String, String> xmlInputStream();

    @Output("poland-orders-channel")
    KStream<String, String> polandOrdersOutputStream();

    @Output("foreign-orders-channel")
    KStream<String, String> foreignOrdersOutputStream();

}
