package dk.itu.spct.photorelay;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public abstract class RelayServlet extends HttpServlet {
	protected BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	protected static final Logger log = Logger.getLogger(RelayServlet.class.getName());
	
	static {
		ObjectifyService.register(Photo.class);
		ObjectifyService.register(RelayClient.class);
	}
	
	protected JSONArray CreateJSONResponse(List<Photo> photos) throws JSONException {
		JSONArray res = new JSONArray();
		for (Photo photo : photos) {
			JSONObject obj = new JSONObject();
			obj.put("id", photo.getId());
			obj.put("nfcId", photo.getNfcId());
			obj.put("filename", photo.getFilename());
			obj.put("uploadedOn", photo.getUploadedOn().getTime());
			res.put(obj);
		}
		return res;
	}

//	public void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws IOException {
//		String uri = req.getRequestURI();
//
//		if (uri.equalsIgnoreCase("/getUploadUrl")) {
//			createUploadUrl(resp);
//		} else if (uri.equalsIgnoreCase("/getPhotoUrls")) {
//			try {
//				getPhotoUrls(resp);
//			} catch (JSONException e) {
//				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
//			}
//		} else if (uri.equalsIgnoreCase("/getPhotoById")) {
//			String id = req.getParameter("id");
//			// validate
//			if(id.isEmpty()) {
//				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
//						"Missing photo ID in query string. Use /getPhotoById?id=<photo id>");
//				return;
//			}
//			servePhotoById(resp, id);
//		} else if (uri.equalsIgnoreCase("/getPhotoUrlsByNfcId")) {
//			String id = req.getParameter("nfcid");
//			// validate
//			if(id.isEmpty()) {
//				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
//						"Missing NFC ID in query string. Use /getPhotoUrlsByNfcId?nfcid=<NFC ID>");
//				return;
//			}
//			try {
//				getPhotoUrlsByNfcId(resp, id);
//			} catch (JSONException e) {
//				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
//			}
//		} else if (uri.equalsIgnoreCase("/deletePhotoById")) {
//			String id = req.getParameter("id");
//			// validate
//			if(id.isEmpty()) {
//				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
//						"Missing photo ID in query string. Use /deletePhotoById?id=<photo id>");
//				return;
//			}
//			deletePhotoById(resp, id);
//		}
//	}
	
//	public void doPost(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		
//		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
//
//		// Photographs are posted from form field named "photos".		
//		List<BlobKey> uploadedPhotos = blobs.get("photos");
//						
//		String id = req.getParameter("nfcid");
//		// validate
//		if(id.isEmpty()) {
//			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
//					"Missing NFCID in post. Please add a field named 'nfcid' with the NFC ID of the uploading device and try again.");
//			return;
//		}
//
//		// Store uploaded photos in object datastore 
//		if (!uploadedPhotos.isEmpty()) {
//			Objectify ofy = ObjectifyService.begin();
//			BlobInfoFactory bif = new BlobInfoFactory();
//			BlobInfo bi;
//
//			for (BlobKey blobKey : uploadedPhotos) {
//				bi = bif.loadBlobInfo(blobKey);
//				Photo p = new Photo();
//				p.setId(blobKey.getKeyString());
//				p.setNfcId(id);
//				p.setUploadedOn(new Date());
//				p.setFilename(bi.getFilename());
//				ofy.put(p);
//				
//				// TODO: notify listeners (table tops) about new photos
//			}
//		}
//
//		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
//	}
//	
//	private void deletePhotoById(HttpServletResponse resp, String id) {
//		Objectify ofy = ObjectifyService.begin();
//		Photo photo = ofy.query(Photo.class).filter("id", id).get();
//		
//		// remove from object data store
//		ofy.delete(photo);
//		
//		// remove from blob store
//		blobstoreService.delete(photo.getBlobKey());
//				
//		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
//	}
//
//	private void getPhotoUrlsByNfcId(HttpServletResponse resp, String nfcid) throws IOException, JSONException {
//		Objectify ofy = ObjectifyService.begin();
//		Iterable<Photo> photos = ofy.query(Photo.class).filter("nfcId", nfcid).fetch();
//		returnPhotoUrls(resp, photos);
//	}
//
//	private void returnPhotoUrls(HttpServletResponse resp, Iterable<Photo> photos) throws IOException, JSONException {
//		JSONArray res = new JSONArray();		
//		for (Photo photo : photos) {
//			JSONObject obj = new JSONObject();
//			obj.put("id", photo.getId());
//			obj.put("nfcId", photo.getNfcId());
//			obj.put("filename", photo.getFilename());
//			obj.put("uploadedOn", photo.getUploadedOn().getTime());
//			res.put(obj);
//		}		
//
//		resp.setContentType("application/json");
//		resp.getWriter().write(res.toString());
//	}
//
//	private void servePhotoById(HttpServletResponse resp, String id)
//			throws IOException {
//		BlobKey blobKey = new BlobKey(id);
//		blobstoreService.serve(blobKey, resp);
//	}
//
//	private void getPhotoUrls(HttpServletResponse resp) throws IOException, JSONException {
//		Objectify ofy = ObjectifyService.begin();
//		Iterable<Photo> photos = ofy.query(Photo.class).fetch();
//		returnPhotoUrls(resp, photos);
//	}
//
//	private void createUploadUrl(HttpServletResponse resp) throws IOException {
//		String uploadUrl = blobstoreService.createUploadUrl("/uploadCompleted");
//		resp.setContentType("text/plain");
//		resp.getWriter().print(uploadUrl);
//	}

}
