package com.leon.source.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.leon.source.model.IoTData;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class IoTDataSerializer implements Serializer<IoTData> {

    private final ObjectMapper mapper;

    public IoTDataSerializer() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, IoTData data) {
        try {
            byte[] result = null;
            if (data != null) {
                result = this.mapper.writeValueAsBytes(data);
                log.info(new String(result));
            }

            return result;
        } catch (IOException ex) {
            log.error("Error when serializing: {}", ex);
            throw new SerializationException("Can't serialize data='" + data + "' for topic='" + topic + "' ", ex);
        }
    }

    @Override
    public void close() {

    }

}
