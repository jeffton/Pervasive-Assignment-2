package dk.itu.spct.photorelay;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class GetPhotoUrls extends RelayServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Objectify ofy = ObjectifyService.begin();
		List<Photo> photos;

		// get nfcid (check if it was supplied
		String nfcid = req.getParameter("nfcid");
		if (nfcid == null || nfcid.isEmpty()) {
			photos = ofy.query(Photo.class).list();
		} else {
			photos = ofy.query(Photo.class).filter("nfcId", nfcid).list();
		}

		// create JSON result		
		try {
			JSONArray json = CreateJSONResponse(photos);
			resp.setContentType("application/json");
			resp.getWriter().write(json.toString());
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (JSONException e) {
			log.throwing("GetPhotoUrls", "CreateJSONResponse", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
