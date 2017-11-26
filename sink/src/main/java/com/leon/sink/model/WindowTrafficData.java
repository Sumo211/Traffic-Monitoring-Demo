package com.leon.sink.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

import java.io.Serializable;
import java.util.Date;

@Table("window_traffic")
@Data
public class WindowTrafficData implements Serializable {

    @PrimaryKeyColumn(name = "routeid", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String routeId;

    @PrimaryKeyColumn(name = "recorddate", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String recordDate;

    @PrimaryKeyColumn(name = "vehicletype", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String vehicleType;

    @Column(value = "totalcount")
    private long totalCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(value = "timestamp")
    private Date timestamp;

    public WindowTrafficData() {
    }

}
