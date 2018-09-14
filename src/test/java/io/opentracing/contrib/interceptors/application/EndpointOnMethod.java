package io.opentracing.contrib.interceptors.application;

import org.eclipse.microprofile.opentracing.Traced;

import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Stateless
@Traced
public class EndpointOnMethod {
    @POST
    @Path("/")
    public void post() {
        // do nothing
    }
}
