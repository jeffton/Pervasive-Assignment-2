using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace NfcCoin
{
  class Program
  {
    private static CardReaderWrapper _reader;
    private enum Modes { Refill, Spend }
    private static Modes _mode = Modes.Spend;

    private static string _refillName;
    private static string _refillAmount;

    static void Main(string[] args)
    {
      ReadArguments(args);

      _reader = new CardReaderWrapper();
      _reader.CardReadyChanged += new CardReaderWrapper.CardReadyChangedHandler(reader_CardReadyChanged);
      _reader.Start();
    }

    private static void ReadArguments(string[] args)
    {
      if (args[0] == "refill")
      {
        _mode = Modes.Refill;
        _refillName = args[1];
        _refillAmount = args[2];
      }
      else if (args[0] == "spend")
      {
        _mode = Modes.Spend;
      }
      else
      {
        Console.WriteLine(@"Usage:
refill <name> <amount>
Reader will save name and amount to any cards inserted.

spend
Once a card is inserted, ""<name>/<amount>"" is sent to stdout.
The reader waits for a line on stdin - if this line has the form ""charge/<amount>"", the amount is charged from the card.
No validation is performed; this must be done on the client side.");


        Environment.Exit(0);
      }
    }

    public static void reader_CardReadyChanged(CardReaderWrapper.CardStatus cardStatus)
    {
      //Console.WriteLine("Card {0} {1}", cardStatus.Atr, cardStatus.Ready ? "ready" : "removed");
      if (cardStatus.Ready)
        HandleCard();
    }

    private static void HandleCard()
    {
      switch (_mode)
      {
        case Modes.Refill:
          RefillCard();
          break;
        case Modes.Spend:
          Spend();
          break;
      }
    }

    private static void Spend()
    {
      string block1 = _reader.ReadBlockAsString(1);
      if (block1.StartsWith("pitC/"))
      {
        string id = block1.Split('/')[1];
        int amount = int.Parse(_reader.ReadBlockAsString(2));

        Console.WriteLine(string.Format("{0}/{1}", id, amount));
        string input = Console.ReadLine();

        if (input.StartsWith("charge/"))
        {
          int charge = int.Parse(input.Split('/')[1]);
          amount -= charge;
          _reader.WriteStringToBlock(2, amount.ToString());
          //Console.WriteLine("Thanks {0}, you have {1} coin(s) left!", id, amount);
        }
      }
    }

    private static void RefillCard()
    {
      string block1 = string.Format("pitC/{0}", _refillName);
      string block2 = _refillAmount;

      _reader.WriteStringToBlock(1, block1);
      _reader.WriteStringToBlock(2, block2);

      Console.WriteLine("Card refilled!");
    }
  }
}
