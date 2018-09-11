package com.naah69.rocketmqproductdemo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * LocalDateTimeTimestampSerializer
 *
 * @author naah
 * @date 2018-09-11 上午10:46
 * @desc
 */
public class LocalDateTimeTimestampSerializer extends JsonSerializer<LocalDateTime> {


    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeString(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()+"");
    }
}