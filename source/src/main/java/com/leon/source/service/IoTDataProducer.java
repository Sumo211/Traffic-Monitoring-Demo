package com.leon.source.service;

import com.leon.source.model.IoTData;
import com.leon.source.model.Route;
import com.leon.source.model.VehicleType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component("producer")
public class IoTDataProducer {

    private static final int TOTAL_VEHICLES = 100;

    private static final int TOTAL_EVENTS = 5;

    private static final Random RANDOM = new Random();

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    private final KafkaTemplate<String, IoTData> kafkaTemplate;

    public IoTDataProducer(KafkaTemplate<String, IoTData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendData() throws InterruptedException {
        List<IoTData> events = generateIoTEvent();
        for (IoTData event : events) {
            ProducerRecord<String, IoTData> data = new ProducerRecord<>(topic, event);
            kafkaTemplate.send(data);
            Thread.sleep(RANDOM.nextInt(3000 - 1000) + 1000);
        }
    }

    private List<IoTData> generateIoTEvent() {
        List<IoTData> events = new ArrayList<>();
        Route[] routes = Route.values();
        VehicleType[] vehicleTypes = VehicleType.values();
        int numberOfRoutes = routes.length;
        int numberOfVehicleTypes = vehicleTypes.length;

        for (int i = 0; i < TOTAL_VEHICLES; i++) {
            String vehicleId = UUID.randomUUID().toString();
            Route route = routes[RANDOM.nextInt(numberOfRoutes)];
            VehicleType vehicleType = vehicleTypes[RANDOM.nextInt(numberOfVehicleTypes)];
            Instant timestamp = Instant.now();
            double speed = getRandomSpeed();
            double fuelLevel = getRandomFuelLevel();

            for (int j = 0; j < TOTAL_EVENTS; j++) {
                String coordinates = getRandomCoordinates(route);
                double latitude = Double.valueOf(coordinates.substring(0, coordinates.indexOf(',')));
                double longitude = Double.valueOf(coordinates.substring(coordinates.indexOf(',') + 1, coordinates.length()));
                IoTData event = IoTData.builder().vehicleId(vehicleId).route(route).vehicleType(vehicleType).timestamp(timestamp)
                        .latitude(latitude).longitude(longitude).speed(speed).fuelLevel(fuelLevel).build();
                events.add(event);
            }
        }

        Collections.shuffle(events);
        return events;
    }

    private double getRandomSpeed() {
        return RANDOM.nextInt(100 - 20) + 20;
    }

    private double getRandomFuelLevel() {
        return RANDOM.nextInt(40 - 10) + 10;
    }

    private String getRandomCoordinates(Route route) {
        int latPrefix = 0;
        int lngPrefix = 0;

        if ("Route-37".equals(route.getValue())) {
            latPrefix = 33;
            lngPrefix = -96;
        } else if ("Route-43".equals(route.getValue())) {
            latPrefix = 34;
            lngPrefix = -97;
        } else if ("Route-82".equals(route.getValue())) {
            latPrefix = 35;
            lngPrefix = -98;
        }

        return (latPrefix + RANDOM.nextFloat()) + "," + (lngPrefix + RANDOM.nextFloat());
    }

}
