package com.ust.my_cart_req3.processor;


import com.ust.my_cart_req3.constants.ApplicationConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemsQueryProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ItemsQueryProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String lastProcessedTs = exchange.getIn().getHeader(ApplicationConstants.LAST_PROCESSED_TS_HEADER, String.class);
        Document query = new Document("lastUpdateDate", new Document("$gt", lastProcessedTs));
        exchange.getIn().setBody(query);
        logger.info("Query for items: {}", query);
    }
}