package com.ust.my_cart_req5.processor;

import com.ust.my_cart_req5.constants.ApplicationConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ControlRefUpdateProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ControlRefUpdateProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> controlRefUpdate = new HashMap<>();
        String key = exchange.getContext().resolvePropertyPlaceholders(ApplicationConstants.CONTROL_REF_KEY);
        controlRefUpdate.put("_id", key);
        String latestUpdateDate = exchange.getProperty(ApplicationConstants.FINAL_LATEST_UPDATE_DATE_PROPERTY, String.class);
        if (latestUpdateDate == null) {
            logger.warn("No latestUpdateDate found; using last processed timestamp.");
            latestUpdateDate = exchange.getIn().getHeader(ApplicationConstants.LAST_PROCESSED_TS_HEADER, String.class);
        }
        controlRefUpdate.put("lastProcessTs", latestUpdateDate);
        exchange.getIn().setBody(new Document(controlRefUpdate));
        logger.info("Control ref update: {}", exchange.getIn().getBody());
    }
}
