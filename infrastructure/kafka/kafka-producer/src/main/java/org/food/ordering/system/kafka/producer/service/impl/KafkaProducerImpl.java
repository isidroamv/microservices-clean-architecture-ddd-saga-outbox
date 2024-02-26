package org.food.ordering.system.kafka.producer.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import org.food.ordering.system.kafka.producer.service.KafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;
import java.io.Serializable;

@Slf4j
@Component
public class KafkaProducerImpl <K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {
    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, K key, V message, ListenableFutureCallback<SendResult<K, V>> callback) {
        log.info("Sending message to topic {} with key {} and value {}", topicName, key, message);
        try {
            ListenableFuture<SendResult<K, V>> send = kafkaTemplate.send(topicName, key, message);
            send.addCallback(callback);
        } catch (Exception e) {
            log.info("Error on kafka producer with key {} and value {} and exception {}", key, message,
                    e.getMessage());
            throw new KafkaProducerException("Error on kafka producer with key " + key + " and value " + message);
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing kafka");
            kafkaTemplate.destroy();
        }
    }
}
