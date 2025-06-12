package com.ust.my_cart_req3.route;

import com.ust.my_cart_req3.constants.ApplicationConstants;
import com.ust.my_cart_req3.processor.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.springframework.stereotype.Component;

@Component
public class ExportSchedulerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Define JAXB formats
        JaxbDataFormat trendJaxb = new JaxbDataFormat("com.ust.my_cart_req3.model.trend");
        JaxbDataFormat reviewJaxb = new JaxbDataFormat("com.ust.my_cart_req3.model.review");

        // Handle exceptions
        onException(Exception.class)
                .log("Error occurred: ${exception.message}")
                .handled(true);

        // Main route
        from("quartz://itemExportTimer?cron=0+0/1+*+*+*+?")
                .routeId(ApplicationConstants.ITEM_EXPORT_SCHEDULER_ROUTE)
                .log("Scheduler triggered at ${date:now:yyyy-MM-dd'T'HH:mm:ss.SSSZ}")
                .setHeader(ApplicationConstants.MONGO_COLLECTION_HEADER, simple(ApplicationConstants.CONTROL_REF_COLLECTION))
                .process(new ControlRefQueryProcessor())
                .to("mongodb:" + ApplicationConstants.MONGODB_CONNECTION_BEAN + "?database=" + ApplicationConstants.MONGODB_DATABASE + "&collection=" + ApplicationConstants.CONTROL_REF_COLLECTION + "&operation=findOneByQuery")
                .process(new ControlRefTimestampProcessor())
                .log("Fetching items updated since ${header." + ApplicationConstants.LAST_PROCESSED_TS_HEADER + "}...")
                .process(new ItemsQueryProcessor())
                .setHeader(ApplicationConstants.MONGO_COLLECTION_HEADER, simple(ApplicationConstants.ITEMS_COLLECTION))
                .to("mongodb:" + ApplicationConstants.MONGODB_CONNECTION_BEAN + "?database=" + ApplicationConstants.MONGODB_DATABASE + "&collection=" + ApplicationConstants.ITEMS_COLLECTION + "&operation=findAll")
                .process(new ItemsValidationProcessor())
                .choice()
                .when(header(ApplicationConstants.ITEMS_FOUND_HEADER).isEqualTo(true))
                .process(new LatestTimestampCaptureProcessor())
                .split(body())
                .aggregationStrategy(new LatestTimestampAggregationStrategy())
                .parallelProcessing()
                .log("Processing item with ID: ${body[_id]}")
                .process(new ItemHeaderProcessor())
                .enrich("direct:categoryLookup", (original, resource) -> {
                    original.setProperty("category", resource.getIn().getBody());
                    return original;
                })
                .process(new CategoryEnrichmentProcessor())
                .multicast().parallelProcessing()
                .to("direct:trendFormat", "direct:reviewFormat", "direct:storefrontFormat")
                .end()
                .end()
                .process(new ControlRefUpdateProcessor())
                .setHeader(ApplicationConstants.MONGO_COLLECTION_HEADER, simple(ApplicationConstants.CONTROL_REF_COLLECTION))
                .to("mongodb:" + ApplicationConstants.MONGODB_CONNECTION_BEAN + "?database=" + ApplicationConstants.MONGODB_DATABASE + "&collection=" + ApplicationConstants.CONTROL_REF_COLLECTION + "&operation=save")
                .endChoice()
                .otherwise()
                .log("No items found; skipping update.")
                .end()
                .log("Item export complete at ${date:now:yyyy-MM-dd'T'HH:mm:ss.SSSZ}");

        // Category Lookup route
        from("direct:categoryLookup")
                .routeId(ApplicationConstants.CATEGORY_LOOKUP_ROUTE)
                .process(new CategoryLookupProcessor())
                .to("mongodb:" + ApplicationConstants.MONGODB_CONNECTION_BEAN + "?database=" + ApplicationConstants.MONGODB_DATABASE + "&collection=" + ApplicationConstants.CATEGORIES_COLLECTION + "&operation=findOneByQuery")
                .log("Fetched category document: ${body}");

        // Trend Analyzer route
        from("direct:trendFormat")
                .routeId(ApplicationConstants.TREND_ANALYZER_ROUTE)
                .process(new TrendAnalyzerXmlBuilder())
                .log("Transformed item to trend XML")
                .marshal(trendJaxb)
                .log("Marshalled trend to XML")
                .toD("file:" + ApplicationConstants.TREND_EXPORT_PATH + "?fileName=${header." + ApplicationConstants.CAMEL_OID_HEADER + "}" + ApplicationConstants.TREND_FILE_SUFFIX + "&fileExist=Override")
                .log("Exported trend XML for ${header." + ApplicationConstants.CAMEL_OID_HEADER + "} to " + ApplicationConstants.TREND_EXPORT_PATH);

        // Review Aggregator route
        from("direct:reviewFormat")
                .routeId(ApplicationConstants.REVIEW_AGGREGATOR_ROUTE)
                .process(new ReviewAggregatorXmlBuilder())
                .log("Transformed item to review XML")
                .marshal(reviewJaxb)
                .toD("file:" + ApplicationConstants.REVIEW_EXPORT_PATH + "?fileName=${header." + ApplicationConstants.CAMEL_OID_HEADER + "}" + ApplicationConstants.REVIEW_FILE_SUFFIX + "&fileExist=Override")
                .log("Exported review XML for ${header." + ApplicationConstants.CAMEL_OID_HEADER + "} to " + ApplicationConstants.REVIEW_EXPORT_PATH);

        // Storefront route
        from("direct:storefrontFormat")
                .routeId(ApplicationConstants.STOREFRONT_APP_ROUTE)
                .process(new StoreFrontJsonBuilder())
                .marshal().json()
                .log("Writing JSON to file: " + ApplicationConstants.STOREFRONT_EXPORT_PATH + "/${header." + ApplicationConstants.CAMEL_OID_HEADER + "}" + ApplicationConstants.STOREFRONT_FILE_SUFFIX)
                .toD("file:" + ApplicationConstants.STOREFRONT_EXPORT_PATH + "?fileName=${header." + ApplicationConstants.CAMEL_OID_HEADER + "}" + ApplicationConstants.STOREFRONT_FILE_SUFFIX + "&fileExist=Override&doneFileName=${header." + ApplicationConstants.CAMEL_OID_HEADER + "}" + ApplicationConstants.STOREFRONT_DONE_SUFFIX)
                .log("Exported storefront JSON for ${header." + ApplicationConstants.CAMEL_OID_HEADER + "} to " + ApplicationConstants.STOREFRONT_EXPORT_PATH);
    }
}