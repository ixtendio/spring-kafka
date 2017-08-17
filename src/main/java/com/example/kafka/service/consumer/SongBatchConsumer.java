package com.example.kafka.service.consumer;

import com.example.kafka.model.Song;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchMessageListener;

import java.util.List;

public class SongBatchConsumer extends AbstractConsumer implements BatchMessageListener<String, Song> {

    private final String consumerGroupId;

    public SongBatchConsumer(String consumerGroupId) {
        super("SONGS_BATCH_CONSUMER_LOGGER");
        this.consumerGroupId = consumerGroupId;
    }

    @Override
    public void onMessage(List<ConsumerRecord<String, Song>> data) {
        data.forEach(r -> log("[" + consumerGroupId + "] => Batch Size=" + data.size() + ",  Key=" + r.key() + ", Partition=" + r.partition() + ", Topic=" + r.topic() + ", Payload=" + r.value()));
    }

}
