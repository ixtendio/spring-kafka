#!/usr/bin/env bash

SONGS_SAMPLE_URL=https://labrosa.ee.columbia.edu/millionsong/sites/default/files/AdditionalFiles/tracks_per_year.txt
MOVIES_SAMPLE_URL=http://files.grouplens.org/datasets/movielens/ml-latest-small.zip
WORKING_DIR=.run
OUTPUT_PATH=$WORKING_DIR/out
SAMPLES_PATH=$WORKING_DIR/samples
SONGS_SAMPLE_PATH=$SAMPLES_PATH/tracks_per_year.txt
MOVIES_SAMPLE_PATH=$SAMPLES_PATH/ml-latest-small.zip
APP_NAME=spring-kafka
LOADER_PATH=config \
EXECUTABLE_JAR=$(ls target/*.jar |grep $APP_NAME) \

case "$1" in
    debug)
        DEBUG=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005
        ;;
    *)
        DEBUG=
esac

if [ ! -f $SONGS_SAMPLE_PATH ]; then
    wget $SONGS_SAMPLE_URL -P $SAMPLES_PATH
fi

if [ ! -f $MOVIES_SAMPLE_PATH ]; then
    wget $MOVIES_SAMPLE_URL -P $SAMPLES_PATH
fi
unzip -o $MOVIES_SAMPLE_PATH -d $SAMPLES_PATH

java $DEBUG -Dserver.port=8080 -Dloader.path=$LOADER_PATH -Dsongs.sample.path=$SONGS_SAMPLE_PATH -Dmovies.sample.path=$SAMPLES_PATH/ml-latest-small -Dconsumers.output.path=$OUTPUT_PATH -DAPP_NAME=$APP_NAME -jar $EXECUTABLE_JAR
