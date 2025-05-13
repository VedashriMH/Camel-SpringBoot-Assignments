package com.mycart.mycart_app.route;


//import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;

import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.processor.GetItemByIdComponents;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class GetItemByIdRoute extends RouteBuilder {

    @Override
    public void configure() {

        from(ApplicationConstants.DIRECT_PREFIX + ApplicationConstants.ENDPOINT_GET_ITEM_BY_ID)
                .routeId(ApplicationConstants.ROUTE_GET_ITEM_BY_ID)
                .errorHandler(noErrorHandler())
                .doTry()
                .setBody(simple("${header.id}"))
                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_READ_COLLECTION))
                .bean(GetItemByIdComponents.class, "itemProcessor")
                .doCatch(Exception.class)
                .bean(GetItemByIdComponents.class,"errorResponseProcessor")
                .doFinally()
                .bean(GetItemByIdComponents.class,"finalResponseProcessor")
                .end()
                .marshal().json();
    }
}
