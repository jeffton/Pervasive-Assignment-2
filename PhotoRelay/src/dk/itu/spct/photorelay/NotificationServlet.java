package dk.itu.spct.photorelay;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

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
