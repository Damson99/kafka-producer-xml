package com.cloud.kafkaproducerxml.web.model.v1;


import com.cloud.kafkaproducerxml.model.v1.Order;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessedOrder {
    private String xmlKey;
    private String xmlValue;
    private String orderTagEnum;
    private Order validOrder;
}
