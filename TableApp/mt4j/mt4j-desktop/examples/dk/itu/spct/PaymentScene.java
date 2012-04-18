package dk.itu.spct;

import org.mt4j.AbstractMTApplication;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import dk.itu.spct.SceneUtilities.TapAction;

public class PaymentScene extends AbstractScene {

  private AbstractMTApplication _application;

  public PaymentScene(AbstractMTApplication application, String name) {
    super(application, name);
    _application = application;

    this.setClearColor(new MTColor(33, 99, 33));
    addButtons();
  }

  private void addButtons() {
    int right = _application.getWidth();
    int bottom = _application.getHeight();

    SceneUtilities.addButton(this, "images/backButton.png", new Vector3D(60,
        bottom - 35), new TapAction() {
      @Override
      public void onTap() {
        _application.popScene();
      }
    });
    SceneUtilities.addButton(this, "images/okButton.png", new Vector3D(right / 2,
        bottom / 2), new TapAction() {
      @Override
      public void onTap() {
        // TODO Auto-generated method stub

      }
    });

  }

}
