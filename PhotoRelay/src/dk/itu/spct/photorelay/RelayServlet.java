package dk.itu.spct.photorelay;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public abstract class RelayServlet extends HttpServlet {
	protected static BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	protected static ChannelService channelService = ChannelServiceFactory.getChannelService();
	protected static final Logger log = Logger.getLogger(RelayServlet.class.getName());
	protected static final String CHANNEL_KEY = "PHOTO_RELAY";
	
	static {
		ObjectifyService.register(Photo.class);
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

}
