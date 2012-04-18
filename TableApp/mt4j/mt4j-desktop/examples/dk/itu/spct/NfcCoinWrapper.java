package dk.itu.spct;

import java.io.*;

public class NfcCoinWrapper implements Runnable {

  public interface INfcCardListener {
    public void cardConnected(String id, int availableAmount);

    public void cardDisconnected();
  }

  private INfcCardListener _cardListener;
  private BufferedWriter _stdIn;

  public NfcCoinWrapper(INfcCardListener cardListener) {
    _cardListener = cardListener;
    new Thread(this).start();
  }

  public void charge(int amount) {
    try {
      _stdIn.write("charge/" + amount + System.getProperty("line.separator"));
      _stdIn.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    try {
      Process coinProcess = Runtime.getRuntime().exec("NfcCoin.exe");
      BufferedReader stdOut = new BufferedReader(new InputStreamReader(
          coinProcess.getInputStream()));
      _stdIn = new BufferedWriter(new OutputStreamWriter(
          coinProcess.getOutputStream()));

      while (true) {
        String line = stdOut.readLine();
        if (line == null) {
          System.out.println("NFC helper disconnected");
          return;
        }

        String[] parts = line.split("\\/");
        if (parts[0].equals("disconnected"))
          _cardListener.cardDisconnected();
        else if (parts[0].equals("connected"))
          _cardListener.cardConnected(parts[1], Integer.parseInt(parts[2]));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
  }

}
