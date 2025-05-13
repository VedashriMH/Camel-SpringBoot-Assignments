package com.mycart.mycart_app.processor;

import com.mycart.mycart_app.exception.ItemNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class GetItemByIdComponents {


    public void itemProcessor(Exchange exchange) {
        // The Mongo query result will already be set in the body
        Document item = exchange.getIn().getBody(Document.class);

        if (item == null) {
            String id = exchange.getIn().getHeader("id", String.class); // Optional: To include ID in error
            throw new ItemNotFoundException("Item not found for ID: " + id);
        }

        // If needed, you can modify or log the item here
        exchange.getIn().setBody(item);
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

