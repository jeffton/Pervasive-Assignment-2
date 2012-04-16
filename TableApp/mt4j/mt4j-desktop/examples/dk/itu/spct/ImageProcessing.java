package dk.itu.spct;

import java.awt.image.BufferedImage;

public class ImageProcessing {

  public static BufferedImage cropImage(BufferedImage source) {
    int width = (int) (source.getWidth() * 0.75);
    int height = (int) (source.getHeight() * 0.75);
    int left = width / 6;
    int top = height / 6;

    return source.getSubimage(left, top, width, height);
  }

}
