package com.mycart.mycart_app.route;

import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.processor.UpdateInventoryComponents;
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
                .unmarshal().json()
                .process("updateInventoryComponents")
                .split(simple("${exchangeProperty." + ApplicationConstants.PROPERTY_VALID_UPDATES + "}"))
                .streaming()
                .doTry()
                .bean(UpdateInventoryComponents.class,"prepareCurrentItem")
                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_BY_UID,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_READ_COLLECTION))
                .bean(UpdateInventoryComponents.class,"calculateStock")
                .bean(UpdateInventoryComponents.class,"prepareUpdateQuery")
                .to(String.format(ApplicationConstants.MONGO_ITEM_UPDATE,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_COLLECTION))
                .bean(UpdateInventoryComponents.class,"handleSuccess")
                .doCatch(Exception.class)
                .bean(UpdateInventoryComponents.class,"handleFailure")
                .end()
                .endDoTry()
                .doCatch(Exception.class)
                .bean(UpdateInventoryComponents.class,"handleFailure")
                .doFinally()
                .bean(UpdateInventoryComponents.class,"buildFinalResponse")
                .end()
                .marshal().json();
    }
}


