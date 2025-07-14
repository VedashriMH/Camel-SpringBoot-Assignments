package com.ust.my_cart_req5.processor;

import com.ust.my_cart_req5.constants.ApplicationConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ControlRefTimestampProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ControlRefTimestampProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> controlRef = exchange.getIn().getBody(Map.class);
        String lastProcessedTs = controlRef != null && controlRef.get("lastProcessTs") != null
                ? controlRef.get("lastProcessTs").toString()
                : ApplicationConstants.DEFAULT_TIMESTAMP;
        exchange.getIn().setHeader(ApplicationConstants.LAST_PROCESSED_TS_HEADER, lastProcessedTs);
        logger.info("Last processed timestamp: {}", lastProcessedTs);
    }
}
