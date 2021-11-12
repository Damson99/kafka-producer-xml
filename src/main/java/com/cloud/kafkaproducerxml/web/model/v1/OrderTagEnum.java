package com.cloud.kafkaproducerxml.web.model.v1;

public enum OrderTagEnum {
    VALID_ORDER("VALID_ORDER"), PARSE_ERROR("PARSE_ERROR"), ADDRESS_ERROR("PARSE_ERROR");
    private String value;

    OrderTagEnum(String value) {
        this.value=value;
    }
}
