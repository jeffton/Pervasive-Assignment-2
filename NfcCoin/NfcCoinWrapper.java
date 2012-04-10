import java.io.*;

public class NfcCoinWrapper {

  public interface INfcCardHandler {
    /**
     * @param id
     *          The ID/name of the card
     * @param availableAmount
     *          How many coins are on the card
     * @return The amount of coins to charge (0 to charge nothing)
     */
    public int getChargeForCard(String id, int availableAmount);
  }

  private INfcCardHandler _cardHandler;

  public NfcCoinWrapper(INfcCardHandler cardHandler) {
    _cardHandler = cardHandler;

    try {
      Process coinProcess = Runtime.getRuntime().exec("NfcCoin.exe spend");
      BufferedReader stdOut = new BufferedReader(new InputStreamReader(
          coinProcess.getInputStream()));
      BufferedWriter stdIn = new BufferedWriter(new OutputStreamWriter(
          coinProcess.getOutputStream()));

      while (true) {
        String line = stdOut.readLine();
        String[] parts = line.split("\\/");
        int charge = _cardHandler.getChargeForCard(parts[0],
            Integer.parseInt(parts[1]));
        stdIn.write("charge/" + charge + System.getProperty("line.separator"));
        stdIn.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
  }

}
