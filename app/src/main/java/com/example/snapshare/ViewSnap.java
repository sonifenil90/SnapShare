package com.example.snapshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ViewSnap extends AppCompatActivity {

    TextView msgTxt;
    ImageView imageView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);

        msgTxt = (TextView) findViewById(R.id.msgTxt);
        imageView = (ImageView) findViewById(R.id.imageView);

        Intent intent = getIntent();

        msgTxt.setText(intent.getStringExtra("message"));

        DownloadImage task = new DownloadImage();
        Bitmap myImage;
        try{
            myImage = task.execute(intent.getStringExtra("imageUrl")).get();
            imageView.setImageBitmap(myImage);
        }catch (Exception e)
        {
            e.printStackTrace();
        }




    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                URLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(in);

                return bitmap;

            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").child(getIntent().getStringExtra("snapKey")).removeValue();
        FirebaseStorage.getInstance().getReference().child("Images").child(getIntent().getStringExtra("imageName")).delete();
    }
}
