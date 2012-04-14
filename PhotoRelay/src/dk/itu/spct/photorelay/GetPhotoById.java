package dk.itu.spct.photorelay;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;

@SuppressWarnings("serial")
public class GetPhotoById extends RelayServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String id = req.getParameter("id");
		
		// validate
		if(id.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"Missing photo ID in query string. Use /getPhotoById?id=<photo id>");
			return;
		}
		
		BlobKey blobKey = new BlobKey(id);
		BlobInfoFactory bif = new BlobInfoFactory();
		BlobInfo bi = bif.loadBlobInfo(blobKey);
		
		if(bi == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"Could not find a photo matching the supplied photo ID.");
			return;
		}
		
		blobstoreService.serve(blobKey, resp);
	}
}
