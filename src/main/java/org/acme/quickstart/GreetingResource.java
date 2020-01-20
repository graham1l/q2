package org.acme.quickstart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/hello")
public class GreetingResource {
	protected static final Logger LOGGER = Logger.getLogger(GreetingResource.class.getName());

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
		LOGGER.info("Entered hello()");
		String downstreams = System.getenv("DOWNSTREAMS");
		LOGGER.info("Downstreams="+downstreams);
		String msg = System.getenv("MESSAGE");
		if (msg==null) 
		{
			msg="Hello";
		}
		LOGGER.info("Message="+msg);
		for (Entry<String, String> entry : System.getenv().entrySet())
		{
			LOGGER.info("Env:"+entry.getKey()+"="+entry.getValue());
			
		}


		StringBuffer response = new StringBuffer();
		response.append(msg);
		if (downstreams != null) {
			for (String downs : downstreams.split(",")) {
				try {
					URL url = new URL("http://" + downs);
					LOGGER.info(url.toExternalForm());

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
		LOGGER.info("Exit hello()");

		return response.toString();

	}
}