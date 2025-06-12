package com.mycart.mycart_app.processor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycart.mycart_app.exception.CategoryNotFoundException;
import com.mycart.mycart_app.exception.ItemNotFoundException;
import com.mycart.mycart_app.model.Item;
import com.mycart.mycart_app.model.ItemResponse;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class GetItemByIdComponents implements Processor{

    private final ObjectMapper objectMapper;

    @Autowired
    public GetItemByIdComponents(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Override
    public void process(Exchange exchange) throws Exception {
        Document itemDoc = exchange.getProperty("itemDoc", Document.class);
        String categoryName = exchange.getProperty("categoryName", String.class);

        if (categoryName == null) {
            String categoryId = itemDoc.getString("categoryId");
            throw new CategoryNotFoundException("Invalid categoryId: " + categoryId);
        }

        Item item = objectMapper.readValue(itemDoc.toJson(), Item.class);

        ItemResponse response = new ItemResponse();
        response.set_id(item.get_id());
        response.setItemName(item.getItemName());
        response.setItemPrice(item.getItemPrice());
        response.setStockDetails(item.getStockDetails());
        response.setSpecialProduct(item.isSpecialProduct());
        response.setCategoryName(categoryName);

        exchange.getIn().setBody(response);
    }
}





