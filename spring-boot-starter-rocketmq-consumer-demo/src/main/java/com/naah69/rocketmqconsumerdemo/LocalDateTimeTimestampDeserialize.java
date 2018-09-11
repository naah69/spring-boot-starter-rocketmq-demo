package com.naah69.rocketmqconsumerdemo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * LocalDateTimeTimestampDeserialize
 *
 * @author naah
 * @date 2018-09-11 下午4:59
 * @desc
 */
public class LocalDateTimeTimestampDeserialize extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String text = p.getText();
        long timestamp = Long.parseLong(text);
        return LocalDateTime.ofEpochSecond(timestamp/1000,0, ZoneOffset.ofHours(8));
    }
}
