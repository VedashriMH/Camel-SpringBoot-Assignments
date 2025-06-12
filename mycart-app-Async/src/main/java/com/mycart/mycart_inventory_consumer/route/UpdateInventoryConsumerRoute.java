// UpdateInventoryConsumerRoute.java
package com.mycart.mycart_inventory_consumer.route;

import com.mycart.mycart_inventory_consumer.constants.ApplicationConstants;
import com.mycart.mycart_inventory_consumer.processor.UpdateInventoryComponents;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class UpdateInventoryConsumerRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("activemq:queue:inventory.update.queue?concurrentConsumers=5")
                .routeId("route-inventory-update-consumer")
                .log(LoggingLevel.INFO, "Received inventory update message from queue.")
                .unmarshal().json()
                .log(LoggingLevel.DEBUG, "Message payload after JSON unmarshal: ${body}")
                .doTry()
                .log(LoggingLevel.INFO, "Validating incoming inventory item...")
                .bean(UpdateInventoryComponents.class, "validateSingleItem")
                .log(LoggingLevel.INFO, "Validation successful.")

                .log(LoggingLevel.INFO, "Preparing item ID and properties for processing.")
                .bean(UpdateInventoryComponents.class, "prepareCurrentItem")
                .log(LoggingLevel.INFO, "Fetching current item state from database for itemId: ${header.itemId}")
                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_BY_UID,
                        ApplicationConstants.MONGO_CONNECTION,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_READ_COLLECTION))
                .log(LoggingLevel.DEBUG, "Fetched current item from DB: ${body}")

                .log(LoggingLevel.INFO, "Calculating updated stock values for itemId: ${header.itemId}")
                .bean(UpdateInventoryComponents.class, "calculateStock")
                .log(LoggingLevel.INFO, "Preparing update query for MongoDB for itemId: ${header.itemId}")
                .bean(UpdateInventoryComponents.class, "prepareUpdateQuery")
                .log(LoggingLevel.DEBUG, "Update query prepared: ${body}")

                .log(LoggingLevel.INFO, "Updating item in MongoDB for itemId: ${header.itemId}")
                .to(String.format(ApplicationConstants.MONGO_ITEM_UPDATE,
                        ApplicationConstants.MONGO_CONNECTION,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_COLLECTION))
                .log(LoggingLevel.INFO, "Inventory updated successfully for itemId: ${header.itemId}")
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Error occurred while processing inventory update for itemId: ${header.itemId} - ${exception.message}")
                .bean(UpdateInventoryComponents.class, "handleFailure")
                .end();
    }
}
