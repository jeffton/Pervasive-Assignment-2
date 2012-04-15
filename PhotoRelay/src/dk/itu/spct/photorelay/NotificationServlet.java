package dk.itu.spct.photorelay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class NotificationServlet extends RelayServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {		
		String token = channelService.createChannel(CHANNEL_KEY);
	    
	    JSONObject json = new JSONObject();
	    try {
			json.put("token", token);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		resp.setContentType("application/json");
		resp.getWriter().write(json.toString());
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
}
