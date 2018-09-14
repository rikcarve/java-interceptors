package io.opentracing.contrib.interceptors.application;

import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerFactory;
import io.opentracing.mock.MockTracer;

public class CustomTracerFactory implements TracerFactory {
    @Override
    public Tracer getTracer() {
        return new MockTracer();
    }
}
