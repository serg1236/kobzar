package com.sdakhniy.kobzar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class KobzarApplication {

    public static void main(String[] args) {
        SpringApplication.run(KobzarApplication.class, args);
    }

}
