
public class NfcInteropTest {

  public static void main(String[] args) {
    NfcCoinWrapper wrapper = new NfcCoinWrapper(new NfcCoinWrapper.INfcCardHandler() {
      @Override
      public int getChargeForCard(String id, int availableAmount) {
        return 4;
      }
    });
  }
  
}
