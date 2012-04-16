package dk.itu.spct;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.mt4j.AbstractMTApplication;
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

public class ImageScene extends AbstractScene {

  private interface TapAction {
    public void onTap();
  }

  private ArrayList<TableImage> _images = new ArrayList<TableImage>();
  private TableImage _selectedImage = null;
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
    addButton("images/effect1Button.png", new Vector3D(964, 30),
        new TapAction() {
          @Override
          public void onTap() {
            applyEffectToSelectedImage();
          }
        });
    addButton("images/uploadButton.png", new Vector3D(964, 200), new TapAction() {
      @Override
      public void onTap() {
        testUpload();
      }
    });
  }

  protected void testUpload() {
    if (_selectedImage == null)
      return;
    
    new ImageUploader().uploadImage("images/smiling-cat.jpg", "dummyId");
  }

  private void addButton(String imageFile, Vector3D position,
      final TapAction action) {
    MTImageButton button = new MTImageButton(_application,
        _application.loadImage(imageFile));
    button.setPositionGlobal(position);

    button.addGestureListener(TapProcessor.class,
        new IGestureEventListener() {
          @Override
          public boolean processGestureEvent(MTGestureEvent ge) {
            TapEvent event = (TapEvent) ge;
            if (!event.isTapDown()) {
              action.onTap();
              return true;
            }
            return false;
          }
        });
    getCanvas().addChild(button);

  }

  private void applyEffectToSelectedImage() {
    if (_selectedImage == null)
      return;

    BufferedImage before = _selectedImage.getBufferedImage();
    BufferedImage after = ImageProcessing.cropImage(before);
    _selectedImage.setBufferedImage(after);
  }

  private void addImageFromFile(String path) {
    TableImage image = new TableImage(_application, path, "dummyId");
    _images.add(image);
    image.addInputListener(_imageInputListener);
    getCanvas().addChild(image);
  }

  private IMTInputEventListener _imageInputListener = new IMTInputEventListener() {
    @Override
    public boolean processInputEvent(MTInputEvent inEvt) {
      TableImage selectedImage = (TableImage) inEvt.getTarget();
      if (_selectedImage != selectedImage) {
        _selectedImage = selectedImage;
        highlightSelectedImage();
      }
      return false;
    }

    private void highlightSelectedImage() {
      for (TableImage image : _images) {
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
