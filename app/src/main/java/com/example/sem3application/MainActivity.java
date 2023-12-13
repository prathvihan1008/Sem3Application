package com.example.sem3application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class MainActivity extends AppCompatActivity {
  //  private BottomSheetBehavior bottomSheetBehavior;
    private ImageView img;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);



        if (toolbar != null)

            setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }



        FrameLayout sheet = findViewById(R.id.sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(sheet);
        bottomSheetBehavior.setPeekHeight(490);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        img = findViewById(R.id.img);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Handle state changes
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Handle expanded state
                    // Update your arrow image here
                    img.setImageResource(R.drawable.baseline_arrow_drop_down_24);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Handle collapsed state
                    // Update your arrow image here
                    img.setImageResource(R.drawable.baseline_arrow_drop_up_24);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Handle sliding
                // Update your arrow image based on the slide offset
                updateArrowImage(slideOffset);
            }

            // Method to update arrow image based on slide offset
            private void updateArrowImage(float slideOffset) {
                // Calculate a threshold value for determining when to switch arrow direction
                float threshold = 0.5f;

                if (slideOffset < threshold) {
                    // Set arrow image to downward
                    img.setImageResource(R.drawable.baseline_arrow_drop_up_24);
                } else {
                    // Set arrow image to upward
                    img.setImageResource(R.drawable.baseline_arrow_drop_down_24);
                }
            }
        });














    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_logout) {
            // Handle logout button click here
            // For example, you can show a logout dialog or perform the logout action
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}