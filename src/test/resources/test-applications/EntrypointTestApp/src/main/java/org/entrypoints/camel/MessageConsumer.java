package org.entrypoints.camel;

import org.apache.camel.Consume;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @Consume(uri = "direct:processMessage")
    @Handler
    public void processMessage(String message) {
        // Process message
    }
}
