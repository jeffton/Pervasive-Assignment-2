package dk.itu.spct;

import org.mt4j.MTApplication;

public class ImageApplication extends MTApplication {

  private static final long serialVersionUID = -1551201037642539088L;

  public static void main(String[] args){
		initialize();
	}
	
	private static void setupCoinListener() {
    NfcCoinWrapper wrapper = new NfcCoinWrapper(new NfcCoinWrapper.INfcCardHandler() {
      @Override
      public int getChargeForCard(String id, int availableAmount) {
        System.out.println("There's a card!");
        return 4;
      }
    });
  }

  @Override
	public void startUp() {
    setupCoinListener();    
    addScene(new ImageScene(this, "Image scene"));
	}

}
