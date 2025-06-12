package com.mycart.mycart_app.route;
import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.exception.ItemNotFoundException;
import com.mycart.mycart_app.processor.GetItemByIdComponents;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class GetItemByIdRoute extends RouteBuilder {



    @Override
    public void configure() {

        from(ApplicationConstants.DIRECT_PREFIX + ApplicationConstants.ENDPOINT_GET_ITEM_BY_ID)
                .routeId(ApplicationConstants.ROUTE_GET_ITEM_BY_ID)
                .errorHandler(noErrorHandler())
                .log(LoggingLevel.valueOf("INFO"), "Received request to fetch item with ID: ${header.id}")
                .doTry()
                .setBody(simple("${header.id}"))
                .log(LoggingLevel.valueOf("DEBUG"), "Querying MongoDB for item ID: ${body}")
                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_READ_COLLECTION))

                .log(LoggingLevel.valueOf("DEBUG"), "MongoDB item result: ${body}")

                // Null check using choice
                .choice()
                    .when(body().isNull())
                        .setHeader("id", simple("${body}")) // optional: preserve ID
                        .log(LoggingLevel.valueOf("WARN"), "Item not found for ID: ${header.id}")
                        .throwException(new ItemNotFoundException("Item not found for given ID"))
                .end()

                .setProperty("itemDoc", body())
                .log(LoggingLevel.valueOf("DEBUG"), "Storing itemDoc in exchange property")
                // Set categoryId from item
                .setBody(simple("${exchangeProperty.itemDoc[categoryId]}"))
                .log(LoggingLevel.valueOf("DEBUG"), "Category ID extracted: ${body}")

                // Project only the categoryName field
                .setHeader("CamelMongoDbFieldsProjection", constant("{ \"categoryName\": 1, \"_id\": 0 }"))

                // Mongo call to fetch only categoryName
                .to(String.format(ApplicationConstants.MONGO_CATEGORY_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_CATEGORY_READ_COLLECTION))

                .log(LoggingLevel.valueOf("DEBUG"), "MongoDB category lookup result: ${body}")
                .setProperty("categoryName", simple("${body[categoryName]}"))
                .log(LoggingLevel.valueOf("DEBUG"), "Category name extracted: ${exchangeProperty.categoryName}")

                // Build response
                .process("getItemByIdComponents")
                .endDoTry()
                .doCatch(Exception.class)
                .log(LoggingLevel.valueOf("ERROR"), "Exception occurred: ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .process("errorResponse")
                .doFinally()
                .log(LoggingLevel.valueOf("INFO"), "Preparing final response")
                .process("finalResponse")
                .end()
                .marshal().json();
    }
}
