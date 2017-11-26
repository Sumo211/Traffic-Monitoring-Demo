package com.leon.sink.service;

import com.leon.sink.dto.Response;
import com.leon.sink.model.POITrafficData;
import com.leon.sink.model.TotalTrafficData;
import com.leon.sink.model.WindowTrafficData;
import com.leon.sink.repository.POITrafficRepository;
import com.leon.sink.repository.TotalTrafficRepository;
import com.leon.sink.repository.WindowTrafficRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class TrafficDataService {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final TotalTrafficRepository totalTrafficRepository;

    private final WindowTrafficRepository windowTrafficRepository;

    private final POITrafficRepository poiTrafficRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public TrafficDataService(TotalTrafficRepository totalTrafficRepository, WindowTrafficRepository windowTrafficRepository, POITrafficRepository poiTrafficRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.totalTrafficRepository = totalTrafficRepository;
        this.windowTrafficRepository = windowTrafficRepository;
        this.poiTrafficRepository = poiTrafficRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void trigger() {
        List<TotalTrafficData> totalTrafficData = new ArrayList<>();
        totalTrafficRepository.findTrafficDataByDate(SIMPLE_DATE_FORMAT.format(new Date())).forEach(totalTrafficData::add);

        List<WindowTrafficData> windowTrafficData = new ArrayList<>();
        windowTrafficRepository.findTrafficDataByDate(SIMPLE_DATE_FORMAT.format(new Date())).forEach(windowTrafficData::add);

        List<POITrafficData> poiTrafficData = new ArrayList<>();
        poiTrafficRepository.findAll().forEach(poiTrafficData::add);

        Response response = new Response();
        response.setTotalTraffic(totalTrafficData);
        response.setWindowTraffic(windowTrafficData);
        response.setPoiTraffic(poiTrafficData);
        log.info("Sending to UI: {}", response);
        this.simpMessagingTemplate.convertAndSend("/topic/trafficData", response);
    }

}
