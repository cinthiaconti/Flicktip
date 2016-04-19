package com.flicktip.flicktip.model;

import java.io.Serializable;

/**
 * Created by Cinthia on 06/02/2016.
 */
public class Movie implements Serializable {


    //attributes SQLite
    private Long id;
    private String dbTitle;
    private Boolean viewed;
    private Boolean bookmark;
    private Boolean favorite;
    private String year;

    //attributes TMDB
    private Long tmdbId;
    private String originalTitle;
    private String overview;
    private String releaseDate;
    private String title;
    private String posterPath;
    private String backdropPath;
    private Integer runtime;


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDbTitle() {
        return dbTitle;
    }

    public void setDbTitle(String dbTitle) {
        this.dbTitle = dbTitle;
    }

    public Boolean getViewed() {
        return viewed;
    }

    public void setViewed(Integer viewed) {
        this.viewed = viewed != 0;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public void setBookmark(Boolean bookmark) {
        this.bookmark = bookmark;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(Integer bookmark) {
        this.bookmark = bookmark != 0;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite != 0;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    @Override
    public String toString() {
        return getDbTitle();
    }
}
