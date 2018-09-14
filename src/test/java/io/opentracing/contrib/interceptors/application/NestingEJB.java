package io.opentracing.contrib.interceptors.application;

import io.opentracing.SpanContext;
import org.eclipse.microprofile.opentracing.Traced;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.opentracing.contrib.interceptors.OpenTracingInterceptor.SPAN_CONTEXT;

@Stateless
@Traced
public class NestingEJB {

    @Inject
    AsyncEJB asyncEJB;

    @Inject
    InterceptedEJB interceptedEJB;

    @Resource
    EJBContext ctx;

    public void doSomethingNested() {
        CountDownLatch latch = new CountDownLatch(1);
        asyncEJB.doSomethingAsync((SpanContext) ctx.getContextData().get(SPAN_CONTEXT), latch);
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        interceptedEJB.action();
    }
}
