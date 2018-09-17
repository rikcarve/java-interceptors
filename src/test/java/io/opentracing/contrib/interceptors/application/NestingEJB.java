package io.opentracing.contrib.interceptors.application;

import io.opentracing.SpanContext;
import org.eclipse.microprofile.opentracing.Traced;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public void doSomethingNested() throws ExecutionException, InterruptedException, TimeoutException {
        Future<Void> result = asyncEJB.doSomethingAsync((SpanContext) ctx.getContextData().get(SPAN_CONTEXT));
        interceptedEJB.action();
        Void ignored = result.get(1, TimeUnit.SECONDS);
    }
}
