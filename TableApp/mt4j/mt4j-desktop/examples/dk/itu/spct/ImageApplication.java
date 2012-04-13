package dk.itu.spct;

import org.mt4j.MTApplication;

public class ImageApplication extends MTApplication {

  private static final long serialVersionUID = -1551201037642539088L;

  public static void main(String[] args){
		initialize();
	}
	
	@Override
	public void startUp() {
	  addScene(new ImageScene(this, "Image scene"));
	}

}
