package com.ust.my_cart_req5.constants;

public final class ApplicationConstants {
    // MongoDB collections
    public static final String CONTROL_REF_COLLECTION = "{{camel.mongodb.controlref.collection}}";
    public static final String ITEMS_COLLECTION = "{{camel.mongodb.item.collection}}";
    public static final String CATEGORIES_COLLECTION = "{{camel.mongodb.category.collection}}";

    // MongoDB properties
    public static final String MONGODB_CONNECTION_BEAN = "{{camel.mongodb.connectionBean}}";
    public static final String MONGODB_DATABASE = "{{camel.mongodb.database}}";
    public static final String CONTROL_REF_KEY = "{{mycart.export.controlref.key}}";

    // File export paths
    public static final String TREND_EXPORT_PATH = "{{export.path.trend-analyzer}}";
    public static final String REVIEW_EXPORT_PATH = "{{export.path.review-aggregator}}";
    public static final String STOREFRONT_EXPORT_PATH = "{{export.path.storefront}}";

    // Header and property keys
    public static final String CAMEL_OID_HEADER = "CamelOid";
    public static final String MONGO_COLLECTION_HEADER = "MongoCollection";
    public static final String ITEMS_FOUND_HEADER = "itemsFound";
    public static final String LAST_PROCESSED_TS_HEADER = "lastProcessedTs";
    public static final String LATEST_UPDATE_DATE_PROPERTY = "latestUpdateDate";
    public static final String FINAL_LATEST_UPDATE_DATE_PROPERTY = "finalLatestUpdateDate";

    // Default values
    public static final String DEFAULT_TIMESTAMP = "1970-01-01T00:00:00";

    // Route IDs
    public static final String ITEM_EXPORT_SCHEDULER_ROUTE = "item-export-scheduler";
    public static final String TREND_ANALYZER_ROUTE = "trend-analyzer-route";
    public static final String REVIEW_AGGREGATOR_ROUTE = "review-aggregator-route";
    public static final String STOREFRONT_APP_ROUTE = "storefront-app-route";
    public static final String CATEGORY_LOOKUP_ROUTE = "category-lookup-route";

    // File suffixes
    public static final String TREND_FILE_SUFFIX = "_trend.xml";
    public static final String REVIEW_FILE_SUFFIX = "_review.xml";
    public static final String STOREFRONT_FILE_SUFFIX = "_storefront.json";
    public static final String STOREFRONT_DONE_SUFFIX = "_storefront.done";

    private ApplicationConstants() {
        // Prevent instantiation
    }
}
