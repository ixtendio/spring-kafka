package com.example.kafka.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.kafka.support.serializer.JsonSerde;

public class UserOpinion {

    private final String userId;
    private final String movieId;
    private final Double rating;
    private final String tag;
    private final String timestamp;

    @JsonCreator
    public UserOpinion(@JsonProperty("userId") String userId,
                       @JsonProperty("movieId") String movieId,
                       @JsonProperty("rating") Double rating,
                       @JsonProperty("tag") String tag,
                       @JsonProperty("timestamp") String timestamp) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
        this.tag = tag;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public Double getRating() {
        return rating;
    }

    public String getTag() {
        return tag;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static UserOpinion fromTagsLine(String line) {
        String[] items = line.split(",");
        return new UserOpinion(items[0], items[1], null, items[2], items[3]);
    }

    public static UserOpinion fromRatingsLine(String line) {
        String[] items = line.split(",");
        return new UserOpinion(items[0], items[1], Double.valueOf(items[2]), null, items[3]);
    }

    public static JsonSerde<UserOpinion> serde() {
        return new JsonSerde<>(UserOpinion.class);
    }
}
