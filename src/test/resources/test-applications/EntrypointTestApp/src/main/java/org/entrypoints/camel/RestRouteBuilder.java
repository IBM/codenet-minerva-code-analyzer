package org.entrypoints.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode("json");

        rest("/api")
                .get("/status").to("direct:getStatus")
                .post("/order").to("direct:submitOrder");
    }
}
