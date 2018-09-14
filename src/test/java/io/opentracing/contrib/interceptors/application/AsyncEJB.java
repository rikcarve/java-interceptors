package io.opentracing.contrib.interceptors.application;

import io.opentracing.SpanContext;
import org.eclipse.microprofile.opentracing.Traced;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import java.util.concurrent.CountDownLatch;

@Stateless
@Traced
@Asynchronous
public class AsyncEJB {

    public void doSomethingAsync(SpanContext context, CountDownLatch latch) {
        latch.countDown();
    }

    public void doSomethingAsync(CountDownLatch latch) {
        latch.countDown();
    }

}
