package com.leon.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.processor.model.IoTData;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testDeserializer() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonValue = "{\"vehicleId\":\"bf0bc111-db2f-4aec-96cf-2893fdc95fc7\",\"vehicleType\":\"BUS\",\"route\":\"ROUTE82\",\"latitude\":0.04050511,\"longitude\":0.53871495,\"timestamp\":\"2017-10-11T02:39:44.416Z\",\"speed\":71.0,\"fuelLevel\":38.0}";
        IoTData data = mapper.readValue(jsonValue, IoTData.class);
        assertEquals("bf0bc111-db2f-4aec-96cf-2893fdc95fc7", data.getVehicleId());
        assertEquals("BUS", data.getVehicleType().name());
        assertEquals("ROUTE82", data.getRoute().name());
        assertEquals(0.04050511, data.getLatitude(), 0.0001);
        assertEquals(0.53871495, data.getLongitude(), 0.0001);
        assertEquals(0.53871495, data.getLongitude(), 0.0001);
        assertEquals(Instant.parse("2017-10-11T02:39:44.416Z"), data.getTimestamp());
        assertEquals(71, data.getSpeed(), 0.0001);
        assertEquals(38, data.getFuelLevel(), 0.0001);
    }

}
