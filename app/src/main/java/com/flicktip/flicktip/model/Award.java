package com.flicktip.flicktip.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Cinthia on 06/02/2016.
 */
public class Award implements Serializable {

    private Long id;
    private String name;
    private List<Edition> editions;

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

    public List<Edition> getEditions() {
        return editions;
    }

    public void setEditions(List<Edition> editions) {
        this.editions = editions;
    }
}
