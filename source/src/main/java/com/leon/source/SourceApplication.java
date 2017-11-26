package com.leon.source;

import com.leon.source.service.IoTDataProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Log4j2
public class SourceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SourceApplication.class, args);

        IoTDataProducer producer = (IoTDataProducer) context.getBean("producer");
        try {
            producer.sendData();
        } catch (InterruptedException ex) {
            log.error("Interruption: {}", ex);
        }
    }

}
