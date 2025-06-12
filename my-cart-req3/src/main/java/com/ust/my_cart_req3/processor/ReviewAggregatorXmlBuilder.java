package com.ust.my_cart_req3.processor;

import com.ust.my_cart_req3.model.review.Review;
import com.ust.my_cart_req3.model.review.ReviewItem;
import com.ust.my_cart_req3.model.review.Reviews;
import com.ust.my_cart_req3.model.trend.TrendItem;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReviewAggregatorXmlBuilder implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ReviewAggregatorXmlBuilder.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        // Get the MongoDB document
        Map<String, Object> itemMap = exchange.getIn().getBody(Map.class);
        String itemId = (String) itemMap.get("_id");

        // Create Review objects from review array
        List<Review> reviewList = new ArrayList<>();
        List<Map<String, Object>> reviews = (List<Map<String, Object>>) itemMap.get("review"); // Correct field name
        if (reviews != null) {
            for (Map<String, Object> reviewMap : reviews) {
                Review review = new Review();
                String ratingStr = (String) reviewMap.get("rating");
                short rating = 0; // Default fallback
                try {
                    // Parse string rating (e.g., "2", "4.5") to unsignedByte
                    double ratingValue = Double.parseDouble(ratingStr);
                    rating = (short) Math.round(ratingValue); // Round decimals (e.g., 4.5 → 4)
                    if (rating < 0 || rating > 255) {
                        throw new IllegalArgumentException("Rating out of range: " + rating);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid rating '{}' for item {}: {}", ratingStr, itemId, e.getMessage());
                    rating = 0; // Fallback
                }
                review.setReviewrating(rating);
                review.setReviewcomment((String) reviewMap.get("comment"));
                reviewList.add(review);
            }
        } else {
            logger.warn("No reviews found for item {}", itemId);
        }

        // Create Item
        ReviewItem item = new ReviewItem();
        item.setId(itemId);
        item.setReviews(reviewList);

        // Create Reviews
        Reviews reviewsObj = new Reviews();
        reviewsObj.setItems(Collections.singletonList(item));

        // Set the transformed object as the body
        exchange.getIn().setBody(reviewsObj);
        // Header is already set by CamelMongo component, but confirm
        exchange.getIn().setHeader("CamelMongoOid", itemId);
    }
}
