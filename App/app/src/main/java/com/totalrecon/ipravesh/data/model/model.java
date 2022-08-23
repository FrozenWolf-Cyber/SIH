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
import android.util.Size;

import androidx.annotation.NonNull;

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

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class model {
    FaceDetector detector;
    Interpreter tfLite;
    Tensor outputTensor;
    TensorBuffer outputProbabilityBuffer;

    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
//    float IMAGE_MEAN = 128.0f;
//    float IMAGE_STD = 128.0f;
    public float embeds[];
    Size tfInputSize;
    int OUTPUT_SIZE=512; //Output size of model

    private static final float IMAGE_MEAN = 0;
    private static final float IMAGE_STD = 255.0f;
    private static final TensorOperator PREPROCESS_NORMALIZE_OP =
            new NormalizeOp(IMAGE_MEAN, IMAGE_STD);

    private TensorImage tfInputBuffer = new TensorImage(DataType.UINT8);


    String modelFile="mobile_face_net.tflite"; //model name
    Activity activity;

    public model(String model_name, Activity activity){
        this.modelFile = model_name;
        this.activity = activity;
        try {
            tfLite=new Interpreter(loadModelFile(this.activity,modelFile));
            int[] inputShape = tfLite.getInputTensor(/* inputIndex */ 0).shape();
            tfInputSize =
                    new Size(inputShape[2], inputShape[1]); // Order of axis is: {1, height, width, 3}

            outputTensor = tfLite.getOutputTensor(/* probabilityTensorIndex */ 0);
            outputProbabilityBuffer =
                    TensorBuffer.createFixedSize(outputTensor.shape(), outputTensor.dataType());

        } catch (IOException e) {
            e.printStackTrace();
        }
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);
    }

    private TensorImage loadImage(final Bitmap bitmapBuffer) {
        // Initializes preprocessor if null
        ImageProcessor tfImageProcessor =
                    new ImageProcessor.Builder()
//                            .add(
//                                    new ResizeOp(
//                                            96,
//                                            112,
//                                            ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                            .add(PREPROCESS_NORMALIZE_OP)
                            .build();
            Log.d("EMBEDS DEBUGGG", "tfImageProcessor initialized successfully. imageSize: ");
        tfInputBuffer.load(bitmapBuffer);
        return tfImageProcessor.process(tfInputBuffer);
    }

    public void getEmbeddings(Bitmap img_bitmap){
//        Bitmap img_bitmap = BitmapFactory.decodeResource(activity.getResources(), id);

        InputImage image = InputImage.fromBitmap(img_bitmap, 0);
//        tfInputBuffer = loadImage(img_bitmap);
////
//        ByteBuffer ith_output = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);  // Float tensor, shape 3x2x4.
//        ith_output.order(ByteOrder.nativeOrder());
//
//        Map<Integer, Object> outputMap = new HashMap<>();
//
//        outputMap.put(i, ith_output);
//
//
//        Log.d("TENOR DEBUG", "tensorSize: " + tfInputBuffer.getWidth() + " x " + tfInputBuffer.getHeight());
////
////        // Runs the inference call
//        tfLite.runForMultipleInputsOutputs(tfInputBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());

        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        Log.i("faces",Integer.toString(faces.size()));
                                        if(faces.size()!=0) {
                                            Face face = faces.get(0); //Get first face from detected faces

//                                            //mediaImage to Bitmap
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            img_bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] imageBytes = stream.toByteArray();
//
                                            Bitmap frame_bmp1 =BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);

                                            //Get bounding box of face
//                                            RectF boundingBox = new RectF(face.getBoundingBox());

                                            //Crop out bounding box from whole Bitmap(image)
//                                            Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                                            //Scale the acquired Face to 112*112 which is required input for model
//                                            Bitmap scaled = getResizedBitmap(cropped_face, 96, 112);

                                            recognizeImage(frame_bmp1); //Send scaled bitmap to create face embeddings.

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

    public static float[] bytesToFloats(byte[] bytes) {
        if (bytes.length % Float.BYTES != 0)
            throw new RuntimeException("Illegal length");
        float floats[] = new float[bytes.length / Float.BYTES];
        ByteBuffer.wrap(bytes).asFloatBuffer().get(floats);
        return floats;
    }
    private void recognizeImage(final Bitmap bitmap) {
        //Create ByteBuffer to store normalized image
        Bitmap inputBitmap = Bitmap.createScaledBitmap(
                bitmap,
                112,
                112,
                true
        );
        Log.i("DEBUG BUFFER : ", Float.toString(loadImage((inputBitmap)).getHeight())+ Float.toString(loadImage((inputBitmap)).getWidth()));
        Object[] inputArray = {loadImage((inputBitmap)).getBuffer()};


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
