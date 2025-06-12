package com.ust.my_cart_req3.processor;

import com.ust.my_cart_req3.constants.ApplicationConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LatestTimestampCaptureProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(LatestTimestampCaptureProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        List<Map<String, Object>> items = exchange.getIn().getBody(List.class);
        String latestTs = items.stream()
                .map(item -> (String) item.get("lastUpdateDate"))
                .filter(Objects::nonNull)
                .max(String::compareTo)
                .orElse(null);
        exchange.setProperty(ApplicationConstants.LATEST_UPDATE_DATE_PROPERTY, latestTs);
        logger.info("Captured latestUpdateDate before split: {}", latestTs);
    }
}
