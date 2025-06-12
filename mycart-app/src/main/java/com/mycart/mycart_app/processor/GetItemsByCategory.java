package com.mycart.mycart_app.processor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycart.mycart_app.exception.CategoryNotFoundException;
import com.mycart.mycart_app.model.CategoryItemsResponse;
import com.mycart.mycart_app.model.Item;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component("getItemsByCategory")
public class GetItemsByCategory implements Processor {

    private final ObjectMapper objectMapper;

    @Autowired
    public GetItemsByCategory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Override
    public void process(Exchange exchange) throws Exception {
        String categoryId = exchange.getProperty("categoryId", String.class);
        Document categoryDoc = exchange.getIn().getBody(Document.class);

        List<Document> itemDocs = exchange.getProperty("filteredItems", List.class);
        List<Item> items = itemDocs.stream()
                .map(doc -> objectMapper.convertValue(doc, Item.class))
                .collect(Collectors.toList());

        CategoryItemsResponse response = new CategoryItemsResponse();
        response.setCategoryName(categoryDoc.getString("categoryName"));
        response.setCategoryDepartment(categoryDoc.getString("categoryDep"));

        if (items.isEmpty()) {
            response.setMessage("No items found in this category.");
        } else {
            response.setItems(items);
        }

        exchange.getIn().setBody(response);

    }
}


