package dk.itu.spct;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class ImageUploader {

  public void uploadImage(String imageFile, String nfcId) {
    try {
      HttpClient httpClient = new DefaultHttpClient();
      String uploadUrl = getUploadUrl();
      HttpPost httpPost = new HttpPost(uploadUrl);
      MultipartEntity entity = new MultipartEntity();
      entity.addPart("photos", new FileBody(new File(imageFile), "image/jpeg"));
      entity.addPart("nfcid", new StringBody(nfcId));
      httpPost.setEntity(entity);
      httpClient.execute(httpPost);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getUploadUrl() throws ClientProtocolException, IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpGet get = new HttpGet(
        "http://fluid-photos-at-itu.appspot.com/getUploadUrl");
    ResponseHandler<String> resp = new BasicResponseHandler();
    return httpClient.execute(get, resp);
  }

}
