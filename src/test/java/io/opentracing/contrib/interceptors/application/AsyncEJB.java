package io.opentracing.contrib.interceptors.application;

import io.opentracing.SpanContext;
import org.eclipse.microprofile.opentracing.Traced;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import java.util.concurrent.Future;

@Stateless
@Traced
@Asynchronous
public class AsyncEJB {

    public Future<Void> doSomethingAsync(SpanContext context) {
        return new AsyncResult<Void>(null);
    }

    public Future<Void> doSomethingAsync() {
        return new AsyncResult<Void>(null);
    }

}
