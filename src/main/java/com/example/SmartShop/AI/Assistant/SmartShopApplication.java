package com.example.SmartShop.AI.Assistant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.example.SmartShop.AI.Assistant.Repository.ProductRepository; // make sure this import exists

@SpringBootApplication
@EnableScheduling
public class SmartShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartShopApplication.class, args);
        System.out.println("✅ SmartShop Backend Running...");
    }

    // Add this bean to test DB connection and product count
    @Bean
    CommandLineRunner testDb(ProductRepository productRepository) {
        return args -> {
            System.out.println("✅ DB Connected Successfully!");
            long count = productRepository.count();
            System.out.println("📦 Product count in DB: " + count);
            // Optional: print top 5 products for quick verification
            productRepository.findTop10ByTitleContainingIgnoreCase("")
                    .stream()
                    .limit(5)
                    .forEach(p -> System.out.println(" - " + p.getTitle()));
        };
    }
}
