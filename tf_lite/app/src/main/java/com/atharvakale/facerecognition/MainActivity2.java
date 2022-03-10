package com.atharvakale.facerecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity {
    public model my_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void init(View view){
        my_model = new model("mobile_face_net.tflite", MainActivity2.this);
    }
    public void findembeds(View view){
        my_model.getEmbeddings(R.drawable.vkb);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                float[] embeds = my_model.embeds;

                Log.i("EMBEDS : ",Arrays.toString(embeds));

                HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>();
                registered = my_model.readFromSP();
            }
        }, 2000);

//        Log.i("DITANCE : " ,Float.toString(my_model.findDistance(embeds, embeds)));
    }
}