package org.acme.quickstart;


import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import io.jaegertracing.Configuration;

import io.opentracing.util.GlobalTracer;

@WebListener
public class OpenTracingContextInitializer implements javax.servlet.ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		io.opentracing.Tracer tracer=jaegerTracer();

		GlobalTracer.register(tracer); // or preferably use CDI

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
	
	@Produces
	  @Singleton
	  public static io.opentracing.Tracer jaegerTracer() {
	    return Configuration.fromEnv().getTracer();
	  }

}