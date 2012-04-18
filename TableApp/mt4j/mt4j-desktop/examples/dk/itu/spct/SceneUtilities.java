package dk.itu.spct;

import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.math.Vector3D;

import processing.core.PApplet;

public class SceneUtilities {
  
  public interface TapAction {
    public void onTap();
  }
  
  public static void addButton(AbstractScene scene, String imageFile, Vector3D position,
      final TapAction action) {
    PApplet application = scene.getMTApplication();
    MTImageButton button = new MTImageButton(application,
        application.loadImage(imageFile));
    button.setPositionGlobal(position);

    button.addGestureListener(TapProcessor.class, new IGestureEventListener() {
      @Override
      public boolean processGestureEvent(MTGestureEvent ge) {
        TapEvent event = (TapEvent) ge;
        if (event.isTapped()) {
          action.onTap();
          return true;
        }
        return false;
      }
    });
    scene.getCanvas().addChild(button);
  }
}
