package com.ust.my_cart_req4.route;


import com.ust.my_cart_req4.ErrorResponseProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyCartStoreRoute extends RouteBuilder {

    @Value("${http.retry.max}")
    private int httpRetryMax;

    @Value("${http.retry.delay}")
    private long httpRetryDelay;

    @Value("${http.retry.backoff}")
    private double httpRetryBackoff;

    @Override
    public void configure() {


        onException(org.apache.hc.client5.http.HttpHostConnectException.class)
                .maximumRedeliveries(httpRetryMax)
                .redeliveryDelay(httpRetryDelay)
                .backOffMultiplier(httpRetryBackoff)
                .useExponentialBackOff()
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .log(LoggingLevel.ERROR, "HTTP connection refused: ${exception.message}")
                .handled(true)
//                .process(new ErrorResponseProcessor())
                .marshal().json(JsonLibrary.Jackson);


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



