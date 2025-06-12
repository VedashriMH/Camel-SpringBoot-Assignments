package com.mycart.mycart_app.model;

public class Review {

    private String rating;
    private String comment;

    public Review() {
    }

    public Review(String rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
