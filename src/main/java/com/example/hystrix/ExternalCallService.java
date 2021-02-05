package com.example.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * 설명 :
 *
 * @author Minkuk Jo / mingood92@gmail.com
 * @since 2021. 01. 14
 */
@Service
public class ExternalCallService {

    private final RestTemplate restTemplate;

    public ExternalCallService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand
    public String callServer2() {
        URI uri = URI.create("http://localhost:8081/sample/server2");
        return restTemplate.getForObject(uri, String.class);
    }

    @HystrixCommand(
            commandKey = "Server2",
            fallbackMethod = "callServer2Fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000"),    // 오류 감시 시간
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "3"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),   // 서킷 오픈 유지 시간
            }
    )
    public String callServer2WithFallback() {
        URI uri = URI.create("http://localhost:8081/sample/server2-fallback");
        return restTemplate.getForObject(uri, String.class);
    }

    private String callServer2Fallback() {
        return "No available";
    }

}
