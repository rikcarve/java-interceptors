package io.opentracing.contrib.interceptors.application;

import org.eclipse.microprofile.opentracing.Traced;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Stateless
@Traced
@Path("/")
public class EndpointOnClass {
    @GET
    public void get() {
        // do nothing
    }
}
