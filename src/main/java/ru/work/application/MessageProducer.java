package ru.work.application;

import kafka.javaapi.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class MessageProducer {
    private final String topicName = "alerts";
    private Properties props = new Properties();
    private KafkaProducer<String, String> producer;

    public MessageProducer() {

    }

    public void init() {
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, String>(props);
    }

    public void sendMessage(String message) {
        producer.send(new ProducerRecord<String, String>(topicName, message));
        producer.close();
    }
}
