package com.mycart.mycart_app.processor;

import com.mycart.mycart_app.exception.ItemInsertException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InsertItemComponents implements Processor{

    @Override
    public void process(Exchange exchange) {
        Object body = exchange.getIn().getBody();

        // Check if payload is a JSON object (Map)
        if (!(body instanceof Map)) {
            throw new ItemInsertException("Invalid payload. Expected a JSON object.");
        }

        Map<String, Object> item = (Map<String, Object>) body;

        // Define required top-level keys
        Set<String> requiredKeys = Set.of(
                "_id", "itemName", "categoryId", "itemPrice", "stockDetails", "specialProduct", "review"
        );

        // Check for unexpected keys
        Set<String> actualKeys = item.keySet();
        // Check for unexpected fields
        Set<String> unexpectedKeys = actualKeys.stream()
                .filter(key -> !requiredKeys.contains(key))
                .collect(Collectors.toSet());

        if (!unexpectedKeys.isEmpty()) {
            throw new ItemInsertException("Unexpected field(s) found in payload: " + String.join(", ", unexpectedKeys));
        }

        // Check for missing fields
        Set<String> missingKeys = requiredKeys.stream()
                .filter(key -> !item.containsKey(key))
                .collect(Collectors.toSet());

        if (!missingKeys.isEmpty()) {
            throw new ItemInsertException("Missing required field(s): " + String.join(", ", missingKeys));
        }


        // Validate _id
        if (!(item.get("_id") instanceof String) || ((String) item.get("_id")).isBlank()) {
            throw new ItemInsertException("Field '_id' must be a non-empty string.");
        }

        // Validate itemName
        if (!(item.get("itemName") instanceof String) || ((String) item.get("itemName")).isBlank()) {
            throw new ItemInsertException("Field 'itemName' must be a non-empty string.");
        }

        // Validate categoryId
        if (!(item.get("categoryId") instanceof String) || ((String) item.get("categoryId")).isBlank()) {
            throw new ItemInsertException("Field 'categoryId' must be a non-empty string.");
        }

        // Automatically set the current timestamp for lastUpdateDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimestamp = LocalDateTime.now().format(formatter);
        item.put("lastUpdateDate", currentTimestamp);


        // Validate itemPrice
        Object priceObj = item.get("itemPrice");
        if (!(priceObj instanceof Map)) {
            throw new ItemInsertException("Field 'itemPrice' must be a JSON object.");
        }

        Map<String, Object> itemPrice = (Map<String, Object>) priceObj;

        if (!itemPrice.containsKey("basePrice") || !itemPrice.containsKey("sellingPrice")) {
            throw new ItemInsertException("Missing 'basePrice' or 'sellingPrice' in 'itemPrice'.");
        }

        double basePrice, sellingPrice;
        try {
            basePrice = Double.parseDouble(itemPrice.get("basePrice").toString());
            sellingPrice = Double.parseDouble(itemPrice.get("sellingPrice").toString());
        } catch (NumberFormatException e) {
            throw new ItemInsertException("Invalid number format in 'itemPrice'.");
        }

        if (basePrice <= 0 || sellingPrice <= 0) {
            throw new ItemInsertException("Base price and selling price must be greater than zero.");
        }

        // Validate stockDetails
        Object stockObj = item.get("stockDetails");
        if (!(stockObj instanceof Map)) {
            throw new ItemInsertException("Field 'stockDetails' must be a JSON object.");
        }

        Map<String, Object> stockDetails = (Map<String, Object>) stockObj;

        if (!stockDetails.containsKey("availableStock") || !stockDetails.containsKey("unitOfMeasure")) {
            throw new ItemInsertException("Missing 'availableStock' or 'unitOfMeasure' in 'stockDetails'.");
        }

        try {
            int availableStock = Integer.parseInt(stockDetails.get("availableStock").toString());
            if (availableStock < 0) {
                throw new ItemInsertException("'availableStock' must be a non-negative integer.");
            }
        } catch (NumberFormatException e) {
            throw new ItemInsertException("Invalid number format for 'availableStock'.");
        }

        if (!(stockDetails.get("unitOfMeasure") instanceof String)) {
            throw new ItemInsertException("'unitOfMeasure' must be a string.");
        }

        // Validate specialProduct
        if (!(item.get("specialProduct") instanceof Boolean)) {
            throw new ItemInsertException("Field 'specialProduct' must be a boolean.");
        }

        // Validate review
        Object reviewObj = item.get("review");
        if (!(reviewObj instanceof List<?>)) {
            throw new ItemInsertException("Field 'review' must be a list of review objects.");
        }

        List<?> reviewList = (List<?>) reviewObj;

        boolean hasInvalidReview = reviewList.stream().anyMatch(review -> {
            if (!(review instanceof Map)) {
                return true; // invalid type
            }
            Map<?, ?> reviewMap = (Map<?, ?>) review;
            return !reviewMap.containsKey("rating") || !reviewMap.containsKey("comment");
        });

        if (hasInvalidReview) {
            throw new ItemInsertException("Each review must be a JSON object containing 'rating' and 'comment'.");
        }


        // Everything is valid
        exchange.setProperty("item", item);
        exchange.getIn().setBody(item.get("categoryId"));
    }


    public void finalResponse(Exchange exchange) {
        exchange.getIn().setBody(Map.of("message", "Item inserted successfully."));
    }

}

