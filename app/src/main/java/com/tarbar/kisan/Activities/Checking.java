package com.tarbar.kisan.Activities;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tarbar.kisan.R;

public class Checking extends AppCompatActivity {
    private static final long SPLASH_SCREEN_DELAY = 2000;

    LinearLayout circle_center;
    ImageView correct, not_correct;
    TextView dynamicText;
    boolean isVerified;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountverified);

        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.WHITE);

        circle_center = findViewById(R.id.circle_center);
        correct = findViewById(R.id.correct);
        not_correct = findViewById(R.id.not_correct);
        dynamicText = findViewById(R.id.dynamicText);

        Intent intent = getIntent();
        isVerified = intent.getBooleanExtra("isVerified", false);
        message = intent.getStringExtra("message");
        Class<?> nextActivity = (Class<?>) intent.getSerializableExtra("nextActivity");

        if (isVerified) {
            correct.setVisibility(View.VISIBLE);
            not_correct.setVisibility(View.GONE);
            circle_center.setBackgroundResource(R.drawable.correct_circle);
            animateImage(correct);
        } else {
            correct.setVisibility(View.GONE);
            not_correct.setVisibility(View.VISIBLE);
            circle_center.setBackgroundResource(R.drawable.not_correct_circle);
            animateImage(not_correct);
        }

        dynamicText.setText(message);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent nextIntent = new Intent(Checking.this, nextActivity);
                nextIntent.putExtras(getIntent().getExtras());
                startActivity(nextIntent);
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }

    private void animateImage(ImageView imageView) {
        Keyframe scaleXFrame1 = Keyframe.ofFloat(0f, 0f);
        Keyframe scaleXFrame2 = Keyframe.ofFloat(0.5f, 1f);
        Keyframe scaleXFrame3 = Keyframe.ofFloat(1f, 1f);

        Keyframe scaleYFrame1 = Keyframe.ofFloat(0f, 0f);
        Keyframe scaleYFrame2 = Keyframe.ofFloat(0.5f, 1f);
        Keyframe scaleYFrame3 = Keyframe.ofFloat(1f, 1f);

        Keyframe alphaFrame1 = Keyframe.ofFloat(0f, 0f);
        Keyframe alphaFrame2 = Keyframe.ofFloat(0.5f, 1f);
        Keyframe alphaFrame3 = Keyframe.ofFloat(1f, 1f);

        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofKeyframe("scaleX", scaleXFrame1, scaleXFrame2, scaleXFrame3);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofKeyframe("scaleY", scaleYFrame1, scaleYFrame2, scaleYFrame3);
        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofKeyframe("alpha", alphaFrame1, alphaFrame2, alphaFrame3);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(imageView, scaleXHolder, scaleYHolder, alphaHolder);
        animator.setDuration(2000);
        animator.start();
    }
}
