package com.example.sem3application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.Menu;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import com.example.sem3application.CustomSpinnerAdapter;
import com.example.sem3application.fragments.BikeFragment;
import com.example.sem3application.fragments.HistoryFragment;
import com.example.sem3application.fragments.WalkFragment;
import com.example.sem3application.fragments.JogFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Load the initial fragment (HistoryFragment) dynamically
        loadInitialFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }



        ImageButton historybtn=findViewById(R.id.historybtn);//
        historybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout sheet = findViewById(R.id.sheet);
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(sheet);


                loadInitialFragment();
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
               // resetBottomSheet();
            }
        });



        ActionBar actionBar = getSupportActionBar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }


        FrameLayout sheet = findViewById(R.id.sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(sheet);
        bottomSheetBehavior.setPeekHeight(410);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

        List<String> spinnerItems = Arrays.asList("Walking", "Jogging", "Biking"); // Replace with your data

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
        FrameLayout sheet = findViewById(R.id.sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(sheet);


        // Replace the fragmentContainer with the HistoryFragment
        Fragment initialFragment = new HistoryFragment();


        fragmentTransaction.replace(R.id.fragmentContainer, initialFragment);


        // Commit the transaction
        fragmentTransaction.commit();
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }




    private void loadFragment(String selectedItem) {
        FrameLayout sheet = findViewById(R.id.sheet);


        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        // Create the appropriate fragment based on the selected item
        if (selectedItem.equals("Walking")) {
            fragment = new WalkFragment();
        } else if (selectedItem.equals("Jogging")) {
            fragment = new JogFragment();
        } else if (selectedItem.equals("Biking")) {
            fragment = new BikeFragment();
        } else {
            // Handle the default case or show an error
            return;
        }

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(sheet);

        // Replace the current fragment with the new one
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack for navigation
        fragmentTransaction.commit();
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

}