package org.entrypoints.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class CamelProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        // Process message
        String body = exchange.getIn().getBody(String.class);
        exchange.getMessage().setBody("Processed: " + body);
    }
}
