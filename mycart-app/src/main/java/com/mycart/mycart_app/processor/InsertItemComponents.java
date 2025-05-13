package com.mycart.mycart_app.processor;

import com.mycart.mycart_app.exception.ItemInsertException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InsertItemComponents {

    public void validatePayload(Exchange exchange) {
        Object body = exchange.getIn().getBody();
        if (!(body instanceof Map)) {
            throw new ItemInsertException("Invalid payload. Expected a JSON object.");
        }

        Map<String, Object> item = (Map<String, Object>) body;
        Object priceObj = item.get("itemPrice");
        if (!(priceObj instanceof Map)) {
            throw new ItemInsertException("Missing or invalid 'itemPrice' field.");
        }

        Map<String, Object> itemPrice = (Map<String, Object>) priceObj;

        double basePrice = Double.parseDouble(itemPrice.getOrDefault("basePrice", "0").toString());
        double sellingPrice = Double.parseDouble(itemPrice.getOrDefault("sellingPrice", "0").toString());

        if (basePrice <= 0 || sellingPrice <= 0) {
            throw new ItemInsertException("Base price and selling price must be greater than zero.");
        }

        exchange.setProperty("item", item);
        exchange.getIn().setBody(item.get("categoryId"));
    }

    public void validateCategory(Exchange exchange) {
        Document category = exchange.getIn().getBody(Document.class);
        Map<String, Object> item = exchange.getProperty("item", Map.class);

        if (category == null) {
            throw new ItemInsertException("Invalid category ID: " + item.get("categoryId"));
        }

        exchange.getIn().setBody(item.get("_id"));
    }

    public void validateDuplicate(Exchange exchange) {
        Document existingItem = exchange.getIn().getBody(Document.class);
        Map<String, Object> item = exchange.getProperty("item", Map.class);

        if (existingItem != null) {
            throw new ItemInsertException("Item already exists with ID: " + item.get("_id"));
        }

        exchange.getIn().setBody(item);
    }

    public void errorResponse(Exchange exchange) {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", exception.getMessage());
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setBody(errorResponse);
    }

    public void finalResponse(Exchange exchange) {
        exchange.getIn().setBody(Map.of("message", "Item inserted successfully."));
    }
}

