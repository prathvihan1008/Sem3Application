package com.example.sem3application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import com.example.sem3application.CustomSpinnerAdapter;
import com.example.sem3application.fragments.HistoryFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      // Load the initial fragment (HistoryFragment) dynamically
        loadInitialFragment();

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
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    img.setImageResource(R.drawable.baseline_arrow_drop_down_24);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    img.setImageResource(R.drawable.baseline_arrow_drop_up_24);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                updateArrowImage(slideOffset);
            }

            private void updateArrowImage(float slideOffset) {
                float threshold = 0.5f;

                if (slideOffset < threshold) {
                    img.setImageResource(R.drawable.baseline_arrow_drop_up_24);
                } else {
                    img.setImageResource(R.drawable.baseline_arrow_drop_down_24);
                }
            }
        });

        // Initialize the Spinner after the BottomSheetBehavior setup

        Spinner customSpinner = findViewById(R.id.customSpinner);

        List<String> spinnerItems = Arrays.asList("Track Walking", "Track Jogging", "Track Biking"); // Replace with your data

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, spinnerItems);
        customSpinner.setAdapter(adapter);

        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = spinnerItems.get(position);
                loadFragment(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private void loadInitialFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the fragmentContainer with the HistoryFragment
        Fragment initialFragment = new HistoryFragment();
        fragmentTransaction.replace(R.id.fragmentContainer, initialFragment);

        // Commit the transaction
        fragmentTransaction.commit();
    }


    private void loadFragment(String selectedItem) {
        // Implement fragment loading logic based on the selected item
        // You may use a FragmentTransaction to replace the current fragment
        // Example:
        // FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // transaction.replace(R.id.fragmentContainer, YourFragment.newInstance(selectedItem));
        // transaction.addToBackStack(null);
        // transaction.commit();
    }

}
