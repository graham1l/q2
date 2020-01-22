package org.acme.quickstart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	public String hello(@Context HttpHeaders headers) {
		LOGGER.info("Entered hello()");
		String downstreams = System.getenv("DOWNSTREAMS");
		LOGGER.info("Downstreams="+downstreams);
		String msg = System.getenv("MESSAGE");
		if (msg==null) 
		{
			msg="Hello";
		}
		msg+=" "+System.getenv("HOSTNAME");
		LOGGER.info("Message="+msg);
		
//		for (Entry<String, String> entry : System.getenv().entrySet())
//		{
//			LOGGER.info("Env:"+entry.getKey()+"="+entry.getValue());
//			
//		}


		StringBuffer response = new StringBuffer();
		response.append(msg);
		if (downstreams != null) {
			for (String downs : downstreams.split(",")) {
				try {
					Client client = ClientBuilder.newClient();
					WebTarget target = client.target("http://" + downs);
					Builder req = target.request(MediaType.TEXT_PLAIN);
				
					LOGGER.info(target.toString());
					

				
//					for (Entry<String, List<String>> e:headers.getRequestHeaders().entrySet())
//					{
//						con.setRequestProperty(e.getKey(),e.getValue().get(0));
//					}
					
				
					InputStream res = req.get(InputStream.class);
					BufferedReader in = new BufferedReader(new InputStreamReader(res));
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