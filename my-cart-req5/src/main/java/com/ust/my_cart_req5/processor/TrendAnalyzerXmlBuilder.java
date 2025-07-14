package com.ust.my_cart_req5.processor;

import com.mongodb.client.MongoClient;
import com.ust.my_cart_req5.model.trend.Category;
import com.ust.my_cart_req5.model.trend.CategoryName;
import com.ust.my_cart_req5.model.trend.TrendInventory;
import com.ust.my_cart_req5.model.trend.TrendItem;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;

public class TrendAnalyzerXmlBuilder implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(TrendAnalyzerXmlBuilder.class);

    @Autowired
    private MongoClient mongoClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        // Get the MongoDB item document
        Map<String, Object> itemMap = (Map<String, Object>) exchange.getIn().getBody();
        if (itemMap == null) {
            logger.error("Item document is null in exchange body");
            throw new IllegalStateException("Item document is null");
        }

        // Extract item ID
        String itemId = (String) itemMap.get("_id");
        if (itemId == null) {
            logger.error("Item ID is null in document: {}", itemMap);
            throw new IllegalStateException("Item ID is null");
        }

        // Create Item
        TrendItem item = new TrendItem();
        item.setItemId(itemId);

        // Get categoryId with fallback
        String categoryId = (String) itemMap.get("categoryId");
        if (categoryId == null) {
            categoryId = "unknown";
            logger.warn("No categoryId for item {}; using fallback: {}", itemId, categoryId);
        }
        item.setCategoryId(categoryId);

        // Extract stock and price details with null checks
        Map<String, Object> stockDetails = (Map<String, Object>) itemMap.get("stockDetails");
        Map<String, Object> itemPrice = (Map<String, Object>) itemMap.get("itemPrice");
        int availableStock = 0;
        int sellingPrice = 0;
        try {
            if (stockDetails != null && stockDetails.get("availableStock") != null) {
                availableStock = ((Number) stockDetails.get("availableStock")).intValue();
            } else {
                logger.warn("No availableStock for item {}; defaulting to 0", itemId);
            }
            if (itemPrice != null && itemPrice.get("sellingPrice") != null) {
                sellingPrice = ((Number) itemPrice.get("sellingPrice")).intValue();
            } else {
                logger.warn("No sellingPrice for item {}; defaulting to 0", itemId);
            }
        } catch (ClassCastException e) {
            logger.error("Error casting stock or price for item {}: {}", itemId, e.getMessage());
            availableStock = 0;
            sellingPrice = 0;
        }
        item.setAvailableStock(availableStock);
        item.setSellingPrice(sellingPrice);

        // Fetch categoryName from categories collection
        String catName = (String) itemMap.get("categoryName");
        // Create CategoryName
        CategoryName categoryName = new CategoryName();
        categoryName.setName(catName);

        // Create Category
        Category category = new Category();
        category.setId(categoryId);
        category.setCategoryName(categoryName);
        category.setItems(Collections.singletonList(item));

        // Create Inventory
        TrendInventory inventory = new TrendInventory();
        inventory.setCategoryList(Collections.singletonList(category));

        // Set the transformed object and header
        exchange.getIn().setBody(inventory);
        exchange.getIn().setHeader("CamelOid", itemId);
        logger.info("Transformed item {} to trend XML", itemId);
    }
}