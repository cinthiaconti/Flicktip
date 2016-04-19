package com.flicktip.flicktip.model;

import java.util.List;

/**
 * Created by Cinthia on 11/02/2016.
 */
public class Nominee {

    private Long id;
    private String name;
    private List<Nomination> nominations;

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
}
