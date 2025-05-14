package com.mycart.mycart_app.route;

// UpdateInventoryAsyncRoute.java
import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.processor.UpdateInventoryComponents;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UpdateInventoryAsyncRoute extends RouteBuilder {

    @Override
    public void configure() {

        // Step 1: REST endpoint to push payload to ActiveMQ
        from("direct:update-inventory-async")
                .routeId("route-inventory-update-rest")
                .log("Received async inventory update request")
                .setBody(simple("${body}"))
                .to("activemq:queue:inventory.update.queue?exchangePattern=InOnly&deliveryMode=2")
                .setBody(constant("Inventory update request accepted for async processing"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202));

    }
}

