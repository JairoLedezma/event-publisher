package com.example.workq.tracelisteners.messaging;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.workq.tracelisteners.AppConfig;
import com.example.workq.tracelisteners.events.ProcessTraceEvent;
import com.example.workq.tracelisteners.events.SlaViolatedTraceEvent;
import com.example.workq.tracelisteners.events.TaskTraceEvent;
import com.example.workq.tracelisteners.events.TraceEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Publishes a {@link ProcessTraceEvent} to kafka
 */
public class KafkaPublisher implements MessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisher.class);
    private final Producer<Long, String> producer;
    private final AppConfig config;
    private final ObjectMapper mapper;

    private boolean kafkaEnabled=true;


    public KafkaPublisher() {
        config = new AppConfig();
        mapper = new ObjectMapper();

        if(kafkaEnabled){
            producer = createProducer();
        }
        else {
            producer = null;
        }
    }

    private Producer<Long, String> createProducer() {

        Thread.currentThread().setContextClassLoader(null);
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.kafkaServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, config.kafkaClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<>(props);

    }

    /**
     * Asynchronously publish a trace event message to Kafka.
     * @param event The message to publish
     * @throws PublishingFailedException if the message could not be published.
     */
    @Override
    public void publishMessage(ProcessTraceEvent event) throws PublishingFailedException {
        if(kafkaEnabled){
            LOGGER.info("Publishing message {}", event);
            publishMessage(event,  config.processTraceTopic());
        }
    }

    @Override
    public void publishMessage(SlaViolatedTraceEvent event) throws PublishingFailedException {
        if(kafkaEnabled) {
            LOGGER.info("Publishing message {}", event);
            publishMessage(event, config.slaTraceTopic());
        }
    }

    @Override
    public void publishMessage(TaskTraceEvent event) throws PublishingFailedException {
        if(kafkaEnabled){
            LOGGER.info("Publishing message {}", event);
            publishMessage(event, config.taskTraceTopic());
        }
    }

    private void publishMessage(TraceEvent event, String topic) throws PublishingFailedException {
        if(kafkaEnabled){
            LOGGER.info("event = {}, topic = {}", event, topic);
            long key;
            String value;
            try {
                key = Long.parseLong(event.getProcessInstanceId());
                value = mapper.writeValueAsString(event);
            } catch (JsonProcessingException e) {
                LOGGER.error("Failed to publish message {}", event.toString());
                throw new PublishingFailedException(e);
            }

            LOGGER.info("topic = {}, key = {}", topic, key);

            final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, key, value);
            producer.send(record, ((meta, exception) -> {
                if (meta != null) {
                    LOGGER.info("Sent message");
//                LOGGER.debug("Sent record({}, {}), metadata({}, {})", key, value, meta.partition(), meta.offset());
                } else {
                    LOGGER.error("Failed to publish message({}, {})", key, value);
                    LOGGER.error("", exception);
                }
            }));

            producer.flush();
        }
    }
}
