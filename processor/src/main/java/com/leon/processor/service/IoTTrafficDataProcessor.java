package com.leon.processor.service;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.leon.processor.model.IoTData;
import com.leon.processor.model.POITrafficData;
import com.leon.processor.model.TotalTrafficData;
import com.leon.processor.model.WindowTrafficData;
import com.leon.processor.util.GeoDistanceCalculator;
import com.leon.processor.vo.AggregateKey;
import com.leon.processor.vo.POIData;
import lombok.extern.log4j.Log4j2;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaMapWithStateDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import scala.Tuple2;
import scala.Tuple3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;

@Log4j2
public class IoTTrafficDataProcessor {

    public void processTotalTrafficData(JavaDStream<IoTData> filteredIoTDataStream) {
        JavaPairDStream<AggregateKey, Long> countDStreamPair = filteredIoTDataStream.mapToPair(iot -> new Tuple2<>(new AggregateKey(iot.getRoute().getValue(), iot.getVehicleType().getValue()), 1L)).reduceByKey((a, b) -> a + b);

        JavaMapWithStateDStream<AggregateKey, Long, Long, Tuple2<AggregateKey, Long>> countDStreamWithStatePair = countDStreamPair.mapWithState(StateSpec.function(totalSumFunc).timeout(Durations.seconds(3600)));

        JavaDStream<Tuple2<AggregateKey, Long>> countDStream = countDStreamWithStatePair.map(tuple2 -> tuple2);

        JavaDStream<TotalTrafficData> trafficDStream = countDStream.map(totalTrafficDataFunc);

        Map<String, String> columnNameMappings = new HashMap<>();
        columnNameMappings.put("routeId", "routeid");
        columnNameMappings.put("vehicleType", "vehicletype");
        columnNameMappings.put("totalCount", "totalcount");
        columnNameMappings.put("timestamp", "timestamp");
        columnNameMappings.put("recordDate", "recorddate");

        javaFunctions(trafficDStream).writerBuilder("traffickeyspace", "total_traffic", CassandraJavaUtil.mapToRow(TotalTrafficData.class, columnNameMappings)).saveToCassandra();
    }

    private static final Function3<AggregateKey, Optional<Long>, State<Long>, Tuple2<AggregateKey, Long>> totalSumFunc = (key, currentSum, state) -> {
        long totalSum = currentSum.or(0L) + (state.exists() ? state.get() : 0);
        Tuple2<AggregateKey, Long> total = new Tuple2<>(key, totalSum);
        state.update(totalSum);
        return total;
    };

    private static final Function<Tuple2<AggregateKey, Long>, TotalTrafficData> totalTrafficDataFunc = tuple -> {
        log.debug("Total Count: " + "key " + tuple._1().getRouteId() + "-" + tuple._1().getVehicleType() + " - value " + tuple._2());

        TotalTrafficData totalTrafficData = new TotalTrafficData();
        totalTrafficData.setRouteId(tuple._1.getRouteId());
        totalTrafficData.setVehicleType(tuple._1.getVehicleType());
        totalTrafficData.setTotalCount(tuple._2);
        totalTrafficData.setTimestamp(new Date());
        totalTrafficData.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        return totalTrafficData;
    };

    public void processWindowTrafficData(JavaDStream<IoTData> filteredIoTDataStream) {
        JavaPairDStream<AggregateKey, Long> countDStreamPair = filteredIoTDataStream.mapToPair(iot -> new Tuple2<>(new AggregateKey(iot.getRoute().getValue(), iot.getVehicleType().getValue()), 1L))
                .reduceByKeyAndWindow((a, b) -> a + b, Durations.seconds(30), Durations.seconds(10));

        JavaDStream<WindowTrafficData> trafficDStream = countDStreamPair.map(windowTrafficDataFunc);

        Map<String, String> columnNameMappings = new HashMap<>();
        columnNameMappings.put("routeId", "routeid");
        columnNameMappings.put("vehicleType", "vehicletype");
        columnNameMappings.put("totalCount", "totalcount");
        columnNameMappings.put("timestamp", "timestamp");
        columnNameMappings.put("recordDate", "recorddate");

        javaFunctions(trafficDStream).writerBuilder("traffickeyspace", "window_traffic", CassandraJavaUtil.mapToRow(WindowTrafficData.class, columnNameMappings)).saveToCassandra();
    }

    private static final Function<Tuple2<AggregateKey, Long>, WindowTrafficData> windowTrafficDataFunc = tuple -> {
        log.debug("Window Count: " + "key " + tuple._1().getRouteId() + "-" + tuple._1().getVehicleType() + " - value " + tuple._2());

        WindowTrafficData windowTrafficData = new WindowTrafficData();
        windowTrafficData.setRouteId(tuple._1.getRouteId());
        windowTrafficData.setVehicleType(tuple._1.getVehicleType());
        windowTrafficData.setTotalCount(tuple._2);
        windowTrafficData.setTimestamp(new Date());
        windowTrafficData.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        return windowTrafficData;
    };

    public void processPOITrafficData(JavaDStream<IoTData> nonFilteredIoTDataStream, Broadcast<Tuple3<POIData, String, String>> broadcastPOIValue) {
        //For real-time applications you might want to use Google Map API or Open Street Map API
        JavaDStream<IoTData> iotDataStreamFiltered = nonFilteredIoTDataStream.filter(iot -> iot.getRoute().getValue().equals(broadcastPOIValue.value()._2()))
                .filter(iot -> iot.getVehicleType().getValue().contains(broadcastPOIValue.value()._3()))
                .filter(iot -> GeoDistanceCalculator.isInPOIRadius(iot.getLatitude(), iot.getLongitude(),
                        broadcastPOIValue.value()._1().getLatitude(), broadcastPOIValue.value()._1().getLongitude(), broadcastPOIValue.value()._1().getRadius()));

        JavaPairDStream<IoTData, POIData> poiDStreamPair = iotDataStreamFiltered.mapToPair(iot -> new Tuple2<>(iot, broadcastPOIValue.value()._1()));

        JavaDStream<POITrafficData> trafficDStream = poiDStreamPair.map(poiTrafficDataFunc);

        Map<String, String> columnNameMappings = new HashMap<>();
        columnNameMappings.put("vehicleId", "vehicleid");
        columnNameMappings.put("vehicleType", "vehicletype");
        columnNameMappings.put("distance", "distance");
        columnNameMappings.put("timestamp", "timestamp");

        javaFunctions(trafficDStream).writerBuilder("traffickeyspace", "poi_traffic", CassandraJavaUtil.mapToRow(POITrafficData.class, columnNameMappings)).withConstantTTL(120).saveToCassandra();
    }

    private static final Function<Tuple2<IoTData, POIData>, POITrafficData> poiTrafficDataFunc = tuple -> {
        POITrafficData poiTrafficData = new POITrafficData();
        poiTrafficData.setVehicleId(tuple._1.getVehicleId());
        poiTrafficData.setVehicleType(tuple._1.getVehicleType().getValue());
        poiTrafficData.setTimestamp(new Date());

        double distance = GeoDistanceCalculator.getDistance(tuple._1.getLatitude(), tuple._1.getLongitude(), tuple._2.getLatitude(), tuple._2.getLongitude());
        poiTrafficData.setDistance(distance);
        log.debug("Distance for " + tuple._1.getLatitude() + ", " + tuple._1.getLongitude() + ", " + tuple._2.getLatitude() + ", " + tuple._2.getLongitude() + " = " + distance);
        return poiTrafficData;
    };

}
