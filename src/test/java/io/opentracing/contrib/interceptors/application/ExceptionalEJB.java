package io.opentracing.contrib.interceptors.application;

import org.eclipse.microprofile.opentracing.Traced;

import javax.ejb.Stateless;

@Stateless
@Traced
public class ExceptionalEJB {
    public void throwException() throws Exception {
        throw new Exception("this is expected to happen");
    }

    public void throwRuntimeException() throws RuntimeException {
        throw new RuntimeException("this is expected to happen");
    }
}
