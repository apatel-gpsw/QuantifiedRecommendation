package com.example.apatel.quantifiedrecommendation.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apatel.quantifiedrecommendation.R;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Fragment to execute main tasks of the app:
 * 1. Take photo and store it
 * 2. Detect gender
 * 3. Recommend product
 */
public class MainFragment extends Fragment {

    ImageView imageView;
    TextView welcomeText;
    private Bitmap mBitmap;


    public MainFragment() {
        // empty public constructor
    }


    /**
     * On Create method of Fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_main, container, false);
        welcomeText = (TextView) result.findViewById(R.id.welcomeText);

        welcomeText.setText("Let's take a selfie! \n Click on the Pink button below to get started!");

        imageView = (ImageView) result.findViewById(R.id.inputImageView);

        // Blink Animation for ImageView
        final AlphaAnimation blinkanimation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(100);
        blinkanimation.setInterpolator(new LinearInterpolator());
        blinkanimation.setRepeatCount(0); // Repeat animation
        blinkanimation.setRepeatMode(Animation.REVERSE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(blinkanimation);
                detect();
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);

        // Scale Animation for Floating Button
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillBefore(true);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setDuration(100);
        scaleAnimation.setInterpolator(new OvershootInterpolator());

        fab.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) { // When icon is clicked (Released)
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(intent, 0);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) { // When icon is touched
                    fab.startAnimation(scaleAnimation);
                }
                return true;
            }
        });

        return result;
    }

    /**
     * Called when the image is clicked.
     *
     */
    public void detect() {
        // Put the image into an input stream for detection.
        if (mBitmap != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

            // Start a background task to detect faces in the image.
            new DetectionTask().execute(inputStream);
        }
    }

    /**
     * Background task of face detection.
     */
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        private boolean mSucceed = true;

        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.subscription_key));
            try {
                publishProgress("Detecting...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        new FaceServiceClient.FaceAttributeType[]{
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Gender,
                                FaceServiceClient.FaceAttributeType.FacialHair,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.HeadPose
                        });
            } catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(Face[] result) {
            // Show the result on screen when detection is done.
            setUiAfterDetection(result, mSucceed);
        }
    }

    /**
     * Get facial recognition data
     * The API gives out data like Age, Gender, Smile, Facial Hair
     * But for the scope of this app, we'll just use Gender
     *
     * @param result
     * @param succeed
     */
    private void setUiAfterDetection(Face[] result, boolean succeed) {

        if (succeed) {
            // The information about the detection result.
            String gender;
            if (result != null && result.length != 0) {
                gender = result[0].faceAttributes.gender;
                Toast.makeText(getActivity(), "Gender: " + gender, Toast.LENGTH_SHORT).show();
                recommend(gender);
            } else {
                Toast.makeText(getActivity(), "No face detected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Based on the Gender recommend products to the user
     * For the sake of simplicity we've hardcoded the websites and redirecting user to them
     * But if we have another API or activity as recommendation algorithm, we can use them
     *
     * @param gender
     */
    public void recommend(String gender) {
        String url = "";
        String message = "";
        if (gender.equals("male")) {
            url = "http://tinyurl.com/jjcb5nk";
            message = "Since you are a Man you may be interested in a razor";
        } else {
            url = "http://www.sephora.com/mascara";
            message = "Since you are a Woman you may be interested in a mascara";
        }

        // Open a Web Browser with link
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * When app gets image from camera
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            mBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(mBitmap);
            welcomeText.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "Click the image to process", Toast.LENGTH_SHORT).show();
        }
    }
}
