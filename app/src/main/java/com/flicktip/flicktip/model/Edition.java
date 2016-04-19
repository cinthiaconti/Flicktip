package com.flicktip.flicktip.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Cinthia on 07/02/2016.
 */
public class Edition implements Serializable {

    private Long id;
    private String name;
    private List<Category> categories;
    private List<Integer> progress;

    public List<Integer> getProgress() {
        return progress;
    }

    public void setProgress(List<Integer> progress) {
        this.progress = progress;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
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

    public void setName(String year) {
        this.name = year;
    }

    @Override
    public String toString() {
        return getName();
    }
}
