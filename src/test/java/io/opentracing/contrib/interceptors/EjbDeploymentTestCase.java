package io.opentracing.contrib.interceptors;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.contrib.interceptors.application.AsyncEJB;
import io.opentracing.contrib.interceptors.application.CustomOperationNameOnClass;
import io.opentracing.contrib.interceptors.application.EndpointOnClass;
import io.opentracing.contrib.interceptors.application.EndpointOnMethod;
import io.opentracing.contrib.interceptors.application.ExceptionalEJB;
import io.opentracing.contrib.interceptors.application.InterceptedEJB;
import io.opentracing.contrib.interceptors.application.NestingEJB;
import io.opentracing.contrib.interceptors.application.TracerProducer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.Tags;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.StatelessBean;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Module;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ApplicationComposer.class)
public class EjbDeploymentTestCase {
    @Inject
    Tracer tracer;

    @Inject
    InterceptedEJB interceptedEJB;

    @Inject
    AsyncEJB asyncEJB;

    @Inject
    NestingEJB nestingEJB;

    @Inject
    EndpointOnMethod endpointOnMethod;

    @Inject
    EndpointOnClass endpointOnClass;

    @Inject
    CustomOperationNameOnClass customOperationNameOnClass;

    @Inject
    ExceptionalEJB exceptionalEJB;

    @Module
    public EjbModule module() {
        final EjbJar ejbJar = new EjbJar();
        ejbJar.addEnterpriseBean(new StatelessBean(AsyncEJB.class));
        ejbJar.addEnterpriseBean(new StatelessBean(CustomOperationNameOnClass.class));
        ejbJar.addEnterpriseBean(new StatelessBean(EndpointOnClass.class));
        ejbJar.addEnterpriseBean(new StatelessBean(EndpointOnMethod.class));
        ejbJar.addEnterpriseBean(new StatelessBean(ExceptionalEJB.class));
        ejbJar.addEnterpriseBean(new StatelessBean(InterceptedEJB.class));
        ejbJar.addEnterpriseBean(new StatelessBean(NestingEJB.class));

        final Beans beans = new Beans();
        beans.addManagedClass(TracerProducer.class);
        beans.addInterceptor(OpenTracingInterceptor.class);

        final EjbModule module = new EjbModule(ejbJar);
        module.setBeans(beans);
        return module;
    }

    @Test
    public void canary() {
        // just a sanity check
        Assert.assertTrue(tracer instanceof MockTracer);
    }

    @Test
    public void regularEjbIsTraced() {
        MockTracer mockTracer = (MockTracer) tracer;

        Assert.assertEquals(0, mockTracer.finishedSpans().size());
        interceptedEJB.action();
        Assert.assertEquals(1, mockTracer.finishedSpans().size());
    }

    @Test
    public void spanIsIntercepted() {
        MockTracer mockTracer = (MockTracer) tracer;

        Assert.assertEquals(0, mockTracer.finishedSpans().size());
        Span span = tracer.buildSpan("spanIsIntercepted").start();
        Scope scope = tracer.activateSpan(span);
        try {
            interceptedEJB.withSpanReadyToUse(span);
        } finally {
            span.finish();
            scope.close();
        }
        Assert.assertEquals(2, mockTracer.finishedSpans().size());
        assertSameTrace(mockTracer.finishedSpans());
    }

    @Test
    public void asyncEjbIsTraced() throws InterruptedException, TimeoutException, ExecutionException {
        MockTracer mockTracer = (MockTracer) tracer;

        Void ignored = asyncEJB.doSomethingAsync().get(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, mockTracer.finishedSpans().size());
    }

    @Test
    public void nestedCalls() throws InterruptedException, ExecutionException, TimeoutException {
        MockTracer mockTracer = (MockTracer) tracer;

        Assert.assertEquals(0, mockTracer.finishedSpans().size());
        nestingEJB.doSomethingNested();
        Assert.assertEquals(3, mockTracer.finishedSpans().size());
        assertSameTrace(mockTracer.finishedSpans());
    }

    @Test
    public void notTraced() {
        MockTracer mockTracer = (MockTracer) tracer;
        interceptedEJB.doSomethingUntraced();
        Assert.assertEquals(0, mockTracer.finishedSpans().size());
    }

    @Test
    public void customOperationNameOnMethod() {
        MockTracer mockTracer = (MockTracer) tracer;
        interceptedEJB.doSomethingWithCustomName();
        Assert.assertEquals("customOperationName", mockTracer.finishedSpans().get(0).operationName());
    }

    @Test
    public void customOperationNameOnClass() {
        MockTracer mockTracer = (MockTracer) tracer;
        customOperationNameOnClass.doSomething();
        Assert.assertEquals("myVeryOwnName", mockTracer.finishedSpans().get(0).operationName());
    }

    @Test
    public void defaultOperationName() {
        MockTracer mockTracer = (MockTracer) tracer;
        interceptedEJB.action();
        Assert.assertEquals(InterceptedEJB.class.getName() + ".action", mockTracer.finishedSpans().get(0).operationName());
    }

    @Test
    public void doNotTraceJaxRsOnMethod() {
        MockTracer mockTracer = (MockTracer) tracer;
        endpointOnMethod.post();
        Assert.assertEquals(0, mockTracer.finishedSpans().size());
    }

    @Test
    public void doNotTraceJaxRsOnClass() {
        MockTracer mockTracer = (MockTracer) tracer;
        endpointOnClass.get();
        Assert.assertEquals(0, mockTracer.finishedSpans().size());
    }

    @Test
    public void exceptionIsLogged() {
        MockTracer mockTracer = (MockTracer) tracer;
        Throwable throwed = null;

        try {
            exceptionalEJB.throwException();
        } catch (Throwable t) {
            throwed = t;
        }
        Assert.assertEquals(1, mockTracer.finishedSpans().size());
        Assert.assertNotNull(throwed);
        assertSpanHasErrorFor(mockTracer.finishedSpans().get(0), throwed);
    }

    @Test
    public void runtimeExceptionIsLogged() {
        MockTracer mockTracer = (MockTracer) tracer;
        Throwable throwed = null;

        try {
            exceptionalEJB.throwRuntimeException();
        } catch (Throwable t) {
            throwed = t;
        }
        Assert.assertEquals(1, mockTracer.finishedSpans().size());
        Assert.assertNotNull(throwed);

        // runtimeExceptions get wrapped as EJBException, so, get the underlying cause
        assertSpanHasErrorFor(mockTracer.finishedSpans().get(0), throwed.getCause());
    }

    private void assertSpanHasErrorFor(MockSpan span, Throwable throwed) {
        Assert.assertTrue((Boolean) span.tags().get("error"));

        // one is an event, the other should be error.object
        Assert.assertEquals(1, span.logEntries().size());
        MockSpan.LogEntry logEntry = span.logEntries().get(0);
        Assert.assertEquals(Tags.ERROR.getKey(), logEntry.fields().get("event"));
        Assert.assertEquals(throwed, logEntry.fields().get("error.object"));
    }

    private void assertSameTrace(List<MockSpan> spans) {
        long traceId = spans.get(0).context().traceId();
        for (MockSpan span : spans) {
            Assert.assertEquals(traceId, span.context().traceId());
        }
    }
}
