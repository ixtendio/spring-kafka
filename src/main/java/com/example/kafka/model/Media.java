package com.example.kafka.model;

public class Media {

    protected final String id;
    protected final String name;

    public Media(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
