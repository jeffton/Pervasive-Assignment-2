using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using General.Threading;
using SpringCardPCSC;

namespace NfcCoin
{
  public class CardReaderWrapper
  {
    private SCardReader _reader;
    private SCardChannel _channel;
    private RequestQueue<CardStatus, object> _debouncer; // not really what it's intened for, but it does the job.
    private bool _cardReportedReady = false;
    public delegate void CardReadyChangedHandler(CardStatus cardStatus);
    public event CardReadyChangedHandler CardReadyChanged;

    public CardReaderWrapper()
    {
      string readerName = SCARD.Readers.First();
      _reader = new SCardReader(readerName);
      _channel = new SCardChannel(_reader);
    }

    public void Start()
    {
      _debouncer = new RequestQueue<CardStatus, object>((status) => { OnCardStatusStable(status); return null; }, timeout: 300);
      _reader.StartMonitor(OnReaderStatusChanged);
    }

    private void OnReaderStatusChanged(uint readerState, CardBuffer cardAtr)
    {
      bool cardPresent = ((readerState & SCARD.STATE_PRESENT) > 0);
      bool cardInUse = ((readerState & SCARD.STATE_INUSE) > 0);
      string cardAtrString = cardAtr == null ? "-" : cardAtr.AsString();

      _debouncer.Request(new CardStatus()
      {
        Atr = cardAtrString,
        Present = cardPresent,
        InUse = cardInUse
      });
    }

    private void OnCardStatusStable(CardStatus cardStatus)
    {
      var readyHandler = CardReadyChanged;
      if (readyHandler != null && cardStatus.Ready != _cardReportedReady)
      {
        _cardReportedReady = cardStatus.Ready;        
        readyHandler(cardStatus);
      }
    }

    public string ReadBlockAsString(byte blockNumber)
    {
      string[] commandStrings = new string[]
      {
        GetLoadKeyCommand(),
        GetAuthCommand(blockNumber),
        GetReadCommand(blockNumber)
      };

      var lastResponse = SendCommands(commandStrings).Last();
      return Encoding.ASCII.GetString(lastResponse.Bytes.Take(16).ToArray()).Trim();
    }

    public void WriteStringToBlock(byte blockNumber, string str)
    {
      string[] commandStrings = new string[]
      {
        GetLoadKeyCommand(),
        GetAuthCommand(blockNumber),
        GetWriteCommand(blockNumber, str)
      };

      SendCommands(commandStrings);
    }

    public List<RAPDU> SendCommands(string[] commandStrings)
    {
      var responses = new List<RAPDU>();
      _channel.Connect();
      foreach (string commandString in commandStrings)
      {
        CAPDU command = new CAPDU(commandString);
//        Console.WriteLine("Command:  {0}", commandString);
        RAPDU response = _channel.Transmit(command);
//        Console.WriteLine("Response: {0}", response.AsString(" "));
        responses.Add(response);
      }
      _channel.Disconnect();
      return responses;
    }
    
    private string GetLoadKeyCommand()
    {
      return "FF 82 00 00 06 FF FF FF FF FF FF"; // Load (Mifare Default) key in reader (key location 0)
    }

    private string GetAuthCommand(byte blockNumber)
    {
      return string.Format("FF 86 00 00 05 01 00 {0} 60 00", blockNumber.ToString("X2"));
    }

    private string GetReadCommand(byte blockNumber)
    {
      return string.Format("FF B0 00 {0} 10", blockNumber.ToString("X2"));
    }

    private string GetWriteCommand(byte blockNumber, string str)
    {
      str = (str + new string(' ', 16)).Substring(0, 16); // exactly 16 chars, space padded.
      string strHex = new CardBuffer(Encoding.ASCII.GetBytes(str)).AsString(" ");
      return string.Format("FF D6 00 {0} 10 {1}", blockNumber.ToString("X2"), strHex);
    }

    public class CardStatus
    {
      public string Atr { get; set; }
      public bool Present { get; set; }
      public bool InUse { get; set; }

      public bool Ready
      {
        get
        {
          return Present;
        }
      }
    }
  }
}

/*
  ; ---------------------------------------------------------------
  ; Demo script for MIFARE 1K card and ACR122U PC/SC reader
  ; (c) 2008, Advanced Card Systems Ltd.
  ; ---------------------------------------------------------------

  ; [1] Load (Mifare Default) key in reader (key location 0)
  FF 82 00 00 06 FF FF FF FF FF FF (9000)

  ; [2] Authenticate sector 0, Block 0 with key at location 0
  FF 86 00 00 05 01 00 00 60 00 (9000)

  ; [3] Read the full 16 bytes from Sector 0, Block 1
  FF B0 00 01 10 [xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx] (9000)

  ; [4] Update the 16 bytes in Sector 0, block 1
  FF D6 00 01 10 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F (9000)

  ; [5] Read the full 16 bytes from Sector 0, Block 1 again
  FF B0 00 01 10 [xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx] (9000)

  ; [6] Authenticate sector 1, Block 5 with key at location 0
  FF 86 00 00 05 01 00 05 60 00 (9000)

  ; [7] Store a value "1" into block 5
  FF D7 00 05 05 00 00 00 00 01 (9000)

  ; [8] Read the value block 5
  FF B1 00 05 04 [xx xx xx xx] (9000)

  ; [9] Copy the value from value block 5 to value block 6 
  FF D7 00 05 02 03 06 (9000)

  ; [10] Read the value block 6
  FF B1 00 06 04 [xx xx xx xx] (9000)

  ; [11] Increment the value block 5 by "5"
  FF D7 00 05 05 01 00 00 00 05 (9000)

  ; [12] Read the value block 5
  FF B1 00 05 04 [xx xx xx xx] (9000)
*/