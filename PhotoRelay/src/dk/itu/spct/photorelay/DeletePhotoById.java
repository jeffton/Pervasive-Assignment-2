package dk.itu.spct.photorelay;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class DeletePhotoById extends RelayServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String id = req.getParameter("id");
		// validate
		if(id.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"Missing photo ID in query string. Use /deletePhotoById?id=<photo id>");
			return;
		}
		
		Objectify ofy = ObjectifyService.begin();
		Photo photo = ofy.query(Photo.class).filter("id", id).get();
		
		// remove from object data store
		ofy.delete(photo);
		
		// remove from blob store
		blobstoreService.delete(photo.getBlobKey());
				
		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}
