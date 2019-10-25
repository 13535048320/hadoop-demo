package com.pt.hadoop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HadoopApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HadoopApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
