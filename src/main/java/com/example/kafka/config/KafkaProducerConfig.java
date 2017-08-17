package com.example.kafka.config;

import com.example.kafka.model.Movie;
import com.example.kafka.model.Song;
import com.example.kafka.model.UserOpinion;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaProducerConfig extends AbstractKafkaConfig {

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(Environment environment) {
        ProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(getKafkaCommonProperties(environment), new StringSerializer(), new JsonSerializer<>());
        return new KafkaTemplate<>(producerFactory);
    }

}
