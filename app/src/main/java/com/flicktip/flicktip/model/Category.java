package com.flicktip.flicktip.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Cinthia on 06/02/2016.
 */
public class Category implements Serializable {

    private Long id;
    private String name;
    private List<Nomination> nominations;
    private List<Integer> progress;

    public List<Integer> getProgress() {
        return progress;
    }

    public void setProgress(List<Integer> progress) {
        this.progress = progress;
    }

    public List<Nomination> getNominations() {
        return nominations;
    }

    public void setNominations(List<Nomination> nominations) {
        this.nominations = nominations;
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

    @Override
    public String toString() {
        return getName();
    }
}
