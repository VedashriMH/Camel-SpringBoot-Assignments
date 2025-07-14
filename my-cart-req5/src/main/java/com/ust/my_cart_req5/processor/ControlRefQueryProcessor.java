package com.ust.my_cart_req5.processor;

import com.ust.my_cart_req5.constants.ApplicationConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ControlRefQueryProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ControlRefQueryProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> query = new HashMap<>();
        String key = exchange.getContext().resolvePropertyPlaceholders(ApplicationConstants.CONTROL_REF_KEY);
        query.put("_id", key);
        exchange.getIn().setBody(new Document(query));
        logger.info("Query for controlref: {}", exchange.getIn().getBody());
    }
}
