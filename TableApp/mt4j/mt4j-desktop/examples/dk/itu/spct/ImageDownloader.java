package dk.itu.spct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gvsu.cis.masl.channelAPI.ChannelAPI;
import edu.gvsu.cis.masl.channelAPI.ChannelAPI.ChannelException;
import edu.gvsu.cis.masl.channelAPI.ChannelService;

public class ImageDownloader implements ChannelService {

  public interface ImageListener {
    public void imageDownloaded(String filePath, String nfcId);
  }

  private ChannelAPI _channelApi;
  private ImageListener _imageListener;

  public ImageDownloader(ImageListener imageListener) {
    _imageListener = imageListener;
    try {
      _channelApi = new ChannelAPI(Settings.APP_SPOT_URL, "PHOTO_RELAY", this);
      _channelApi.open();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ChannelException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onOpen() {
    Log.write("Channel open");
  }

  @Override
  public void onMessage(String message) {
    Log.write("Message: " + message);
    try {
      JSONArray jArray = new JSONArray(message);
      for (int i = 0; i < jArray.length(); i++) {
        JSONObject imageObject = jArray.getJSONObject(i);
        handleImageObject(imageObject);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void handleImageObject(JSONObject imageObject) {
    try {
      if (imageObject.getString("source").equals("tabletop"))
        return; // not handling our own images

      String imageId = imageObject.getString("id");
      String nfcId = imageObject.getString("nfcId");
      String fileName = imageObject.getString("filename");
      String imagePath = Settings.IMAGE_DIRECTORY + File.separator + fileName;
      downloadImage(imageId, imagePath);
      _imageListener.imageDownloaded(imagePath, nfcId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void downloadImage(String imageId, String filePath)
      throws ClientProtocolException, IOException {
    HttpClient httpClient = new DefaultHttpClient();
    String url = Settings.APP_SPOT_URL + "/getPhotoById?id=" + imageId;
    HttpGet get = new HttpGet(url);

    ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>() {
      public byte[] handleResponse(HttpResponse response)
          throws ClientProtocolException, IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          return EntityUtils.toByteArray(entity);
        } else {
          return null;
        }
      }
    };
    byte[] response = httpClient.execute(get, handler);
    new FileOutputStream(filePath).write(response);
  }

  @Override
  public void onClose() {
    Log.write("Channel closed");
  }

  @Override
  public void onError(Integer errorCode, String description) {
    Log.write("Error from channel API: " + errorCode + " - " + description);
  }
}
