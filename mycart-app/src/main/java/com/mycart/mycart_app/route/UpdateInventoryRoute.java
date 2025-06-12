package com.mycart.mycart_app.route;

import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.processor.UpdateInventoryComponents;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UpdateInventoryRoute extends RouteBuilder {

    @Override
    public void configure() {

        from(ApplicationConstants.DIRECT_PREFIX + ApplicationConstants.ENDPOINT_UPDATE_INVENTORY_PROCESS)
                .routeId(ApplicationConstants.ROUTE_UPDATE_INVENTORY_PROCESS)
                .errorHandler(noErrorHandler())
                .doTry()
                .log(LoggingLevel.INFO, "Received request to update inventory.")
                .unmarshal().json()
                .log(LoggingLevel.DEBUG, "Request unmarshalled: ${body}")
                .process("updateInventoryComponents")
                .log(LoggingLevel.INFO, "Inventory items validated and split for processing.")
                .split(simple("${exchangeProperty." + ApplicationConstants.PROPERTY_VALID_UPDATES + "}"))
                .streaming()
                .doTry()
                .log(LoggingLevel.INFO, "Processing item: ${body[_id]}")
                .bean(UpdateInventoryComponents.class,"prepareCurrentItem")

                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_READ_COLLECTION))
                .log(LoggingLevel.DEBUG, "Fetched current stock data from DB for item: ${body[_id]}")
                .bean(UpdateInventoryComponents.class,"calculateStock")
                .log(LoggingLevel.DEBUG, "Stock calculated for item: ${body[_id]}")
                .bean(UpdateInventoryComponents.class,"prepareUpdateQuery")
                .log(LoggingLevel.DEBUG, "Update query prepared for item: ${body[_id]}")
                .setProperty("currentItemId", simple("${body[_id]}"))
                .to(String.format(ApplicationConstants.MONGO_ITEM_UPDATE,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_COLLECTION))
                .log(LoggingLevel.INFO, "Inventory updated in DB for item:${exchangeProperty.currentItemId}")
                .bean(UpdateInventoryComponents.class,"handleSuccess")
                .log(LoggingLevel.INFO, "Success handled for item: ${exchangeProperty.currentItemId}")
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Error while processing item: ${exchangeProperty.currentItemId}, Error: ${exception.message}")
                .bean(UpdateInventoryComponents.class,"handleFailure")
                .end()
                .endDoTry()
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Unexpected error in inventory update process: ${exception.message}")
                .bean(UpdateInventoryComponents.class,"handleFailure")
                .doFinally()
                .log(LoggingLevel.INFO, "Finalizing inventory update response.")
                .bean(UpdateInventoryComponents.class,"buildFinalResponse")
                .end()
                .marshal().json();
    }
}


