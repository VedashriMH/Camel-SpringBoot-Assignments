package com.mycart.mycart_app.route;

// UpdateInventoryAsyncRoute.java
import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.processor.UpdateInventoryComponents;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public class UpdateInventoryAsyncRoute extends RouteBuilder {

    @Override
    public void configure() {

            from("direct:update-inventory-async")
                    .routeId("route-inventory-update-rest")
                    .log(LoggingLevel.INFO, "Received batch inventory update request from REST endpoint.")
                    .unmarshal().json()
                    .log(LoggingLevel.DEBUG, "Unmarshalled request payload: ${body}")

                    .log(LoggingLevel.INFO, "Validating inventory items and preparing valid updates.")
                    .process("updateInventoryComponents") // validates & sets PROPERTY_VALID_UPDATES
                    .doTry()
                    .log(LoggingLevel.INFO, "Splitting valid inventory updates for asynchronous processing.")
                    .split(simple("${exchangeProperty." + ApplicationConstants.PROPERTY_VALID_UPDATES + "}"))
                    .log(LoggingLevel.DEBUG, "Sending inventory update message to ActiveMQ: ${body}")

                    .marshal().json()
                    .to("activemq:queue:inventory.update.queue?exchangePattern=InOnly&deliveryMode=2")
                    .endDoTry()
                    .log(LoggingLevel.INFO, "All valid inventory updates sent to queue successfully.")
                    .doCatch(Exception.class)
                    .log(LoggingLevel.ERROR, "Batch send to ActiveMQ queue failed: ${exception.message}")
                    .setBody(constant("Inventory update failed. Messaging error occurred."))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                    .stop()
                    .end()
                    .log(LoggingLevel.INFO, "Inventory update request accepted for asynchronous processing.")
                    .setBody(constant("Inventory update request accepted for async processing"))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202));



    }
}


