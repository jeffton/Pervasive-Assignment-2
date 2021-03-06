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

  public void uploadImage(TableImage image) {
    try {
      HttpClient httpClient = new DefaultHttpClient();
      String uploadUrl = getUploadUrl();
      HttpPost httpPost = new HttpPost(uploadUrl);
      MultipartEntity entity = new MultipartEntity();
      entity.addPart("photos", new FileBody(new File(image.getFilePath()),
          "image/jpeg"));
      entity.addPart("nfcid", new StringBody(image.getNfcId()));
      entity.addPart("source", new StringBody("tabletop"));
      httpPost.setEntity(entity);
      httpClient.execute(httpPost);
      Log.write("Uploaded " + image.getFilePath());
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
    HttpGet get = new HttpGet(Settings.APP_SPOT_URL + "/getUploadUrl");
    ResponseHandler<String> resp = new BasicResponseHandler();
    return httpClient.execute(get, resp);
  }

}
