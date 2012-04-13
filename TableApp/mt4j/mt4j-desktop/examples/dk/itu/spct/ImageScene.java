package dk.itu.spct;

import java.util.ArrayList;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeEvent;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;

public class ImageScene extends AbstractScene {
  
  private ArrayList<MTImage> _images = new ArrayList<MTImage>(); 
  private AbstractMTApplication _application;

  public ImageScene(AbstractMTApplication mtApplication, String name) {
    super(mtApplication, name);
    _application = mtApplication;
    
    this.setClearColor(new MTColor(66,66,66));
    
    addImageFromFile("images/smiling-cat.jpg");
    addImageFromFile("images/photo_cat2.jpg");
  }

  private void addImageFromFile(String path) {
    MTImage image = new MTImage(_application, _application.loadImage(path));
    _images.add(image);
    getCanvas().addChild(image);
  }

}

