package com.flicktip.flicktip.model;

import java.io.Serializable;

/**
 * Created by Cinthia on 06/02/2016.
 */
public class Nomination implements Serializable {

    private Long id;
    private String name;
    private Movie movie;
    private Category category;
    private Boolean winner;

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    private Long getId() {
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
        return getId() + " - " + getName();
    }
}
