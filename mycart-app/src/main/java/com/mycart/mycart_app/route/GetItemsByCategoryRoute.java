package com.mycart.mycart_app.route;

import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.processor.GetItemsByCategoryComponents;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GetItemsByCategoryRoute extends RouteBuilder {

    @Override
    public void configure() {

        from(ApplicationConstants.DIRECT_PREFIX + ApplicationConstants.ENDPOINT_GET_ITEMS_BY_CATEGORY)
                .routeId(ApplicationConstants.ROUTE_GET_ITEMS_BY_CATEGORY)
                .errorHandler(noErrorHandler())
                .doTry()
                .setProperty("categoryId", header("categoryId"))
                .setProperty("includeSpecial", header("includeSpecial"))

                // Fetch all items from the item collection (no query at Mongo level)
                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_ALL,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_READ_COLLECTION))

                .setHeader("allItems", body())

                // Fetch category document by ID
                .setBody(exchangeProperty("categoryId"))
                .to(String.format(ApplicationConstants.MONGO_CATEGORY_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_CATEGORY_READ_COLLECTION))

                .setHeader("categoryDoc", body())
                .setBody(header("allItems"))

                // Filtering will happen here in the processor
                .bean(GetItemsByCategoryComponents.class, "itemProcessor")
                .doCatch(Exception.class)
                .bean(GetItemsByCategoryComponents.class,"errorResponseProcessor")
                .doFinally()
                .bean(GetItemsByCategoryComponents.class,"finalResponseProcessor")
                .end()
                .marshal().json();


    }
}
