package com.leon.processor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WindowTrafficData implements Serializable {

    private String routeId;

    private String recordDate;

    private String vehicleType;

    private long totalCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date timestamp;

    public WindowTrafficData() {
    }

}
