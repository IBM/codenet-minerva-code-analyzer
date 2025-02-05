package org.entrypoints.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CustomRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:input/orders")
                .routeId("orderProcessingRoute")
                .to("direct:processOrder");

        from("direct:processOrder")
                .process(exchange -> {
                    // Process order
                })
                .to("file:output/processed");
    }
}
