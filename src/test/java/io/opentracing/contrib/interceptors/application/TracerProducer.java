package io.opentracing.contrib.interceptors.application;

import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TracerProducer {
    private final Tracer tracer = new MockTracer();

    @Produces
    public Tracer getTracer() {
        return tracer;
    }
}
