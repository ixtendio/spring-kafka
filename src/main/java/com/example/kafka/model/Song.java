package com.example.kafka.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Song extends Media {

    private final int year;
    private final String album;

    @JsonCreator
    public Song(@JsonProperty("year") int year,
                @JsonProperty("id") String id,
                @JsonProperty("album") String album,
                @JsonProperty("name") String name) {
        super(id, name);
        this.year = year;
        this.album = album;
    }

    public int getYear() {
        return year;
    }

    public String getAlbum() {
        return album;
    }

    public static Song fromLine(String line) {
        String[] items = line.split("<SEP>");
        return new Song(Integer.parseInt(items[0]), items[1], items[2], items[3]);
    }

    @Override
    public String toString() {
        return "Song{" +
                "year=" + year +
                ", id='" + id + '\'' +
                ", album='" + album + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
