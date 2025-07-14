package com.ust.my_cart_req5.processor;

import com.ust.my_cart_req5.model.storefront.StoreFrontItem;
import com.ust.my_cart_req5.model.storefront.Price;
import com.ust.my_cart_req5.model.storefront.Stock;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Map;

public class StoreFrontJsonBuilder implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> item = exchange.getIn().getBody(Map.class);
        StoreFrontItem storeItem = new StoreFrontItem();

        storeItem.setId((String) item.get("_id"));
        storeItem.setItemName((String) item.get("itemName"));
        storeItem.setCategoryName((String) item.get("categoryName"));
        storeItem.setSpecialProduct(Boolean.TRUE.equals(item.get("specialProduct")));

        // Price
        Map<String, Object> priceMap = (Map<String, Object>) item.get("itemPrice");
        if (priceMap != null) {
            Price price = new Price();
            price.setBasePrice(((Number) priceMap.get("basePrice")).doubleValue());
            price.setSellingPrice(((Number) priceMap.get("sellingPrice")).doubleValue());
            storeItem.setItemPrice(price);
        }

        // Stock
        Map<String, Object> stockMap = (Map<String, Object>) item.get("stockDetails");
        if (stockMap != null) {
            Stock stock = new Stock();
            stock.setAvailableStock(((Number) stockMap.get("availableStock")).intValue());
            stock.setUnitOfMeasure((String) stockMap.get("unitOfMeasure"));
            storeItem.setStockDetails(stock);
        }

        exchange.getIn().setBody(storeItem);
        exchange.getIn().setHeader("CamelOid", storeItem.getId());
    }
}

