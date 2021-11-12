package com.cloud.kafkaproducerxml.service;

import com.cloud.kafkaproducerxml.binder.OrderListenerBinding;
import com.cloud.kafkaproducerxml.config.serde.ProcessedOrderSerde;
import com.cloud.kafkaproducerxml.model.v1.Order;
import com.cloud.kafkaproducerxml.web.model.v1.CountryEnum;
import com.cloud.kafkaproducerxml.web.model.v1.OrderTagEnum;
import com.cloud.kafkaproducerxml.web.model.v1.ProcessedOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static com.cloud.kafkaproducerxml.config.constant.Constants.PROCESSED_ORDER_SCHEMA_REGISTRY_URL;

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

        KStream<String, ProcessedOrder> processedOrderKStream = inputStream
                .map((key, value) -> {
                    ProcessedOrder processedOrder = ProcessedOrder.builder()
                           .xmlKey(key)
                           .xmlValue(value)
                           .build();
                   try {
                       JAXBContext jaxbContext = JAXBContext.newInstance(ProcessedOrder.class);
                       Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                       processedOrder.setValidOrder((Order) unmarshaller.unmarshal(new StringReader(value)));
                       processedOrder.setOrderTagEnum(OrderTagEnum.VALID_ORDER.name());

                       if(isCityEmptyInProcessedOrder(processedOrder)){
                           log.error("Missing destination city");
                           processedOrder.setOrderTagEnum(OrderTagEnum.ADDRESS_ERROR.name());
                       }
                   } catch (JAXBException e) {
                       log.error("Failed to unmarshal the incoming xml");
                       processedOrder.setOrderTagEnum(OrderTagEnum.PARSE_ERROR.name());
                   }
                   return KeyValue.pair(processedOrder.getOrderTagEnum(), processedOrder);
                });

        processedOrderKStream.filterNot((key, value) -> key.equalsIgnoreCase(OrderTagEnum.VALID_ORDER.name()))
                .to(ERROR_TOPIC, Produced.with(ProcessedOrderSerde.String(), ProcessedOrderSerde.processedOrderSerde()));

        KStream<String, Order> validOrders = processedOrderKStream
                .filter((key, value) -> key.equalsIgnoreCase(OrderTagEnum.VALID_ORDER.name()))
                .map((key, value) -> KeyValue.pair(value.getValidOrder().getOrderId(), value.getValidOrder()));

        validOrders.foreach((key, value) -> log.info(String.format("Valid order with: Key - %s | Value id - %s | Value - %s", key, value.getOrderId(), value)));

        Predicate<String, Order> isPolandOrder = (key, value) -> value.getShipTo().getCountry().equalsIgnoreCase(CountryEnum.POLAND.name());
        Predicate<String, Order> isForeignOrder = (key, value) -> !value.getShipTo().getCountry().equalsIgnoreCase(CountryEnum.FOREIGN_COUNTRY.name());
        return validOrders.branch(isPolandOrder, isForeignOrder);
    }

    private boolean isCityEmptyInProcessedOrder(ProcessedOrder processedOrder){
        return processedOrder.getValidOrder().getShipTo().getCity().isEmpty();
    }
}
