package com.example.kafka.config;

import com.example.kafka.model.Movie;
import com.example.kafka.model.Song;
import com.example.kafka.service.consumer.MovieBatchConsumer;
import com.example.kafka.service.consumer.MovieConsumer;
import com.example.kafka.service.consumer.SongBatchConsumer;
import com.example.kafka.service.consumer.SongConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig extends AbstractKafkaConfig {

    @Bean
    public ConcurrentMessageListenerContainer<String, Song> songsKafkaListener(Environment environment) {
        return songsMessageListenerContainer("songs-group-1", false, environment);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Song> songsBatchKafkaListener(Environment environment) {
        return songsMessageListenerContainer("songs-group-2", true, environment);
    }

    private ConcurrentMessageListenerContainer<String, Song> songsMessageListenerContainer(String consumerGroupId, boolean batch, Environment environment) {
        Map<String, Object> kafkaProps = new HashMap<>();
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        kafkaProps.putAll(getKafkaCommonProperties(environment));

        ConsumerFactory<String, Song> consumerFactory = new DefaultKafkaConsumerFactory<>(kafkaProps, new StringDeserializer(), new JsonDeserializer<>(Song.class));
        ContainerProperties containerProperties = new ContainerProperties(KafkaTopic.SONGS);
        containerProperties.setPollTimeout(3000);
        containerProperties.setMessageListener(batch ? new SongBatchConsumer(consumerGroupId) : new SongConsumer(consumerGroupId));
        ConcurrentMessageListenerContainer<String, Song> container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setConcurrency(3);
        return container;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Movie> moviesKafkaListener(Environment environment) {
        return moviesMessageListenerContainer("movies-group-1", false, environment);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Movie> moviesBatchKafkaListener(Environment environment) {
        return moviesMessageListenerContainer("movies-group-2", true, environment);
    }

    private ConcurrentMessageListenerContainer<String, Movie> moviesMessageListenerContainer(String consumerGroupId, boolean batch, Environment environment) {
        Map<String, Object> kafkaProps = new HashMap<>();
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        kafkaProps.putAll(getKafkaCommonProperties(environment));

        ConsumerFactory<String, Movie> consumerFactory = new DefaultKafkaConsumerFactory<>(kafkaProps, new StringDeserializer(), new JsonDeserializer<>(Movie.class));
        ContainerProperties containerProperties = new ContainerProperties(KafkaTopic.MOVIES);
        containerProperties.setPollTimeout(3000);
        containerProperties.setMessageListener(batch ? new MovieBatchConsumer(consumerGroupId) : new MovieConsumer(consumerGroupId, "MOVIES_CONSUMER_LOGGER"));
        ConcurrentMessageListenerContainer<String, Movie> container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setConcurrency(3);
        return container;
    }

}
