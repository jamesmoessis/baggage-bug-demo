package com.example.demo;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
public class Controller {

    private final Tracer tracer;
    private final ObservationRegistry observationRegistry;

    public Controller(Tracer tracer, ObservationRegistry observationRegistry) {
        this.tracer = tracer;
        this.observationRegistry = observationRegistry;
    }

    @GetMapping("observation")
    public Mono<String> observation() {
        String baggageBefore = tracer.getBaggage("key").get();
        Observation parent = observationRegistry.getCurrentObservation();
        Observation o = Observation.createNotStarted("child", observationRegistry).parentObservation(parent);
        try(Observation.Scope ignored = o.openScope()) {
            String baggageAfter = tracer.getBaggage("key").get();
            return Mono.just("baggagebefore="+baggageBefore+", baggageAfter="+baggageAfter);
        }
    }
}
