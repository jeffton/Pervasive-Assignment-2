package dk.itu.spct;

import java.util.ArrayList;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;

public class ImageScene extends AbstractScene {

  private ArrayList<MTImage> _images = new ArrayList<MTImage>();
  private MTImage _selectedImage = null;
  private AbstractMTApplication _application;

  public ImageScene(AbstractMTApplication mtApplication, String name) {
    super(mtApplication, name);
    _application = mtApplication;

    this.setClearColor(new MTColor(66, 66, 66));

    addImageFromFile("images/smiling-cat.jpg");
    addImageFromFile("images/photo_cat2.jpg");
  }

  private void addImageFromFile(String path) {
    MTImage image = new MTImage(_application, _application.loadImage(path));
    _images.add(image);
    image.addInputListener(_imageInputListener);
    getCanvas().addChild(image);
  }

  private IMTInputEventListener _imageInputListener = new IMTInputEventListener() {
    @Override
    public boolean processInputEvent(MTInputEvent inEvt) {
      _selectedImage = (MTImage) inEvt.getTarget();
      highlightSelectedImage();
      return false;
    }

    private void highlightSelectedImage() {
      for (MTImage image : _images) {
        MTColor color = null;
        if (image == _selectedImage)
          color = new MTColor(255, 255, 0);
        else
          color = new MTColor(255, 255, 255);

        image.setStrokeColor(color);
        image.setFillColor(color);
      }

    }
  };

}
