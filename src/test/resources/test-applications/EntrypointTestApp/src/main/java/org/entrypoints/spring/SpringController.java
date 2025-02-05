package org.entrypoints.spring;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class SpringController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring!";
    }
}
