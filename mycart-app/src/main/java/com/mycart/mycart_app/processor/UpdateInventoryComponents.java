package com.mycart.mycart_app.processor;


import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.exception.ItemNotFoundException;
import com.mycart.mycart_app.exception.StockUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UpdateInventoryComponents implements Processor{

    @Override
    public void process(Exchange exchange) {
        Map<String, Object> body = exchange.getIn().getBody(Map.class);
        Object itemsObj = body != null ? body.get("items") : null;

        if (!(itemsObj instanceof List<?>)) {
            throw new IllegalArgumentException("'items' field must be a list.");
        }

        List<Map<String, Object>> validUpdates = new ArrayList<>();
        List<Map<String, Object>> failedUpdates = new ArrayList<>();

        for (Object obj : (List<?>) itemsObj) {
            if (obj instanceof Map<?, ?> item) {
                try {
                    String id = (String) item.get("_id");
                    Map<String, Object> stockDetails = (Map<String, Object>) item.get("stockDetails");

                    if (id == null || stockDetails == null) {
                        failedUpdates.add(Map.of(
                                "_id", id != null ? id : "unknown",
                                "status", "failed",
                                "reason", "Missing _id or stockDetails."
                        ));
                        continue;

                    }


                    int soldOut = Integer.parseInt(stockDetails.get("soldOut").toString());
                    int damaged = Integer.parseInt(stockDetails.get("damaged").toString());

                    if (soldOut < 0 || damaged < 0) {
                        failedUpdates.add(Map.of(
                                "_id", id,
                                "status", "failed",
                                "reason", "soldOut/damaged must be non-negative."
                        ));
                        continue;
                    }

                    validUpdates.add(Map.of("_id", id, "soldOut", soldOut, "damaged", damaged));

                } catch (Exception e) {

                    failedUpdates.add(Map.of(
                            "Error", "Error processing item: " + e.getMessage()
                    ));

                }
            }
        }

        if (validUpdates.isEmpty()) {
            throw new StockUpdateException("No valid items found in payload.");
        }

        exchange.setProperty(ApplicationConstants.PROPERTY_VALID_UPDATES, validUpdates);
        exchange.setProperty("successfulUpdates", new java.util.ArrayList<String>());
        exchange.setProperty("failedUpdates", failedUpdates);
    }


    public void prepareCurrentItem(Exchange exchange) {
        Map<String, Object> currentItem = exchange.getIn().getBody(Map.class);
        exchange.setProperty(ApplicationConstants.PROPERTY_CURRENT_ITEM, currentItem);
        exchange.getIn().setBody(currentItem.get("_id"));

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

    public void handleSuccess(Exchange exchange) {
        List<Map<String, Object>> success = exchange.getProperty("successfulUpdates", List.class);
        if (success == null) success = new ArrayList<>();
        Map<String, Object> item = exchange.getProperty(ApplicationConstants.PROPERTY_CURRENT_ITEM, Map.class);
        success.add(Map.of("_id", item.get("_id"), "status", "success"));
        exchange.setProperty("successfulUpdates", success);
    }

    public void handleFailure(Exchange exchange) {
        List<Map<String, Object>> failed = exchange.getProperty("failedUpdates", List.class);
        if (failed == null) failed = new ArrayList<>();
        Map<String, Object> item = exchange.getProperty(ApplicationConstants.PROPERTY_CURRENT_ITEM, Map.class);
        Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        failed.add(Map.of(
                "_id", item != null ? item.getOrDefault("_id", "unknown") : "unknown",
                "status", "failed",
                "reason", ex != null ? ex.getMessage() : "Unknown error"
        ));

        exchange.setProperty("failedUpdates", failed);
        exchange.getIn().removeHeader(Exchange.EXCEPTION_CAUGHT);
    }

    public void buildFinalResponse(Exchange exchange) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> success = exchange.getProperty("successfulUpdates", List.class);
        List<Map<String, Object>> failed = exchange.getProperty("failedUpdates", List.class);
        response.put("successfulUpdates", success != null ? success : List.of());
        response.put("failedUpdates", failed != null ? failed : List.of());

        exchange.getMessage().setBody(response);
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE,
                (success == null || success.isEmpty()) ? 400 :
                        (failed != null && !failed.isEmpty()) ? 207 : 202);
    }
}


