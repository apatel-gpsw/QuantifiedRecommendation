package com.example.apatel.quantifiedrecommendation.Fragments;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apatel.quantifiedrecommendation.R;

/**
 * About the App Fragment with information about the developer
 */
public class AboutFragment extends Fragment {

    TextView aboutAppText;
    ImageView imgFacebook;
    ImageView imgInstagem;
    ImageView imgLinkedIn;
    ImageView imgGithub;

    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * On Create method of Fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
//    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_about, container, false);
        aboutAppText = (TextView) result.findViewById(R.id.textAbout);
        aboutAppText.setText(getString(R.string.About_App));

        // Scale Animation for the profile buttons
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setDuration(300);
        scaleAnimation.setInterpolator(new OvershootInterpolator());

        // LinkedIn
        imgLinkedIn = (ImageView) result.findViewById(R.id.linkedIn);
        imgLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);
                String sharerUrl = "https://www.linkedin.com/in/akashnpatel009";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                startActivity(intent);

            }
        });

        // Facebook
        imgFacebook = (ImageView) result.findViewById(R.id.facebook);
        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/758780260"));
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/akashnpatel")));
                }
            }
        });

        // Instagram
        imgInstagem = (ImageView) result.findViewById(R.id.instagram);
        imgInstagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);
                Uri uri = Uri.parse("http://instagram.com/_u/thepatelguy");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                intent.setPackage("com.instagram.android");

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/xxx")));
                }
            }
        });

        // Github
        imgGithub = (ImageView) result.findViewById(R.id.github);
        imgGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(scaleAnimation);
                String sharerUrl = "https://github.com/apatel-gpsw";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                startActivity(intent);
            }
        });

        return result;
    }
}
