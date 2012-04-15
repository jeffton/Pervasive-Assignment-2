using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;

namespace General.Threading
{
  public class RequestQueue<TRequestParameter, TResponse> : IDisposable
  {
    private int _timeout;
    private object _lockObject = new object();
    private bool _stopped = false;
    private RequestWrapper _lastRequest;
    private Func<TRequestParameter, TResponse> _requestMethod;
    private Thread _thread;

    public delegate void ResponseReadyHandler(TRequestParameter requestParameter, TResponse response);
    public event ResponseReadyHandler ResponseReady;
    public delegate void RequestFailedHandler(TRequestParameter request, Exception exception);
    public event RequestFailedHandler RequestFailed;
    public delegate void ProgressIndicationHandler(bool working);
    public event ProgressIndicationHandler ProgressIndication;

    public RequestQueue(Func<TRequestParameter, TResponse> requestMethod,
      int timeout = 300, ThreadPriority threadPriority = ThreadPriority.Normal)
    {
      _requestMethod = requestMethod;
      _timeout = timeout;
      _thread = new Thread(ExecuteRequests);
      _thread.IsBackground = true;
      _thread.Priority = threadPriority;
      _thread.Start();
    }

    private void ExecuteRequests()
    {
      while (!_stopped)
      {
        TRequestParameter request = WaitForRequest();
        OnProgressIndication(true);
        try
        {
          TResponse response = _requestMethod(request);
          OnResponseReady(request, response);
        }
        catch (Exception ex)
        {
          OnActionFailed(request, ex);
        }
        finally
        {
          OnProgressIndication(false);
        }
      }
    }

    private void OnResponseReady(TRequestParameter request, TResponse response)
    {
      var handler = ResponseReady;
      if (handler != null)
        ResponseReady(request, response);
    }

    private void OnActionFailed(TRequestParameter request, Exception ex)
    {
      var handler = RequestFailed;
      if (handler != null)
        handler(request, ex);
      else
        throw new ApplicationException("RequestFailed event for RequestQueue not handled, and exception occurred. Queue stopped.", ex);
    }

    private void OnProgressIndication(bool working)
    {
      var handler = ProgressIndication;
      if (handler != null)
        handler(working);
    }

    private TRequestParameter WaitForRequest()
    {
      lock (_lockObject)
      {
        WaitForFirstRequest();
        return WaitForAdditionalRequests();
      }
    }

    private TRequestParameter WaitForAdditionalRequests()
    {
      RequestWrapper request = null;
      do
      {
        request = _lastRequest;
        _lastRequest = null;
        if (!request.SkipTimeout)
          Monitor.Wait(_lockObject, _timeout);
      } while (_lastRequest != null);
      return request.Parameter;
    }

    private void WaitForFirstRequest()
    {
      while (_lastRequest == null)
        Monitor.Wait(_lockObject);
    }

    public void Request(TRequestParameter parameter, bool skipTimeout = false)
    {
      lock (_lockObject)
      {
        _lastRequest = new RequestWrapper(parameter, skipTimeout);
        Monitor.PulseAll(_lockObject);
      }
    }

    public void Dispose()
    {
      _stopped = true;
    }

    private class RequestWrapper
    {
      public RequestWrapper(TRequestParameter request, bool respectTimeout)
      {
        Parameter = request;
        SkipTimeout = respectTimeout;
      }

      public TRequestParameter Parameter { get; private set; }
      public bool SkipTimeout { get; set; }
    }
  }
}
