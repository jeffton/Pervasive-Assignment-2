(function($, goog) {
  $(document).ready(function() {
    var channel, socket, handler, messages = $("#messages > ul"), addMessage;
    
    addMessage = function(msg) {
      var d = new Date();
      messages.prepend('<li><span>[' + d.getHours() + ':' + 
          d.getMinutes() + ':' +
          d.getSeconds() + ']:</span> ' + msg + '</li>');
    };
    
    handler = {
      'onopen' : function() {
        addMessage("Channel open");
      },
      'onmessage' : function(msg) {
        addMessage(JSON.stringify(msg));
      },
      'onerror' : function(err) {
        addMessage("Channel error: " + JSON.stringify(err));
      },
      'onclose' : function() {
        addMessage("Channel close");
      }
    };

    $('#subscribe').on('click', function() {
      $.get("/token", function(res) {
        addMessage("Token received: " + JSON.stringify(res));
        channel = new goog.appengine.Channel(res.token);
        socket = channel.open(handler);
      });
    });
    
    $('#unsubscribe').on('click', function() {
      socket.close();
    });    
  });
})(jQuery, goog);