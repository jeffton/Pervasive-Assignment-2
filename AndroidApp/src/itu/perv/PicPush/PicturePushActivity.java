package itu.perv.PicPush;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class PicturePushActivity extends Activity {
    /** Called when the activity is first created. */
	private static final int SELECT_PHOTO = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);  
        
        

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
        case SELECT_PHOTO:
            if(resultCode == RESULT_OK){
            	Uri selectedImage = imageReturnedIntent.getData();
            	 Toast.makeText(PicturePushActivity.this, "pic selected!"+ selectedImage.toString() , Toast.LENGTH_SHORT).show();
            	
            }
//                Uri selectedImage = imageReturnedIntent.getData();
//                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
//                Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
            }
        }
}