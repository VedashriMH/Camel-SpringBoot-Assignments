package com.ust.my_cart_req5.model.review;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    // Example methods — replace with actual model classes
    public Review createReview() {
        return new Review();
    }

    public Reviews createReviews() {
        return new Reviews();
    }

    public ReviewItem createReviewItem() {
        return new ReviewItem();
    }

    // Add factory methods for each JAXB-annotated class in this package
}