package com.bsep2024.MarketingAgency.controllers;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/dos")
public class DOSController {
    private TimeLimiter ourTimeLimiter = TimeLimiter.of(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(500)).build());
    @GetMapping("/author/resilience4j/{time}")
    public Callable<Integer> getWithResilience4jTimeLimiter(@PathVariable("time") Long time) {
        return TimeLimiter.decorateFutureSupplier(ourTimeLimiter, () ->
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(time*100); // Sleep for 1000 milliseconds (1 second)
                    } catch (InterruptedException e) {
                        e.printStackTrace(); // Handle the InterruptedException if necessary
                    }
                    return 5;
                }));
    }
}
