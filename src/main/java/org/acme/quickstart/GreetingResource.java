package org.acme.quickstart;

import java.io.BufferedReader;
import java.io.IOException;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
		
		//Dump HTTP heads
		LOGGER.info("Dump HTTP Headers of Request");
		for (Entry<String, List<String>> e:headers.getRequestHeaders().entrySet())
		{
			LOGGER.info(e.getKey()+":"+e.getValue().get(0));
		}

		
		if (downstreams != null) {
			for (String downs : downstreams.split(",")) {
				try {
					URL url = new URL("http://" + downs);
					LOGGER.info("Calling "+url.toExternalForm());
					

					
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					for (Entry<String, List<String>> e:headers.getRequestHeaders().entrySet())
					{
//						if(e.getKey().equals("x-b3-spanid"))
//						{
//							//ParentSpan=span
//							LOGGER.info("Propagating X x-b3-parentspanid:"+e.getValue().get(0));
//							con.setRequestProperty("x-b3-parentspanid",e.getValue().get(0));
//							String newSpan=Long.toHexString(new Random().nextLong()); 
//							LOGGER.info("Propagating X x-b3-spanid:"+newSpan);
//							con.setRequestProperty("x-b3-spanid",newSpan);
//							
//							
//						}
//						else if(e.getKey().equals("x-b3-parentspanid"))
//						{
//							LOGGER.info("Not Propagating (copy span to parent) "+e.getKey()+":"+e.getValue().get(0));
//						
//						}
//						else 
							if (e.getKey().startsWith("x-") || e.getKey().equals("b3"))
						{
							LOGGER.info("Propagating     |"+e.getKey()+":"+e.getValue().get(0));
							con.setRequestProperty(e.getKey(),e.getValue().get(0));
						}else {
							LOGGER.info("Not Propagating |"+e.getKey()+":"+e.getValue().get(0));
							
						}
					}
				
					con.setRequestMethod("GET");
				
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					LOGGER.info("Called ");
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					response.append(',');
					response.append(content);
					in.close();
					LOGGER.fine("Response :"+content);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		LOGGER.info("Exit hello() returns"+response.toString());

		return response.toString();

	}
}