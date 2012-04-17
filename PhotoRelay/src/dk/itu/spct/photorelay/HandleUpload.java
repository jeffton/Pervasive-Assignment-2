package dk.itu.spct.photorelay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.channel.ChannelMessage;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class HandleUpload extends RelayServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String uploadUrl = blobstoreService.createUploadUrl("/uploadCompleted");
		resp.setContentType("text/plain");
		resp.getWriter().print(uploadUrl);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);

		// Photographs are posted from form field named "photos".		
		List<BlobKey> uploadedPhotos = blobs.get("photos");
						
		String id = req.getParameter("nfcid");
		// validate
		if(id.isEmpty()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
					"Missing NFCID in post. Please add a field named 'nfcid' with the NFC ID of the uploading device and try again.");
			return;
		}

		// Store uploaded photos in object datastore 
		if (!uploadedPhotos.isEmpty()) {
			Objectify ofy = ObjectifyService.begin();
			BlobInfoFactory bif = new BlobInfoFactory();
			BlobInfo bi;
			List<Photo> photos = new ArrayList<Photo>(uploadedPhotos.size());

			for (BlobKey blobKey : uploadedPhotos) {
				bi = bif.loadBlobInfo(blobKey);
				Photo p = new Photo();
				p.setId(blobKey.getKeyString());
				p.setNfcId(id);
				p.setUploadedOn(new Date());
				p.setFilename(bi.getFilename());
				p.setSource(req.getRemoteAddr() + ":" + req.getRemotePort());
				ofy.put(p);
				photos.add(p);
			}
			
			NotifyListeners(photos);
		}

		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	private void NotifyListeners(List<Photo> photos) {	
		try {
			String msg = CreateJSONResponse(photos).toString();				
			channelService.sendMessage(new ChannelMessage(CHANNEL_KEY, msg));				
		} catch (JSONException e) {
			log.throwing("HandleUpload", "CreateJSONResponse", e);
		}				
	}
}
