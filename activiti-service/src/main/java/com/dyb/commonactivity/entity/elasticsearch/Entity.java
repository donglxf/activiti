package com.dyb.commonactivity.entity.elasticsearch;


import java.io.Serializable;

//@Document(indexName = "index_book", type = "book")
public class Entity implements Serializable {
    public static final String INDEX_NAME = "index_book";

    public static final String TYPE = "book";

    private Long id;

    private String name;

    public Entity() {
        super();
    }

    public Entity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
