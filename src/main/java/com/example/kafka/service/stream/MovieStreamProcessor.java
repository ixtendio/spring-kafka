package com.example.kafka.service.stream;

import com.example.kafka.config.KafkaTopic;
import com.example.kafka.model.Movie;
import com.example.kafka.model.UserOpinion;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class MovieStreamProcessor {

    private KStream<String, Movie> moviesKafkaStream;
    private KStream<String, UserOpinion> userOpinionKafkaStream;

    @Autowired
    public MovieStreamProcessor(KStream<String, Movie> moviesKafkaStream,
                                KStream<String, UserOpinion> userOpinionKafkaStream) {
        this.moviesKafkaStream = moviesKafkaStream;
        this.userOpinionKafkaStream = userOpinionKafkaStream;
    }

    @PostConstruct
    public void postConstruct() {
        KTable<String, UserOpinionAggregate> ua = userOpinionKafkaStream
                .groupBy((k, v) -> v.getMovieId(), Serdes.String(), UserOpinion.serde())
                .aggregate(UserOpinionAggregate::new, (k, v, ag) -> ag.aggregate(v), UserOpinionAggregate.serde(), "users-options-aggregation");
        moviesKafkaStream
                .leftJoin(ua, this::mergeMovieWithUsersOpinions, Serdes.String(), Movie.serde())
                .filter((k, m) -> m.hasGenre("thriller"))
                .to(Serdes.String(), Movie.serde(), KafkaTopic.MOVIES_THRILLER);
    }

    private Movie mergeMovieWithUsersOpinions(Movie movie, UserOpinionAggregate uoa) {
        if (uoa == null) {
            return movie;
        } else {
            return movie.addRatingAndTags(uoa.getRating(), uoa.getTags());
        }
    }

    static class UserOpinionAggregate {

        private List<Double> ratings = new ArrayList<>();
        private Set<String> tags = new HashSet<>();

        UserOpinionAggregate() {
        }

        @JsonCreator
        UserOpinionAggregate(@JsonProperty("ratings") List<Double> ratings,
                             @JsonProperty("tags") Set<String> tags) {
            this.ratings = ratings;
            this.tags = tags;
        }

        @JsonIgnore
        UserOpinionAggregate aggregate(UserOpinion userOpinion) {
            if (userOpinion.getRating() != null) {
                this.ratings.add(userOpinion.getRating());
            }
            if (userOpinion.getTag() != null) {
                this.tags.add(userOpinion.getTag());
            }
            return this;
        }

        @JsonIgnore
        Double getRating() {
            if (ratings.isEmpty()) {
                return 0d;
            } else {
                return ratings.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum() / ratings.size();
            }
        }

        List<Double> getRatings() {
            return ratings;
        }

        Set<String> getTags() {
            return tags;
        }

        static JsonSerde<UserOpinionAggregate> serde() {
            return new JsonSerde<>(UserOpinionAggregate.class);
        }
    }
}
