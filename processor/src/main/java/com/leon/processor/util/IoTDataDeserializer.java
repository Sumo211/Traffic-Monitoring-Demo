package com.leon.processor.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.processor.model.IoTData;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Log4j2
public class IoTDataDeserializer implements Deserializer<IoTData> {

    private final ObjectMapper mapper;

    public IoTDataDeserializer() {
        mapper = new ObjectMapper();
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public IoTData deserialize(String topic, byte[] bytes) {
        try {
            IoTData data = null;
            if (bytes != null && bytes.length != 0) {
                data = mapper.readValue(bytes, IoTData.class);
                log.info(data);
            }

            return data;
        } catch (IOException ex) {
            log.error("Error when deserializing {}", ex);
            throw new SerializationException("Can't deserialize data '" + Arrays.toString(bytes) + "' from topic '" + topic + "' ", ex);
        }
    }

    @Override
    public void close() {

    }

}
