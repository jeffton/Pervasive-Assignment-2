package dk.itu.spct.photorelay;

import java.util.Date;

import javax.persistence.Id;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.annotation.Cached;

@Cached
public class Photo {
	@Id private String id;
	private String nfcId;
	private Date uploadedOn;
	private String filename;
	
	public BlobKey getBlobKey(){
		return new BlobKey(id);
	}	

	public String getNfcId() {
		return nfcId;
	}

	public void setNfcId(String nfcId) {
		this.nfcId = nfcId;
	}

	public Date getUploadedOn() {
		return uploadedOn;
	}

	public void setUploadedOn(Date uploadedOn) {
		this.uploadedOn = uploadedOn;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
