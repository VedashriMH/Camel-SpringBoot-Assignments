// UpdateInventoryConsumerRoute.java
package com.mycart.mycart_inventory_consumer.route;

import com.mycart.mycart_inventory_consumer.constants.ApplicationConstants;
import com.mycart.mycart_inventory_consumer.processor.UpdateInventoryComponents;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class UpdateInventoryConsumerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("activemq:queue:inventory.update.queue?concurrentConsumers=5")
                .routeId("route-inventory-update-consumer")
                .unmarshal().json()
                .bean(UpdateInventoryComponents.class,"validatePayload")
                .split(simple("${exchangeProperty." + ApplicationConstants.PROPERTY_VALID_UPDATES + "}"))
                .streaming()
                .doTry()
                .bean(UpdateInventoryComponents.class,"prepareCurrentItem")
                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_BY_UID,
                        ApplicationConstants.MONGO_CONNECTION,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_READ_COLLECTION))
                .bean(UpdateInventoryComponents.class,"calculateStock")
                .bean(UpdateInventoryComponents.class,"prepareUpdateQuery")
                .to(String.format(ApplicationConstants.MONGO_ITEM_UPDATE,
                        ApplicationConstants.MONGO_CONNECTION,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_COLLECTION))
                .bean(UpdateInventoryComponents.class,"handleSuccess")
                .doCatch(Exception.class)
                .bean(UpdateInventoryComponents.class,"handleFailure")
                .end()
                .end()
                .marshal().json()
                .bean(UpdateInventoryComponents.class, "logSummary")
                .log("✅ Inventory update process completed");
    }
}