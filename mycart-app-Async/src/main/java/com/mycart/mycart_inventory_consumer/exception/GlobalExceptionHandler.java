// com.mycart.mycart_app.exception.GlobalExceptionHandler.java
package com.mycart.mycart_inventory_consumer.exception;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionHandler extends RouteBuilder {

    @Override
    public void configure() {

        // Custom exceptions (Client errors - 400)
        onException(CategoryNotFoundException.class, ItemInsertException.class,
                ItemNotFoundException.class, StockUpdateException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"error\": \"${exception.message}\"}"));

        // Generic fallback (Server errors - 500)
        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"error\": \"${exception.message}\"}"));
    }
}
