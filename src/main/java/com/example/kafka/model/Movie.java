package com.example.kafka.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Movie extends Media {

    private final List<String> genres;
    private final String imdbId;
    private final Double rating;
    private final Set<String> tags;

    @JsonCreator
    public Movie(@JsonProperty("id") String id,
                 @JsonProperty("name") String name,
                 @JsonProperty("genres") List<String> genres,
                 @JsonProperty("imdbId") String imdbId,
                 @JsonProperty("rating") Double rating,
                 @JsonProperty("tags") Set<String> tags) {
        super(id, name);
        this.genres = genres;
        this.imdbId = imdbId;
        this.rating = rating;
        this.tags = tags;
    }

    private Movie(Movie movie, Double rating, Set<String> tags) {
        super(movie.id, movie.name);
        this.genres = movie.genres;
        this.imdbId = movie.imdbId;
        this.rating = rating;
        this.tags = tags;
    }


    public List<String> getGenres() {
        return genres;
    }

    public String getImdbId() {
        return imdbId;
    }

    public Double getRating() {
        return rating;
    }

    public Set<String> getTags() {
        return tags;
    }

    @JsonIgnore
    public boolean hasGenre(String genre) {
        return genres.stream().anyMatch(g -> g.equalsIgnoreCase(genre));
    }

    @JsonIgnore
    public Movie addRatingAndTags(Double rating, Set<String> tags) {
        return new Movie(this, rating, tags);
    }

    public static Movie fromMovieLine(String line) {
        String[] items = line.split(",");
        return new Movie(items[0], items[1], Arrays.asList(items[2].split("\\|")), items[3], null, null);
    }

    public static JsonSerde<Movie> serde() {
        return new JsonSerde<>(Movie.class);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genres=" + genres +
                ", imdbId=" + imdbId +
                ", rating=" + rating +
                ", tags=" + tags +
                '}';
    }
}
