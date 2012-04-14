package dk.itu.spct;

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

public class SampleImageScene extends AbstractScene {

  public SampleImageScene(AbstractMTApplication mtApplication, String name) {
    super(mtApplication, name);
    
    this.setClearColor(new MTColor(66,66,66));
    
    MTImage image1 = new MTImage(mtApplication, mtApplication.loadImage("images/photo_cat2.jpg"));
    getCanvas().addChild(image1);
    
    UnistrokeProcessor up = new UnistrokeProcessor(mtApplication);
    up.addTemplate(UnistrokeGesture.CIRCLE, Direction.CLOCKWISE);
    up.addTemplate(UnistrokeGesture.CIRCLE, Direction.COUNTERCLOCKWISE);
    up.addTemplate(UnistrokeGesture.RECTANGLE, Direction.CLOCKWISE);
    up.addTemplate(UnistrokeGesture.RECTANGLE, Direction.COUNTERCLOCKWISE);
    up.addTemplate(UnistrokeGesture.V, Direction.CLOCKWISE);
    
    image1.unregisterAllInputProcessors();
    image1.removeAllGestureEventListeners();
    
    
    
    image1.registerInputProcessor(up);
    image1.addGestureListener(UnistrokeProcessor.class, new IGestureEventListener(){
      @Override
      public boolean processGestureEvent(MTGestureEvent ge) {
        UnistrokeEvent ue = (UnistrokeEvent)ge;        
        
        if (ue.getId() == UnistrokeEvent.GESTURE_ENDED && 
            ue.getGesture() == UnistrokeGesture.V) {
          System.out.println("We have a V on " + ue.getTarget());
        }
        
        return false;
      }
    });
    
    MTImage image2 = new MTImage(mtApplication, mtApplication.loadImage("images/smiling-cat.jpg"));
    getCanvas().addChild(image2);
  }

}
