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
        String routeStartTimestamp = exchange.getProperty(ApplicationConstants.ROUTE_START_TIMESTAMP, String.class);
        if (routeStartTimestamp == null) {
            logger.warn("No route start timestamp found; using last processed timestamp as fallback.");
            routeStartTimestamp = exchange.getIn().getHeader(ApplicationConstants.LAST_PROCESSED_TS_HEADER, String.class);
        }
        controlRefUpdate.put("lastProcessTs", routeStartTimestamp);
        exchange.getIn().setBody(new Document(controlRefUpdate));
        logger.info("Control ref update with route start timestamp: {}", exchange.getIn().getBody());
    }
}
