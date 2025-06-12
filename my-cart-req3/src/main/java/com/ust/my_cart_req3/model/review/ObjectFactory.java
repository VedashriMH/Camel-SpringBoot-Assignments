package com.ust.my_cart_req3.model.review;

import com.ust.my_cart_req3.model.trend.Category;
import com.ust.my_cart_req3.model.trend.CategoryName;
import com.ust.my_cart_req3.model.trend.TrendInventory;
import com.ust.my_cart_req3.model.trend.TrendItem;
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