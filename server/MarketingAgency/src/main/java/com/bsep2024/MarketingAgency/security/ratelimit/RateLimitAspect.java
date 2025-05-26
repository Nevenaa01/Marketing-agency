package com.bsep2024.MarketingAgency.security.ratelimit;

import com.bsep2024.MarketingAgency.security.ratelimit.exceptions.RateLimitException;
import com.bsep2024.MarketingAgency.utils.RequestWrapper;
import org.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {

    public static final String ERROR_MESSAGE = "Too many requests at endpoint %s from IP %s! Please try again after %d milliseconds!";
    private final ConcurrentHashMap<String, List<Long>> requestCounts = new ConcurrentHashMap<>();

    @Value("${APP_RATE_DURATIONINMS:#{60000}}")
    private long rateDuration;

    @Before("@annotation(com.bsep2024.MarketingAgency.security.ratelimit.WithRateLimitProtection)")
    public void rateLimit() throws IOException {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest request = requestAttributes.getRequest();
        final RequestWrapper requestWrapper = new RequestWrapper(request);
        final String key = request.getRemoteAddr();
        final long currentTime = System.currentTimeMillis();

        String requestBody = requestWrapper.getBody();

        JSONObject jsonObject = new JSONObject(requestBody);
        String role = jsonObject.getString("role");

        int roleRateLimit = getRateLimitForRole(role);

        requestCounts.putIfAbsent(key, new ArrayList<>());
        requestCounts.get(key).add(currentTime);
        cleanUpRequestCounts(currentTime);
        if (requestCounts.get(key).size() > roleRateLimit) {
            throw new RateLimitException(String.format(ERROR_MESSAGE, request.getRequestURI(), key, rateDuration));
        }
    }

    private int getRateLimitForRole(String role) {
        switch (role) {
            case "BASIC":
                return 10;
            case "STANDARD":
                return 100;
            case "GOLDEN":
                return 10000;
            default:
                return 200;
        }
    }

    private void cleanUpRequestCounts(final long currentTime) {
        requestCounts.values().forEach(l -> {
            l.removeIf(t -> timeIsTooOld(currentTime, t));
        });
    }

    private boolean timeIsTooOld(final long currentTime, final long timeToCheck) {
        return currentTime - timeToCheck > rateDuration;
    }
}
