package dk.itu.spct;

import java.awt.Image;
import java.util.ArrayList;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

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
    addButtons();
  }

  private void addButtons() {
    MTImageButton effectButton = new MTImageButton(_application,
        _application.loadImage("images/effect1Button.png"));
    effectButton.setPositionGlobal(new Vector3D(964, 30));

    effectButton.addGestureListener(TapProcessor.class,
        new IGestureEventListener() {
          @Override
          public boolean processGestureEvent(MTGestureEvent ge) {
            TapEvent event = (TapEvent) ge;
            if (!event.isTapDown()) {
              applyEffectToSelectedImage();
              return true;
            }
            return false;
          }
        });
    getCanvas().addChild(effectButton);
  }

  private void applyEffectToSelectedImage() {
    if (_selectedImage == null)
      return;

    Image before = _selectedImage.getImage().getTexture().getImage();
    Image after = ImageEffects.cropImage(before);
    _selectedImage.getImage().setTexture(new PImage(after));
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
      MTImage selectedImage = (MTImage) inEvt.getTarget();
      if (_selectedImage != selectedImage) {
        _selectedImage = selectedImage;
        highlightSelectedImage();
      }
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
