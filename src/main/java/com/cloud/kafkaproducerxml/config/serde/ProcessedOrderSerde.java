package com.cloud.kafkaproducerxml.config.serde;

import com.cloud.kafkaproducerxml.web.model.v1.ProcessedOrder;
import io.confluent.kafka.streams.serdes.json.KafkaJsonSchemaSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

import static com.cloud.kafkaproducerxml.config.constant.Constants.PROCESSED_ORDER_SCHEMA_REGISTRY_URL;

@Configuration
public class ProcessedOrderSerde extends Serdes {

    private final static Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", PROCESSED_ORDER_SCHEMA_REGISTRY_URL);

    public static Serde<ProcessedOrder> processedOrderSerde(){
        final Serde<ProcessedOrder> processedOrderJsonSerde = new KafkaJsonSchemaSerde<>();
        processedOrderJsonSerde.configure(serdeConfig, false);
        return processedOrderJsonSerde;
    }
}
