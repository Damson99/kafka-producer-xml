package com.cloud.kafkaproducerxml.web.model.v1;


import com.cloud.kafkaproducerxml.model.v1.Order;
import lombok.Data;

@Data
public class ProcessedOrder {
    String xmlKey;
    String xmlValue;

    OrderTagEnum orderTagEnum;
    Order validOrder;
}
