package com.example.teaapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.teaapplication.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class CameraFragment extends Fragment {

    Button camera, gallery;
    ImageView imageView;
    TextView result;
    int imageSize = 64;
    String y;
    private ImageSlider imageSlider;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        camera = view.findViewById(R.id.button);
        gallery = view.findViewById(R.id.button2);


        result = view.findViewById(R.id.result);
        imageView = view.findViewById(R.id.imageView);

        imageView.setVisibility(View.GONE);
        result.setVisibility(View.GONE);
        imageSlider = view.findViewById(R.id.imageSlider);

        // Create the list for images
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.tea1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.tea2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.tea3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.tea4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.tea5, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.tea6, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.tea7, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.tea8, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });

        return view;
    }


    public void classifyImage(Bitmap image) {
        try {
            Model model = Model.newInstance(requireContext());
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            // iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));  // Normalize to [0, 1]
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            // Set a threshold confidence level, e.g., 95%
            float confidenceThreshold = 0.8f;

            if (maxConfidence > confidenceThreshold) {
                String[] classes = {
                        "H 1/58",
                        "TRI2025",
                        "TRI2027",
                        "TRI4042",
                        "TRI4049"
                };
                y = classes[maxPos];
                result.setText(classes[maxPos] + "\n Confidence: " + maxConfidence);
                // Show the Get Information button when the result is displayed

            } else {
                // If confidence is below the threshold, display a message indicating low confidence
                result.setText("Low confidence" + maxConfidence);
                y="low";
                // Hide the Get Information button when confidence is low

            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 3) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);

                // Create an intent to launch the ResultActivity
                Intent resultIntent = new Intent(requireContext(), ResultActivity.class);

                // Pass data to the ResultActivity
                resultIntent.putExtra("image", image);
                resultIntent.putExtra("result", result.getText().toString());

                // Start the ResultActivity
                startActivity(resultIntent);
            } else {
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), dat);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);

                // Create an intent to launch the ResultActivity
                Intent resultIntent = new Intent(requireContext(), ResultActivity.class);

                // Pass data to the ResultActivity
                resultIntent.putExtra("image", image);
                resultIntent.putExtra("result", result.getText().toString());

                // Start the ResultActivity
                startActivity(resultIntent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}