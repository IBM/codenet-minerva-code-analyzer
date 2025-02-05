package org.entrypoints.spring;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class SpringBootController {

    @PostMapping("/api/data")
    public String processData(@RequestBody String data) {
        return "Processed: " + data;
    }
}
