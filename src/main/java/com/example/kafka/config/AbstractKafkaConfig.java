package com.example.kafka.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class AbstractKafkaConfig {

    protected Map<String, Object> getKafkaCommonProperties(Environment environment) {
        List<String> propKeys = Arrays.asList(
                CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
                CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
                SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,
                SslConfigs.SSL_KEY_PASSWORD_CONFIG,
                SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG);
        return propKeys.stream().collect(Collectors.toMap(e -> e, e -> environment.getProperty("kafka." + e)));
    }
}
