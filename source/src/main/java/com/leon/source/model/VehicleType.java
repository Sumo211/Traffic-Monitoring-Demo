package com.leon.source.model;

public enum VehicleType {

    LARGE_TRUCK("Large Truck"), SMALL_TRUCK("Small Truck"), PRIVATE_CAR("Private Car"), BUS("Bus"), TAXI("Taxi");

    private String value;

    VehicleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
