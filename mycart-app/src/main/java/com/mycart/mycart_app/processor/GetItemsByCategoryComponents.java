package com.mycart.mycart_app.processor;


import com.mycart.mycart_app.exception.CategoryNotFoundException;
import com.mycart.mycart_app.exception.ItemNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GetItemsByCategoryComponents {


    public void itemProcessor(Exchange exchange) {
        String categoryId = exchange.getProperty("categoryId", String.class);
        String includeSpecial = exchange.getProperty("includeSpecial", "true", String.class);
        boolean filterSpecial = !Boolean.parseBoolean(includeSpecial);

        List<Document> allItems = exchange.getIn().getBody(List.class);
        Document category = exchange.getIn().getHeader("categoryDoc", Document.class);

        if (category == null) {
            throw new CategoryNotFoundException("Invalid categoryId: " + categoryId);
        }

        List<Document> filteredItems = allItems.stream()
                .filter(doc -> categoryId.equals(doc.getString("categoryId")))
                .filter(doc -> !filterSpecial || Boolean.FALSE.equals(doc.getBoolean("specialProduct")))
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("categoryName", category.getString("categoryName"));
        response.put("categoryDepartment", category.getString("categoryDep"));

        if (filteredItems.isEmpty()) {
            response.put("message", "No items found in this category.");
        } else {
            response.put("items", filteredItems);
        }

        exchange.getIn().setBody(response);
    }

    public void errorResponseProcessor(Exchange exchange) {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", exception.getMessage());
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setBody(errorResponse);
    }

    public void finalResponseProcessor(Exchange exchange) {
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
    }
}

