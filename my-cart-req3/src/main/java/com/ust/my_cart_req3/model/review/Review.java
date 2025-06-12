package com.ust.my_cart_req3.model.review;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Review {
    private int reviewrating;
    private String reviewcomment;

    public Review() {
    }

    public Review(int reviewrating, String reviewcomment) {
        this.reviewrating = reviewrating;
        this.reviewcomment = reviewcomment;
    }

    public int getReviewrating() {
        return reviewrating;
    }

    public void setReviewrating(int reviewrating) {
        this.reviewrating = reviewrating;
    }

    public String getReviewcomment() {
        return reviewcomment;
    }

    public void setReviewcomment(String reviewcomment) {
        this.reviewcomment = reviewcomment;
    }
}
