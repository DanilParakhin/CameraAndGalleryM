package com.example.danil.imageapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.AUTHORITY;
import static android.provider.MediaStore.EXTRA_OUTPUT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	TextView tv;
	Button photo, gallery;
	Uri photoURI;
	ImageView imageView;

	private static final int GALLERY = 1;
	private static final int PHOTO = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = findViewById(R.id.tv_uri);
		imageView = findViewById(R.id.image);
		photo = findViewById(R.id.photo);
		gallery = findViewById(R.id.gallery);
		photo.setOnClickListener(this);
		gallery.setOnClickListener(this);



	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.photo:
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (checkSelfPermission(Manifest.permission.CAMERA)
						!= PERMISSION_GRANTED) {
					requestPermissions(new String[]{Manifest.permission.CAMERA},
							PHOTO);
					return;
				}
		}
		dispatchTakePictureIntent();

		break;
			case R.id.gallery:
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, GALLERY );
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri targetURI;
		if (resultCode == RESULT_OK){
			switch (requestCode){
				case GALLERY:
					targetURI = data.getData();
					tv.setText(targetURI.toString());
					Picasso.with(MainActivity.this).load(targetURI).into(imageView);
					break;
				case PHOTO:
//					Bundle extras = data.getExtras();
//					Bitmap bitmap = (Bitmap) extras.get("data");
//					imageView.setImageBitmap(bitmap);
					Picasso.with(this).load(photoURI).into(imageView);
					tv.setText( photoURI.toString());
					break;
			}
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode){
			case PHOTO:{
				if (grantResults[0] == PERMISSION_GRANTED){
					dispatchTakePictureIntent();
				}
			}
			break;

		}
	}
//
//	public void useCamera(){
//		Intent intent = new Intent(ACTION_IMAGE_CAPTURE);
//		intent.putExtra(EXTRA_OUTPUT, generateFileURI();
//		if (intent.resolveActivity(getPackageManager()) != null) {
//			startActivityForResult(intent, PHOTO);
//		}
//
//	}

	String mCurrentPhotoPath;
//
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
//			startActivityForResult(takePictureIntent, PHOTO);
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				photoURI = FileProvider.getUriForFile(getApplicationContext(),
						getApplicationContext().getPackageName() + ".fileprovider",
						photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, PHOTO);
			}
		}
	}
}
