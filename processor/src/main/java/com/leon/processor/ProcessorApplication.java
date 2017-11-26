package com.leon.processor;

import com.leon.processor.model.IoTData;
import com.leon.processor.service.IoTTrafficDataProcessor;
import com.leon.processor.util.IoTDataDeserializer;
import com.leon.processor.util.PropertyFileReader;
import com.leon.processor.vo.POIData;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;
import scala.Tuple3;

import java.util.*;

@Log4j2
public class ProcessorApplication {

    public static void main(String[] args) throws InterruptedException {
        Properties props = PropertyFileReader.readPropertyFile("application.properties");
        SparkConf conf = new SparkConf();
        conf.setAppName(props.getProperty("spark.app.name"));
        conf.setMaster(props.getProperty("spark.master"));
        conf.set("spark.cassandra.connection.host", props.getProperty("cassandra.host"));
        conf.set("spark.cassandra.connection.port", props.getProperty("cassandra.port"));
        conf.set("spark.cassandra.connection.keep_alive_ms", props.getProperty("cassandra.keep-alive"));

        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(conf, Durations.seconds(5));
        javaStreamingContext.checkpoint(props.getProperty("spark.checkpoint.dir"));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", props.getProperty("kafka.bootstrap-servers"));
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", IoTDataDeserializer.class);
        kafkaParams.put("group.id", "iot-data");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Set<String> topics = new HashSet<>();
        topics.add(props.getProperty("kafka.topic"));

        JavaInputDStream<ConsumerRecord<String, IoTData>> directKafkaStream = KafkaUtils.createDirectStream(
                javaStreamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams));

        log.info("Starting Stream Processing...");

        JavaDStream<IoTData> nonFilteredIoTDataStream = directKafkaStream.map(record -> new Tuple2<>(record.key(), record.value())).map(Tuple2::_2);

        JavaPairDStream<String, IoTData> ioTDataPairStream = nonFilteredIoTDataStream.mapToPair(iot -> new Tuple2<>(iot.getVehicleId(), iot)).reduceByKey((a, b) -> a);

        JavaMapWithStateDStream<String, IoTData, Boolean, Tuple2<IoTData, Boolean>> iotDStreamWithStatePairs = ioTDataPairStream.mapWithState(StateSpec.function(processVehicleFunc).timeout(Durations.seconds(3600)));

        JavaDStream<Tuple2<IoTData, Boolean>> filteredIoTDStreams = iotDStreamWithStatePairs.map(tuple2 -> tuple2).filter(tuple -> tuple._2.equals(Boolean.FALSE));

        JavaDStream<IoTData> filteredIoTDataStream = filteredIoTDStreams.map(tuple -> tuple._1);

        filteredIoTDataStream.cache();

        IoTTrafficDataProcessor iotTrafficProcessor = new IoTTrafficDataProcessor();
        iotTrafficProcessor.processTotalTrafficData(filteredIoTDataStream);
        iotTrafficProcessor.processWindowTrafficData(filteredIoTDataStream);

        POIData poiData = new POIData(33.877495, -95.50238, 30);
        Broadcast<Tuple3<POIData, String, String>> broadcastPOIValue = javaStreamingContext.sparkContext().broadcast(new Tuple3<>(poiData, "Route-37", "Truck"));
        iotTrafficProcessor.processPOITrafficData(nonFilteredIoTDataStream, broadcastPOIValue);

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }

    private static final Function3<String, Optional<IoTData>, State<Boolean>, Tuple2<IoTData, Boolean>> processVehicleFunc = (vehicleId, iot, state) -> {
        Tuple2<IoTData, Boolean> vehicle = new Tuple2<>(iot.get(), false);
        if (state.exists()) {
            vehicle = new Tuple2<>(iot.get(), true);
        } else {
            state.update(Boolean.TRUE);
        }

        return vehicle;
    };

}
