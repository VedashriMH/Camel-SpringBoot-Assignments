package com.mycart.mycart_app.route;

import com.mycart.mycart_app.constants.ApplicationConstants;
import com.mycart.mycart_app.exception.CategoryNotFoundException;
import com.mycart.mycart_app.processor.GetItemsByCategory;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;

@Component
public class GetItemsByCategoryRoute extends RouteBuilder {


    @Override
    public void configure() {

        from(ApplicationConstants.DIRECT_PREFIX + ApplicationConstants.ENDPOINT_GET_ITEMS_BY_CATEGORY)
                .routeId(ApplicationConstants.ROUTE_GET_ITEMS_BY_CATEGORY)
                .errorHandler(noErrorHandler())
                .doTry()

                // Log incoming request
                .log(LoggingLevel.INFO, "Received request to fetch items by category. categoryId=${header.categoryId}, includeSpecial=${header.includeSpecial}")

                // Capture incoming parameters
                .setProperty("categoryId", header("categoryId"))
                .setProperty("includeSpecial", header("includeSpecial"))

                // Validate 'includeSpecial' is a boolean or string 'true'/'false'
                .choice()
                .when(simple("${header.includeSpecial} != null && ${header.includeSpecial} != 'true' && ${header.includeSpecial} != 'false'"))
                .throwException(new InvalidParameterException("includeSpecial must be either 'true' or 'false'"))
                .end()

                // Log before querying items
                .log(LoggingLevel.DEBUG, "Building Mongo query for category items...")

                // Prepare Mongo query
                .setHeader("CamelMongoDbCriteria", method("categoryQueryBuilder", "build"))

                .log(LoggingLevel.DEBUG, "Executing Mongo query to fetch items")

                .to(String.format(ApplicationConstants.MONGO_ITEM_FIND_WITH_QUERY,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_ITEM_READ_COLLECTION))

                .log(LoggingLevel.DEBUG, "Items fetched from Mongo: ${body}")

                .setProperty("filteredItems", body())

                // Get category info for enrichment
                .log(LoggingLevel.DEBUG, "Fetching category document for ID: ${exchangeProperty.categoryId}")
                .setBody(exchangeProperty("categoryId"))
                .to(String.format(ApplicationConstants.MONGO_CATEGORY_FIND_BY_ID,
                        ApplicationConstants.MONGO_DATABASE,
                        ApplicationConstants.MONGO_CATEGORY_READ_COLLECTION))

                .choice()
                    .when(body().isNull())
                        .throwException(new CategoryNotFoundException("Invalid categoryId"))
                .end()

                .log(LoggingLevel.DEBUG, "Category document fetched: ${body}")

                .log(LoggingLevel.DEBUG, "Building final response using POJO")
                .process("getItemsByCategory")// response processor using POJO
                .endDoTry()
                .doCatch(Exception.class)
                    .log(LoggingLevel.ERROR, "Exception occurred while processing request: ${exception.message}")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                    .process("errorResponse")
                .doFinally()
                    .log(LoggingLevel.INFO, "Finalizing response for GetItemsByCategoryRoute")
                    .process("finalResponse")
                .end()
                .marshal().json();
    }
}
