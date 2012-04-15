package itu.perv.PicPush;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class PicturePushActivity extends Activity {
	private String URL;

	private static final int SELECT_PHOTO = 100;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// File imgFile = new File("/sdcard/DCIM/Camera/20120407_190530.jpg");
		// if(imgFile.exists()){
		//
		// Bitmap myBitmap =
		// BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		//
		// ImageView myImage = (ImageView) findViewById(R.id.myImage);
		// myImage.setImageBitmap(myBitmap);
		//
		// }

	}


	public void startPhotoPicker(View view) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet get = new HttpGet(
							"http://fluid-photos-at-itu.appspot.com/getUploadUrl");

					ResponseHandler<String> resp = new BasicResponseHandler();
					String uploadURL = httpClient.execute(get, resp);
					
					Uri selectedImage = imageReturnedIntent.getData();
					Log.i("myI", " " +selectedImage);
					Log.i("myI", getPath(selectedImage) );
					String picPath = getPath(selectedImage);

//					 InputStream imageStream = getContentResolver().openInputStream(selectedImage);
//					 Bitmap yourSelectedImage =
//					 BitmapFactory.decodeStream(imageStream);
					// Kode til at sende billeder, kan først testes når webservice er
					// oppe og køre...
//
					 HttpPost httpPost = new HttpPost(uploadURL);
					//
					 	
					 MultipartEntity entity = new MultipartEntity();
					 entity.addPart("photos", new FileBody(new File(picPath)));
					 entity.addPart("nfcid", new StringBody("123"));
					 httpPost.setEntity(entity);
					 HttpResponse res = httpClient.execute(httpPost);
					
					 Toast.makeText(this, "Upload done", Toast.LENGTH_LONG).show();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// 
		}
	}
	public String getPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
}