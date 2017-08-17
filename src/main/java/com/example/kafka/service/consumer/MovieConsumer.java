package com.example.kafka.service.consumer;

import com.example.kafka.model.Movie;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class MovieConsumer extends AbstractConsumer implements MessageListener<String, Movie> {

    private final String consumerGroupId;

    public MovieConsumer(String consumerGroupId, String loggerName) {
        super(loggerName);
        this.consumerGroupId = consumerGroupId;
    }

    @Override
    public void onMessage(ConsumerRecord<String, Movie> data) {
        log("[" + consumerGroupId + "] => Key=" + data.key() + ", Partition=" + data.partition() + ", Topic=" + data.topic() + ", Payload=" + data.value());
    }
}
