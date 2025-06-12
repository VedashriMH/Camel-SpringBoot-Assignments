package com.mycart.mycart_app.processor;

import org.apache.camel.Exchange;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component("categoryQueryBuilder")
public class CategoryQueryBuilder {

    public Document build(Exchange exchange) {
        String categoryId = exchange.getProperty("categoryId", String.class);
        String includeSpecial = exchange.getProperty("includeSpecial", "false", String.class);
        boolean filterSpecial = !Boolean.parseBoolean(includeSpecial);

        Document query = new Document("categoryId", categoryId);
        if (filterSpecial) {
            query.append("specialProduct", false);
        }

        return query;
    }
}

