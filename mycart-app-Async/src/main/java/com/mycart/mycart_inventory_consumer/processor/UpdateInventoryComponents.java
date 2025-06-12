package com.mycart.mycart_inventory_consumer.processor;


import com.mycart.mycart_inventory_consumer.constants.ApplicationConstants;
import com.mycart.mycart_inventory_consumer.exception.ItemNotFoundException;
import com.mycart.mycart_inventory_consumer.exception.StockUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.util.Map;

@Component
public class UpdateInventoryComponents {


    public void validateSingleItem(Exchange exchange) {
        Map<String, Object> item = exchange.getIn().getBody(Map.class);
        if (item == null || item.get("_id") == null || item.get("soldOut") == null || item.get("damaged") == null) {
            throw new IllegalArgumentException("Missing required fields in item.");
        }
    }


    public void prepareCurrentItem(Exchange exchange) {
        Map<String, Object> currentItem = exchange.getIn().getBody(Map.class);
        exchange.setProperty(ApplicationConstants.PROPERTY_CURRENT_ITEM, currentItem);
        exchange.getIn().setHeader("itemId", currentItem.get("_id"));
        exchange.getIn().setHeader("CamelMongoDbCriteria",
                String.format("{ \"_id\": \"%s\" }", currentItem.get("_id")));
    }



    public void calculateStock(Exchange exchange) {
        Document itemDoc = exchange.getIn().getBody(Document.class);

        if (itemDoc == null) {
            throw new ItemNotFoundException("Item not found or invalid.");
        }
        Map<String, Object> item = exchange.getProperty(ApplicationConstants.PROPERTY_CURRENT_ITEM, Map.class);

        int soldOut = (int) item.get("soldOut");
        int damaged = (int) item.get("damaged");

        Document stock = (Document) itemDoc.get("stockDetails");
        int available = stock.getInteger("availableStock", 0);
        int newStock = available - soldOut - damaged;

        if (newStock < 0) throw new StockUpdateException("Stock would go below zero.");

        exchange.setProperty("newAvailableStock", newStock);
    }


    public void prepareUpdateQuery(Exchange exchange) {
        Map<String, Object> currentItem = exchange.getProperty(ApplicationConstants.PROPERTY_CURRENT_ITEM, Map.class);
        Document itemDoc = exchange.getIn().getBody(Document.class);

        int newStock = exchange.getProperty("newAvailableStock", Integer.class);

        Document stockDetails = (Document) itemDoc.get("stockDetails");
        stockDetails.put("availableStock", newStock);
        itemDoc.put("lastUpdateDate", LocalDateTime.now().toString());

        exchange.getIn().setBody(itemDoc);
    }

    public void handleFailure(Exchange exchange) {
        Map<String, Object> item = exchange.getProperty(ApplicationConstants.PROPERTY_CURRENT_ITEM, Map.class);
        Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        System.err.println("Failed to update item ID: " +
                (item != null ? item.get("_id") : "unknown") +
                " | Reason: " + (ex != null ? ex.getMessage() : "Unknown error"));


    }


}


