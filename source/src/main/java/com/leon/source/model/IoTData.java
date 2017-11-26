package com.leon.source.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class IoTData implements Serializable {

    private String vehicleId;

    private VehicleType vehicleType;

    private Route route;

    private double latitude;

    private double longitude;

    private Instant timestamp;

    private double speed;

    private double fuelLevel;

    public IoTData() {
    }

    @Builder
    public IoTData(String vehicleId, VehicleType vehicleType, Route route, double latitude, double longitude, Instant timestamp, double speed, double fuelLevel) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.route = route;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.speed = speed;
        this.fuelLevel = fuelLevel;
    }

}
