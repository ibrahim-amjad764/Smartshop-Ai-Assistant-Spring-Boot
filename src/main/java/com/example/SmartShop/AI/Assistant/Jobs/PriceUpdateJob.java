package com.example.SmartShop.AI.Assistant.Jobs;

import com.example.SmartShop.AI.Assistant.Entity.Offer;
import com.example.SmartShop.AI.Assistant.Entity.PriceHistory;
import com.example.SmartShop.AI.Assistant.Repository.OfferRepository;
import com.example.SmartShop.AI.Assistant.Repository.PriceHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Scheduled job for refreshing product prices.
 *
 * Runs every 6 hours (configurable via application.properties).
 *
 * Production-ready features:
 * - Execution time logging
 * - Safe error handling (scheduler never dies)
 * - Prevents overlapping runs (single-thread execution with a lock)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PriceUpdateJob {

    // Lock to prevent overlapping runs
    private final Lock lock = new ReentrantLock();

    // Inject repositories
    private final OfferRepository offerRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    //  Inject PriceUpdateService when implemented
    // private final PriceUpdateService priceUpdateService;


    @Scheduled(cron = "${smartshop.scheduler.price-update-cron:0 0 */6 * * *}")
    public void updatePrices() {
        if (lock.tryLock()) {  // Prevent overlap
            Instant start = Instant.now();
            log.info("[PriceUpdateJob] Started price refresh job at {}", start);

            System.out.println(" PriceUpdateJob started");

            try {

                // Fetch all offers
                List<Offer> offers = offerRepository.findAll();
                System.out.println(" Total offers found: " + offers.size());

                for (Offer offer : offers) {

                    double oldPrice = offer.getPrice();

                    //  Replace this with real store API price fetch
                    double newPrice = fetchLatestPrice(offer);

                    System.out.println("Checking product: " + offer.getProduct().getTitle());
                    System.out.println("Old price: " + oldPrice + " | New price: " + newPrice);

                    // Detect price change
                    if (oldPrice != newPrice) {

                        System.out.println(" Price changed. Saving history...");

                        PriceHistory history = new PriceHistory();
                        history.setOffer(offer);
                        history.setPrice(newPrice);
                        history.setCheckedAt(LocalDateTime.now());

                        priceHistoryRepository.save(history);

                        // Update offer price
                        offer.setPrice(newPrice);
                        offerRepository.save(offer);

                        System.out.println(" Price history saved for offer: " + offer.getId());
                    }
                }

                log.info("[PriceUpdateJob] Price refresh logic executed successfully.");

            } catch (Exception ex) {
                // Log the error without interrupting the job
                log.error("[PriceUpdateJob] Error occurred during price refresh", ex);
                System.out.println(" Error during price update job: " + ex.getMessage());
            } finally {
                lock.unlock();  // Always release the lock
            }

            Instant end = Instant.now();
            long executionTime = Duration.between(start, end).toMillis();
            log.info("[PriceUpdateJob] Finished. Execution time = {} ms", executionTime);

            System.out.println(" PriceUpdateJob finished in " + executionTime + " ms");

        } else {
            // Log if the previous execution is still running
            log.warn("[PriceUpdateJob] Another price refresh job is already running, skipping this execution.");
            System.out.println("⚠ PriceUpdateJob skipped because another job is running.");
        }
    }


    /**
     * Simulates fetching latest price from store API.
     * Replace this method with actual API/scraper logic.
     */
    private double fetchLatestPrice(Offer offer) {

        // Example simulation: small random fluctuation
        double randomChange = (Math.random() * 20) - 10;

        double simulatedPrice = offer.getPrice() + randomChange;

        System.out.println("🔎 Simulated fetched price: " + simulatedPrice);

        return Math.max(simulatedPrice, 1); // ensure price never becomes negative
    }
}