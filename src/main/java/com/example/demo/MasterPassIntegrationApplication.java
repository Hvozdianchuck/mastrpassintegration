package com.example.demo;


import com.mastercard.sdk.core.util.QueryParams;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MasterPassIntegrationApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MasterPassIntegrationApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MasterPassIntegrationApplication.class);
    }


}
