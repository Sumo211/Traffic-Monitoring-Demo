package com.leon.sink.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.io.Serializable;
import java.util.Date;

@Table("poi_traffic")
@Data
public class POITrafficData implements Serializable {

    @PrimaryKeyColumn(name = "timeStamp", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date timestamp;

    @PrimaryKeyColumn(name = "recorddate", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String recordDate;

    @Column("vehicleid")
    private String vehicleId;

    private double distance;

    @Column("vehicletype")
    private String vehicleType;

    public POITrafficData() {
    }

}
