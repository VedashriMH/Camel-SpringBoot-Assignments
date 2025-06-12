package com.mycart.mycart_app.route;

import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.exception.ItemInsertException;
import com.mycart.mycart_app.processor.InsertItemComponents;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
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
                .log(LoggingLevel.INFO, "Received request to insert new item")
                .unmarshal().json()
                .log(LoggingLevel.DEBUG, "Unmarshalled item payload: ${body}")
                .process("insertItemComponents") // This stores item in exchange property
                .log(LoggingLevel.DEBUG, "Validated and parsed item data: ${exchangeProperty.item}")


                // Validate category existence
                .to(String.format(ApplicationConstants.MONGO_CATEGORY_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE, ApplicationConstants.MONGO_CATEGORY_READ_COLLECTION))
                .log(LoggingLevel.DEBUG, "Category lookup response: ${body}")

                .choice()
                    .when(body().isNull())
                        .throwException(new ItemInsertException("Invalid category ID"))
                .end()

                // Validate duplicate item

                .setBody(simple("${exchangeProperty.item[_id]}"))
                .log(LoggingLevel.DEBUG, "Checking for existing item with ID: ${body}")
                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE, ApplicationConstants.MONGO_ITEM_READ_COLLECTION))
                .log(LoggingLevel.DEBUG, "Existing item check result: ${body}")

                .choice()
                    .when(body().isNotNull())
                        .log(LoggingLevel.WARN, "Item already exists with ID: ${exchangeProperty.item[_id]}")
                        .throwException(ItemInsertException.class, "Item already exists with given ID")
                .end()


                // Insert new item
                .setBody(exchangeProperty("item"))
                .log(LoggingLevel.INFO, "Inserting new item into MongoDB")
                .to(String.format(ApplicationConstants.MONGO_ITEM_INSERT,
                        ApplicationConstants.MONGO_DATABASE, ApplicationConstants.MONGO_ITEM_COLLECTION))

                .log(LoggingLevel.INFO, "Item inserted successfully")

                .bean("insertItemComponents", "finalResponse")
                .endDoTry()
                .doCatch(com.mycart.mycart_app.exception.ItemInsertException.class)
                .log(LoggingLevel.ERROR, "ItemInsertException: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .process("errorResponse")

                .doCatch(com.mongodb.MongoWriteException.class)
                .log(LoggingLevel.ERROR, "MongoWriteException: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(409))
                .process("errorResponse")

                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Unhandled Exception: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process("errorResponse")
                .end()
                .marshal().json();
    }
}

