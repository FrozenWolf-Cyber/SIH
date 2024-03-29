package com.totalrecon.ipravesh.data.model;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class model {
    FaceDetector detector;
    Interpreter tfLite;

    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    public float embeds[];
    int OUTPUT_SIZE=192; //Output size of model

    String modelFile="mobile_face_net.tflite"; //model name
    Activity activity;

    public model(String model_name, Activity activity){
        this.modelFile = model_name;
        this.activity = activity;
        try {
            tfLite=new Interpreter(loadModelFile(this.activity,modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);
    }

    public void getEmbeddings(Bitmap img_bitmap){
//        Bitmap img_bitmap = BitmapFactory.decodeResource(activity.getResources(), id);
        InputImage image = InputImage.fromBitmap(img_bitmap, 0);

        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        Log.i("faces",Integer.toString(faces.size()));
                                        if (faces.size()>1) {
                                            clearEmbedsArray();
                                            // Many faces
                                            embeds = new float[] {-1};
                                        }
                                        else if(faces.size()!=0) {
                                            Face face = faces.get(0); //Get first face from detected faces

//                                            //mediaImage to Bitmap
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            img_bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] imageBytes = stream.toByteArray();
//
                                            Bitmap frame_bmp1 =BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);

                                            //Get bounding box of face
                                            RectF boundingBox = new RectF(face.getBoundingBox());

                                            //Crop out bounding box from whole Bitmap(image)
                                            Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                                            //Scale the acquired Face to 112*112 which is required input for model
                                            Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

                                            recognizeImage(scaled); //Send scaled bitmap to create face embeddings.

                                        }
                                        else {
                                            // If no face exists, clear existing embeds
                                            clearEmbedsArray();
                                        }

                                    }
                                }).addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                                        @Override
                                        public void onComplete(@NonNull Task<List<Face>> task) {
                    }
                });



    }


    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {

        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void recognizeImage(final Bitmap bitmap) {
        //Create ByteBuffer to store normalized image

        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();

        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        //imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();


        embeedings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable
        outputMap.put(0, embeedings);
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model

        this.embeds =  embeedings[0];
    }

    private void clearEmbedsArray() {
        this.embeds = null;
    }

    //Compare Faces by distance between face embeddings
    public float findDistance(float[] emb1, float[] emb2 ) {
        float distance = 0;
        for (int i = 0; i < emb1.length; i++) {
            float diff = emb1[i] - emb2[i];
            distance += diff*diff;
        }
        distance = (float) Math.sqrt(distance);

        return distance;
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas cavas = new Canvas(resultBitmap);

        // draw background
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        cavas.drawRect(
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        cavas.drawBitmap(source, matrix, paint);

        if (source != null && !source.isRecycled()) {
            source.recycle();
        }

        return resultBitmap;
    }

//    Save Faces to Shared Preferences.Conversion of Recognition objects to json string
     public void insertToSP(HashMap<String, SimilarityClassifier.Recognition> jsonMap,boolean clear) {
         if(clear)
             jsonMap.clear();
         else
             jsonMap.putAll(readFromSP());
         String jsonString = new Gson().toJson(jsonMap);

         SharedPreferences sharedPreferences = this.activity.getSharedPreferences("HashMap", MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.putString("map", jsonString);
         editor.apply();
     }
//
     // Load Faces from Shared Preferences.Json String to Recognition object
     public HashMap<String, SimilarityClassifier.Recognition> readFromSP(){
         SharedPreferences sharedPreferences = this.activity.getSharedPreferences("HashMap", MODE_PRIVATE);
         String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
         String json=sharedPreferences.getString("map",defValue);

         TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
         HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());

         for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet())
         {
             float[][] output=new float[1][OUTPUT_SIZE];
             ArrayList arrayList= (ArrayList) entry.getValue().getExtra();
             arrayList = (ArrayList) arrayList.get(0);
             for (int counter = 0; counter < arrayList.size(); counter++) {
                 output[0][counter]= ((Double) arrayList.get(counter)).floatValue();
             }
             entry.getValue().setExtra(output);


         }
         return retrievedMap;
     }


}
