package org.entrypoints.spring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CustomCommandLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println("CommandLineRunner executed");
    }
}
