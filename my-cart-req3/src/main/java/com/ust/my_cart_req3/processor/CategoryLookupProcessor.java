package com.ust.my_cart_req3.processor;

import com.ust.my_cart_req3.constants.ApplicationConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.apache.camel.builder.Builder.simple;

public class CategoryLookupProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(CategoryLookupProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> item = exchange.getIn().getBody(Map.class);
        String categoryId = (String) item.get("categoryId");
        logger.info("Looking up category with ID: {}", categoryId);
        exchange.getIn().setBody(new Document("_id", categoryId));
        exchange.getIn().setHeader(ApplicationConstants.MONGO_COLLECTION_HEADER, simple(ApplicationConstants.CATEGORIES_COLLECTION));
    }
}
