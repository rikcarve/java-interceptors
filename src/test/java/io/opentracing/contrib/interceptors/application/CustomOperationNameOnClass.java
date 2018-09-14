package io.opentracing.contrib.interceptors.application;

import org.eclipse.microprofile.opentracing.Traced;

import javax.ejb.Stateless;

@Traced(operationName = "myVeryOwnName")
@Stateless
public class CustomOperationNameOnClass {

    public void doSomething() {
        // do something
    }
}
