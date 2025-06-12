package com.mycart.mycart_inventory_consumer.constants;

public class ApplicationConstants {


    public static final String MONGO_CONNECTION = "mongoBean";

    // Mongo Database
    public static final String MONGO_DATABASE = "mycartdb";  // <-- Your MongoDB database name

    // Mongo Collections
    public static final String MONGO_ITEM_COLLECTION = "item";
    public static final String MONGO_ITEM_READ_COLLECTION = "item";  // sometimes read/write can differ


    // Mongo Queries
    public static final String MONGO_ITEM_FIND_BY_UID = "mongodb:%s?database=%s&collection=%s&operation=findOneByQuery&dynamicity=true";
    public static final String MONGO_ITEM_UPDATE = "mongodb:%s?database=%s&collection=%s&operation=save&dynamicity=true";

    public static final String PROPERTY_CURRENT_ITEM = "currentItem";


    private ApplicationConstants() {
        // Prevent instantiation
    }
}



