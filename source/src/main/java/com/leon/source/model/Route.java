package com.leon.source.model;

public enum Route {

    ROUTE37("Route-37"), ROUTE43("Route-43"), ROUTE82("Route-82");

    private String value;

    Route(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
