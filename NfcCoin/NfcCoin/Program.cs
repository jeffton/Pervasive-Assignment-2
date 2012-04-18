using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.ComponentModel;
using System.Threading;

namespace NfcCoin
{
  class Program
  {
    private static CardReaderWrapper _reader;
    private static bool _coinCardConnected = false;

    static void Main(string[] args)
    {
      KillOtherInstances();

      try
      {
        _reader = new CardReaderWrapper();
        _reader.CardReadyChanged += new CardReaderWrapper.CardReadyChangedHandler(reader_CardReadyChanged);
        _reader.Start();
        ThreadPool.QueueUserWorkItem((state) => HandleCommands());
      }
      catch (NoReaderConnectedException)
      {
        Console.Error.WriteLine("No NFC-reader");
        Environment.Exit(1);
      }
    }

    private static object HandleCommands()
    {
      while (true)
      {
        string input = Console.ReadLine();
        string[] parts = input.Split('/');
        if (parts[0] == "charge")
        {
          int charge = int.Parse(parts[1]);
          ChargeCard(charge);
        }
        else if (parts[0] == "refill")
        {
          string id = parts[1];
          int amount = int.Parse(parts[2]);
          WriteCoinStatus(new CoinStatus() { Id = id, Amount = amount });
        }
      }
    }

    private static void ChargeCard(int charge)
    {
      var coinStatus = ReadCoinStatus();
      if (coinStatus != null)
      {
        coinStatus.Amount -= charge;
        WriteCoinStatus(coinStatus);
      }
    }

    private static void KillOtherInstances()
    {
      foreach (var process in Process.GetProcessesByName("NfcCoin")
        .Where(p => p.Id != Process.GetCurrentProcess().Id))
      {
        TryKillProcess(process);
      }
    }

    private static void TryKillProcess(Process process)
    {
      try
      {
        process.Kill();
      }
      catch (Exception ex)
      {
        if (!(ex is Win32Exception || ex is InvalidOperationException))
          throw;
      }
    }

    public static void reader_CardReadyChanged(CardReaderWrapper.CardStatus cardStatus)
    {
      if (cardStatus.Ready)
        PrintCoinStatus();
      else
        Console.WriteLine("disconnected");
    }

    private static void PrintCoinStatus()
    {
      var status = ReadCoinStatus();
      if (status != null)
        Console.WriteLine(string.Format("connected/{0}/{1}", status.Id, status.Amount));
    }

    private static CoinStatus ReadCoinStatus()
    {
      string block1 = _reader.ReadBlockAsString(1);
      if (block1.StartsWith("pitC/"))
      {
        string id = block1.Split('/')[1];
        int amount = int.Parse(_reader.ReadBlockAsString(2));
        return new CoinStatus() { Id = id, Amount = amount };
      }
      return null;
    }

    private static void WriteCoinStatus(CoinStatus status)
    {
      string block1 = string.Format("pitC/{0}", status.Id);
      string block2 = status.Amount.ToString();

      _reader.WriteStringToBlock(1, block1);
      _reader.WriteStringToBlock(2, block2);
    }

    private class CoinStatus
    {
      public string Id { get; set; }
      public int Amount { get; set; }
    }
  }
}
