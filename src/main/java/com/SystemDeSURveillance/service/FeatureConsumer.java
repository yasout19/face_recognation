package com.SystemDeSURveillance.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class FeatureConsumer {

    private String lastConsumedMessage;
    private CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = "test-topic", groupId = "group_id")
    public void consumeFromTopic2(String message) {
        lastConsumedMessage = message;
        latch.countDown();  // Release the latch when a message is consumed
        System.out.println("Consumed message from topic2: " + message);
    }

    public String getLastConsumedMessage() {
        return lastConsumedMessage;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
