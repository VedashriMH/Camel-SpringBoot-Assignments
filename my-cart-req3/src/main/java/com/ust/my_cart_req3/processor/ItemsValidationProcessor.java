package com.ust.my_cart_req3.processor;

import com.ust.my_cart_req3.constants.ApplicationConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ItemsValidationProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ItemsValidationProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        List<?> items = exchange.getIn().getBody(List.class);
        logger.info("Total items found: {} items", items != null ? items.size() : 0);
        if (items == null || items.isEmpty()) {
            logger.warn("No items found for processing. Check lastUpdateDate in items collection.");
            exchange.getIn().setHeader(ApplicationConstants.ITEMS_FOUND_HEADER, false);
        } else {
            logger.info("Items to process: {}", items);
            exchange.getIn().setHeader(ApplicationConstants.ITEMS_FOUND_HEADER, true);
        }
    }
}
