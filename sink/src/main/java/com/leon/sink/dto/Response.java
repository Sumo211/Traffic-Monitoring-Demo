package com.leon.sink.dto;

import com.leon.sink.model.POITrafficData;
import com.leon.sink.model.TotalTrafficData;
import com.leon.sink.model.WindowTrafficData;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Response implements Serializable {

    private List<TotalTrafficData> totalTraffic;

    private List<WindowTrafficData> windowTraffic;

    private List<POITrafficData> poiTraffic;

}
