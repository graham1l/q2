package org.acme.quickstart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/hello")
public class GreetingResource {

	@Inject
	GreetingService service;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/greeting/{name}")
	public String greeting(@PathParam String name) {
		return service.greeting(name);
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		String downstreams = System.getenv("DOWNSTREAMS");

		StringBuffer response = new StringBuffer();
		response.append("hello");
		if (downstreams != null) {
			for (String downs : downstreams.split(",")) {
				try {
					URL url = new URL("http://" + downs);

					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					response.append(',');
					response.append(content);
					in.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return response.toString();

	}
}