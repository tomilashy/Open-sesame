package com.project.coen_elec_390;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.animation.Animation;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity {

    private ImageView drop1;
    private ImageView drop2;
    private ImageView drop3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        drop1 = findViewById(R.id.drop1);
        drop2 = findViewById(R.id.drop2);
        drop3 = findViewById(R.id.drop3);

        animateSweat();
    }

    private void animateSweat() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float heightResolution = size.y;
        float pivotResolution = 2028f;

        float translationY1 = (500f / pivotResolution) * heightResolution;
        float translationY2 = (350f / pivotResolution) * heightResolution;
        float translationY3 = (150f / pivotResolution) * heightResolution;

        Log.d("CreditsActivity", "heightResolution =  " + heightResolution);

        ObjectAnimator animation1 = ObjectAnimator.ofFloat(drop1, "translationY", translationY1);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(drop2, "translationY", translationY2);
        ObjectAnimator animation3 = ObjectAnimator.ofFloat(drop3, "translationY", translationY3);

        animation1.setDuration(2000);
        animation2.setDuration(2000);
        animation3.setDuration(2000);

        animation1.setRepeatCount(Animation.INFINITE);
        animation2.setRepeatCount(Animation.INFINITE);
        animation3.setRepeatCount(Animation.INFINITE);

        animation1.start();
        animation2.start();
        animation3.start();

    }
}
