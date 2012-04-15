(function($) {
  $(document).ready(function(){
    var hasUploadUrl = false;
    $("#uploadPhoto").on('submit', function(e){
      var form = $("#uploadPhoto");
                         
      // get upload url
      if(!hasUploadUrl) {
        // prevent the form from submitted as usual
        e.preventDefault();
        
        // get new upload url, need a unique for each upload
        $.get("/getUploadUrl", function(res) {
          form.attr("action", res);
          console.log(res);
          form.submit();
          $("#uploadPhoto").trigger('reset');
        });
        
        hasUploadUrl = true;            
      } else {
        hasUploadUrl = false;            
      }          
    });
    
    $('#viewPhotos').find('input[type="reset"]').on('click', function() {
      $('#viewPhotos').find('input[name="nfcid"]').val('');
      $('#viewPhotos').trigger('submit');
    });
    
    $("#viewPhotos").on("submit", function(e) {
      var nfcid = $(this).find('input[name="nfcid"]').val(),
          url = nfcid !== "" ? "/getPhotoUrlsByNfcId?nfcid=" + nfcid : "/getPhotoUrls";
      
      $.get(url, function(res) {
        var photos = $("#photoslist"), img;
        photos.find('figure, p').remove();
        if($.isArray(res) && res.length > 0) {              
          for(var i=0,j=res.length; i<j; i++) {
            photos.append('<figure><img src="/getPhotoById?id=' + encodeURIComponent(res[i].id) + '" />' + 
                          '<figcaption><span>ID</span>: ' + res[i].id + '<br />' + 
                          '<span>Filename</span>: ' + res[i].filename + '<br />' +
                          '<span>Uploaded On</span>: ' + new Date(res[i].uploadedOn).toString() + '<br />' +
                          '<span>NFC ID</span>: ' + res[i].nfcId + '<br />' +
                          '<a class="delete warning" href="/deletePhotoById?id=' + encodeURIComponent(res[i].id) + '">Delete</a></figcaption></figure>');
          };
        } else {
          photos.append('<p>No photos found!</p>');
        }
        photos.removeClass("hidden");
      });                    
      e.preventDefault();
    }).trigger("submit");
    
    $('#photoslist').on('click', '.delete', function(e) {
      var figure = $(this).parent().parent(),
          url = $(this).attr('href');
  
      figure.remove();              
      $.get(url);          
      e.preventDefault();
    });                
  });
})(jQuery);