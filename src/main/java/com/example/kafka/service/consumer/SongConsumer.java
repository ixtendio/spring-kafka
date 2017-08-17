package com.example.kafka.service.consumer;

import com.example.kafka.model.Song;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class SongConsumer extends AbstractConsumer implements MessageListener<String, Song> {

    private final String consumerGroupId;

    public SongConsumer(String consumerGroupId) {
        super("SONGS_CONSUMER_LOGGER");
        this.consumerGroupId = consumerGroupId;
    }

    @Override
    public void onMessage(ConsumerRecord<String, Song> data) {
        log("[" + consumerGroupId + "] => Key=" + data.key() + ", Partition=" + data.partition() + ", Topic=" + data.topic() + ", Payload=" + data.value());
    }

}
