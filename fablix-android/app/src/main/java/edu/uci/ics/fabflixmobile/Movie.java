package edu.uci.ics.fabflixmobile;

public class Movie {
    private String title;
    private String year;
    private String director;
    private String genres;
    private String stars;


    public Movie(String title, String year, String director, String genres, String stars) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getTitle() {
        return title;
    }
    public String getYear() {
        return year;
    }
    public String getDirector() {
        return director;
    }
    public String getStars() {
        return stars;
    }
    public String getGenres() {
        return genres;
    }
}