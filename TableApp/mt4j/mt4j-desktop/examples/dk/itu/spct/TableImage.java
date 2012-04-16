package dk.itu.spct;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.mt4j.components.visibleComponents.widgets.MTImage;

import processing.core.PApplet;

public class TableImage extends MTImage {

  private String _filePath;
  private String _nfcId;
  private BufferedImage _image;
  private PApplet _application;

  public TableImage(PApplet application, String filePath, String nfcId) {
    super(application, application.loadImage(filePath));
    _application = application;
    _filePath = filePath;
    _nfcId = nfcId;
    try {
      _image = ImageIO.read(new File(filePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getFilePath() {
    return _filePath;
  }

  public String getNfcId() {
    return _nfcId;
  }

  public BufferedImage getBufferedImage() {
    return _image;
  }

  public void setBufferedImage(BufferedImage image) {
    try {
      _image = image;
      ImageIO.write(_image, "jpeg", new File(_filePath));
      getImage().setTexture(_application.loadImage(_filePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
