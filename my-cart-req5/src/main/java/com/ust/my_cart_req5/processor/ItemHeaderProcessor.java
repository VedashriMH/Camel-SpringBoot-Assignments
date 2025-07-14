package com.ust.my_cart_req5.processor;

import com.ust.my_cart_req5.constants.ApplicationConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ItemHeaderProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(ItemHeaderProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> item = (Map) exchange.getIn().getBody();
        String id = (String) item.get("_id");
        exchange.getIn().setHeader(ApplicationConstants.CAMEL_OID_HEADER, id);
        logger.info("Processing item: {}, CamelOid: {}", item, id);
    }
}
