package com.cloud.kafkaproducerxml.web.model.v1;

public enum CountryEnum {
    POLAND("POLAND"), FOREIGN_COUNTRY("FOREIGN_COUNTRY");

    private String value;


    CountryEnum(String value) {
        this.value=value;
    }
}
