package dk.itu.spct.photorelay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class NotificationServlet extends RelayServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {		
		// create new channel token for client
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String token = channelService.createChannel(RelayClient.CreateIdFromRequest(req));
		resp.setContentType("text/html");
	    resp.getWriter().write(token);
	    resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
				
		Objectify ofy = ObjectifyService.begin();
		RelayClient client;
		if(presence.isConnected()) {
			client = new RelayClient();
			client.id = presence.clientId();
			client.type = ClientType.CUSTOMER;
			ofy.put(client);
		} else {
			client = ofy.query(RelayClient.class).filter("id", presence.clientId()).get();
			ofy.delete(client);
		}
	}
}
