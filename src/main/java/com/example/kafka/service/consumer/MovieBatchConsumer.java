package com.example.kafka.service.consumer;

import com.example.kafka.model.Movie;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchMessageListener;

import java.util.List;

public class MovieBatchConsumer extends AbstractConsumer implements BatchMessageListener<String, Movie> {

    private final String consumerGroupId;

    public MovieBatchConsumer(String consumerGroupId) {
        super("MOVIES_BATCH_CONSUMER_LOGGER");
        this.consumerGroupId = consumerGroupId;
    }

    @Override
    public void onMessage(List<ConsumerRecord<String, Movie>> data) {
        data.forEach(r -> log("[" + consumerGroupId + "] => Batch Size=" + data.size() + ", Key=" + r.key() + ", Partition=" + r.partition() + ", Topic=" + r.topic() + ", Payload=" + r.value()));
    }

}
