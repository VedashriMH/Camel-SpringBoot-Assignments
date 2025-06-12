package com.ust.my_cart_req3.processor;

import com.ust.my_cart_req3.constants.ApplicationConstants;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LatestTimestampAggregationStrategy implements AggregationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(LatestTimestampAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            Object item = newExchange.getIn().getBody();
            if (item instanceof Map) {
                Map<String, Object> itemMap = (Map<String, Object>) item;
                String lastUpdateDate = itemMap.get("lastUpdateDate") != null ? itemMap.get("lastUpdateDate").toString() : null;
                newExchange.setProperty(ApplicationConstants.LATEST_UPDATE_DATE_PROPERTY, lastUpdateDate);
                logger.info("Initialized latestUpdateDate: {}", lastUpdateDate);
            }
            return newExchange;
        }

        Object item = newExchange.getIn().getBody();
        if (item instanceof Map) {
            Map<String, Object> itemMap = (Map<String, Object>) item;
            String currentTs = itemMap.get("lastUpdateDate") != null ? itemMap.get("lastUpdateDate").toString() : null;
            String storedTs = oldExchange.getProperty(ApplicationConstants.LATEST_UPDATE_DATE_PROPERTY, String.class);
            if (currentTs != null && (storedTs == null || currentTs.compareTo(storedTs) > 0)) {
                oldExchange.setProperty(ApplicationConstants.LATEST_UPDATE_DATE_PROPERTY, currentTs);
                logger.info("Updated latestUpdateDate to: {}", currentTs);
            }
        }
        oldExchange.setProperty(ApplicationConstants.FINAL_LATEST_UPDATE_DATE_PROPERTY, oldExchange.getProperty(ApplicationConstants.LATEST_UPDATE_DATE_PROPERTY));
        return oldExchange;
    }
}
