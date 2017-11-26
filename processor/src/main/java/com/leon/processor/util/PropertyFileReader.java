package com.leon.processor.util;

import lombok.extern.log4j.Log4j2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
public class PropertyFileReader {

    private PropertyFileReader() {
    }

    public static Properties readPropertyFile(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = PropertyFileReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new FileNotFoundException();
            }
        } catch (IOException ex) {
            log.error("Error when loading property file {}", ex);
        }

        return properties;
    }

}
