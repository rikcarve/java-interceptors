package io.opentracing.contrib.interceptors.application;

import io.opentracing.Span;
import org.eclipse.microprofile.opentracing.Traced;

import javax.ejb.Stateless;

@Stateless
@Traced
public class InterceptedEJB {

    public void action() {
    }

    @Traced(false)
    public void doSomethingUntraced() {
    }

    @Traced(operationName = "customOperationName")
    public void doSomethingWithCustomName() {
    }

    public void withSpanReadyToUse(Span span) {
    }
}
