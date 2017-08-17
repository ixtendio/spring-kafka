package com.example.kafka.config;

import com.example.kafka.model.Movie;
import com.example.kafka.model.UserOpinion;
import com.example.kafka.service.consumer.MovieConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamingConfig extends AbstractKafkaConfig {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfig(Environment environment) {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "movies-streams");
        props.putAll(getKafkaCommonProperties(environment));
        return new StreamsConfig(props);
    }

    @Bean
    public KStream<String, Movie> moviesKafkaStream(KStreamBuilder defaultKStreamBuilder) {
        return defaultKStreamBuilder.stream(Serdes.String(), Movie.serde(), KafkaTopic.MOVIES);
    }

    @Bean
    public KStream<String, UserOpinion> userOpinionKafkaStream(KStreamBuilder defaultKStreamBuilder) {
        return defaultKStreamBuilder.stream(Serdes.String(), UserOpinion.serde(), KafkaTopic.USERS_OPINIONS);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Movie> moviesThrillerKafkaListener(Environment environment) {
        String consumerGroupId = "movies-thriller-group";
        Map<String, Object> kafkaProps = new HashMap<>();
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        kafkaProps.putAll(getKafkaCommonProperties(environment));

        ConsumerFactory<String, Movie> consumerFactory = new DefaultKafkaConsumerFactory<>(kafkaProps, new StringDeserializer(), new JsonDeserializer<>(Movie.class));
        ContainerProperties containerProperties = new ContainerProperties(KafkaTopic.MOVIES_THRILLER);
        containerProperties.setPollTimeout(3000);
        containerProperties.setMessageListener(new MovieConsumer(consumerGroupId, "MOVIES_THRILLER_CONSUMER_LOGGER"));
        ConcurrentMessageListenerContainer<String, Movie> container = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setConcurrency(3);
        return container;
    }

}
