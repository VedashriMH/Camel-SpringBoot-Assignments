package com.mycart.mycart_app.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestApiDefinition extends RouteBuilder {

    @Override
    public void configure() {

        rest("/mycart/item")
                .get("/{id}")
                .to("direct:getItemById");

        rest("/mycart/items")
                .get("/{categoryId}")
                .to("direct:getItemsByCategory");

        rest("/mycart/insertItem")
                .post()
                .consumes("application/json")
                .produces("application/json")
                .to("direct:insertItem");

        rest("/mycart/updateInventory")
                .post()
                .consumes("application/json")
                .produces("application/json")
                .to("direct:update-inventory-process");

        rest("/mycart")
                .post("/updateInventoryAsync")
                .consumes("application/json")
                .produces("application/json")
                .to("direct:update-inventory-async");


    }
}

