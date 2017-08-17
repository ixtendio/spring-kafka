package com.example.kafka.service.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractConsumer {

    private Logger logger;

    public AbstractConsumer(String logName) {
        this.logger = LoggerFactory.getLogger(logName);
    }

    protected void log(String line) {
        logger.info(line);
    }
}
