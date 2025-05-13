package com.mycart.mycart_app.route;

import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.processor.InsertItemComponents;
import org.apache.camel.builder.RouteBuilder;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InsertItemRoute extends RouteBuilder {

    @Override
    public void configure() {

        from(ApplicationConstants.DIRECT_PREFIX + ApplicationConstants.ENDPOINT_INSERT_ITEM)
                .routeId(ApplicationConstants.ROUTE_INSERT_ITEM)
                .errorHandler(noErrorHandler())
                .doTry()
                .unmarshal().json()
                .bean(InsertItemComponents.class,"validatePayload")

                // Validate if category exists
                .to(String.format(ApplicationConstants.MONGO_CATEGORY_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE, ApplicationConstants.MONGO_CATEGORY_READ_COLLECTION))

                .bean(InsertItemComponents.class, "validateCategory")

                // Validate if item already exists
                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE, ApplicationConstants.MONGO_ITEM_READ_COLLECTION))

                .bean(InsertItemComponents.class, "validateDuplicate")

                .to(String.format(ApplicationConstants.MONGO_ITEM_INSERT,
                        ApplicationConstants.MONGO_DATABASE, ApplicationConstants.MONGO_ITEM_COLLECTION))

                .bean(InsertItemComponents.class,"finalResponse")

                .doCatch(Exception.class)
                .bean(InsertItemComponents.class,"errorResponse")
                .end()
                .marshal().json();

    }
}
