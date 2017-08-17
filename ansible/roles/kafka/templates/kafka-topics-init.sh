#!/usr/bin/env bash

LOCAL_HOSTNAME=$(hostname)
KAFKA_BIN_FOLDER={{kafka_home_folder}}/bin
REPLICATION_FACTOR=3
PARTITIONS_NO=3
TOPICS=(songs movies movies-thriller users-opinions)

function init_kafka_topic {
    TOPIC_NAME=$1
    TOPIC=$($KAFKA_BIN_FOLDER/kafka-topics.sh --zookeeper $LOCAL_HOSTNAME:2181 --list |grep $TOPIC_NAME)
    if [ ! -z "$TOPIC" ]; then
        echo "Delete topic: $TOPIC_NAME"
        $KAFKA_BIN_FOLDER/kafka-topics.sh --zookeeper $LOCAL_HOSTNAME:2181 --delete --topic $TOPIC_NAME
    fi
    echo "Create topic: $TOPIC_NAME"
    $KAFKA_BIN_FOLDER/kafka-topics.sh --create --zookeeper $LOCAL_HOSTNAME:2181 --replication-factor $REPLICATION_FACTOR --partitions $PARTITIONS_NO --topic $TOPIC_NAME
}

for i in ${TOPICS[@]}; do
    init_kafka_topic ${i}
done

exit 0