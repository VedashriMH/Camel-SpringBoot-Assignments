package com.ust.my_cart_req5.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CategoryEnrichmentProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(CategoryEnrichmentProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> item = exchange.getIn().getBody(Map.class);
        Map<String, Object> category = exchange.getProperty("category", Map.class);

        logger.info("Fetched category for ID {}: {}", item.get("categoryId"), category);

        if (category != null && category.get("categoryName") != null) {
            item.put("categoryName", category.get("categoryName"));
        } else {
            item.put("categoryName", "Unknown");
            logger.warn("No categoryName found for categoryId {} for item {}", item.get("categoryId"), item.get("_id"));
        }

        exchange.getIn().setBody(item);
    }
}