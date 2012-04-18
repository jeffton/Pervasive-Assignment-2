package dk.itu.spct;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import edu.gvsu.cis.masl.channelAPI.ChannelAPI;
import edu.gvsu.cis.masl.channelAPI.ChannelService;
import edu.gvsu.cis.masl.channelAPI.ChannelAPI.ChannelException;

public class ImageDownloader implements ChannelService {
  
  private ChannelAPI _channelApi;
  
  public ImageDownloader() {
    try {
      _channelApi = new ChannelAPI("http://fluid-photos-at-itu.appspot.com", "PHOTO_RELAY", this);
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
