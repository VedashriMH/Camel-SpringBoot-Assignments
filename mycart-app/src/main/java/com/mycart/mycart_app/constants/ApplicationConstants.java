package com.mycart.mycart_app.constants;

public class ApplicationConstants {

    // Common
    public static final String DIRECT_PREFIX = "direct:";

    public static final String MONGO_CONNECTION = "mongoBean";

    // Mongo Database
    public static final String MONGO_DATABASE = "mycartdb";  // <-- Your MongoDB database name

    // Mongo Collections
    public static final String MONGO_ITEM_COLLECTION = "item";
    public static final String MONGO_ITEM_READ_COLLECTION = "item";  // sometimes read/write can differ
    public static final String MONGO_CATEGORY_READ_COLLECTION = "category";

    // Mongo Queries
    public static final String MONGO_ITEM_FIND_BY_ID =
            "mongodb:{{mongo.connection}}"
                    + "?database=%s"
                    + "&collection=%s"
                    + "&operation=findById";
    public static final String MONGO_CATEGORY_FIND_BY_ID = "mongodb:{{mongo.connection}}?database=%s&collection=%s&operation=findById&dynamicity=true";
    public static final String MONGO_ITEM_FIND_ALL = "mongodb:{{mongo.connection}}?database=%s&collection=%s&operation=findAll&dynamicity=true";
    public static final String MONGO_ITEM_INSERT = "mongodb:{{mongo.connection}}?database=%s&collection=%s&operation=insert&dynamicity=true";
    public static final String MONGO_ITEM_UPDATE = "mongodb:{{mongo.connection}}?database=%s&collection=%s&operation=save&dynamicity=true";
    public static final String MONGO_ITEM_FIND_WITH_QUERY = "mongodb:{{mongo.connection}}?database=%s&collection=%s&operation=findAll&dynamicity=true";


    // Direct Endpoints
    public static final String ENDPOINT_GET_ITEM_BY_ID = "getItemById";
    public static final String ENDPOINT_GET_ITEMS_BY_CATEGORY = "getItemsByCategory";
    public static final String ENDPOINT_INSERT_ITEM = "insertItem";
    public static final String ENDPOINT_UPDATE_INVENTORY_PROCESS = "update-inventory-process";

    // Routes
    public static final String ROUTE_GET_ITEM_BY_ID = "get-item-by-id-route";
    public static final String ROUTE_GET_ITEMS_BY_CATEGORY = "get-items-by-category-route";
    public static final String ROUTE_INSERT_ITEM = "insert-item-route";
    public static final String ROUTE_UPDATE_INVENTORY_PROCESS = "route-update-inventory-process";


    public static final String PROPERTY_VALID_UPDATES = "validUpdates";
    public static final String PROPERTY_CURRENT_ITEM = "currentItem";


    private ApplicationConstants() {
        // Prevent instantiation
    }
}



