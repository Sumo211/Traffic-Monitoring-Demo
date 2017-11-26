package com.leon.processor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class POITrafficData implements Serializable {

    private String vehicleId;

    private double distance;

    private String vehicleType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date timestamp;

    public POITrafficData() {
    }

}
