package com.example.workq.tracelisteners;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
    private static final String KAFKA_CONFIG_KEY = "kafka.topic.name";
    private static final String KAFKA_SERVERS_KEY = "kafka.servers";
    private static final String KAFKA_CLIENT_ID = "kafka.client.id";
    private static final String CONFIG_FILE = "/config.properties";
    //private static final boolean KAFKA_IS_ENABLED = "kafka.enabled";

    public AppConfig() {

    }

    public String kafkaServers() {
        return "localhost:9092";

    }

    public String processTraceTopic() {
        return "trace-events";
    }

    public String taskTraceTopic() {
        return "trace-events";
    }

    public String slaTraceTopic() {
        return "trace-events";
    }

    public String kafkaClientId() {
        return "pam-trace-publisher";
    }

    public boolean kafkaEnabled() {
        return false;
    }

}
