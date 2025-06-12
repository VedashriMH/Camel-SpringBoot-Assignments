package com.ust.my_cart_req4.route;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class MyCartStoreRoute extends RouteBuilder {

    @Override
    public void configure() {

        onException(HttpOperationFailedException.class)
                .handled(true)
                .log("Failed to get item: ${exception.message}")
                .setBody(constant("Item service error occurred"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .end();

        restConfiguration()
                .component("netty-http")
                .host("0.0.0.0")
                .port(8082);

        rest("/store")
                .get("/item/{id}")
                .to("direct:getItemForStore");

        from("direct:getItemForStore")
                .routeId("store-get-item-route")
                .log("Received request for item ID: ${header.id}")
                .setBody(simple("${header.id}"))
                .removeHeader("id")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD("http://localhost:8081/mycart/item/${body}?bridgeEndpoint=true&throwExceptionOnFailure=false&preserveHostHeader=false")
                .log("Fetched item details from item service: ${body}");
    }
}



