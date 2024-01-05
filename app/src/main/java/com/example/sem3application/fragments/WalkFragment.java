package com.example.sem3application.fragments;

import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sem3application.R;

import java.util.Locale;

public class WalkFragment extends Fragment implements SensorEventListener {
    private SensorManager mSensorManager=null;
    private Sensor stepSensor;
    private int totalSteps=0;
    private int previewsTotalSteps=0;
    private ProgressBar progressBar;
    private TextView steps;
    private EditText goalEditText;

    private TextView distance,calories,tvTimer,notation;
    private ImageView ivPauseResume,stopbtn;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private boolean isRunning = false;
    private boolean isSensorRegistered=false;
    private boolean isStepCountingEnabled = false;

    private int stepsDuringUnregistered = 0;
    private Handler handler = new Handler();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk, container, false);

        goalEditText=view.findViewById(R.id.goalEditText);
        distance=view.findViewById(R.id.distance);
        calories=view.findViewById(R.id.calories);
        tvTimer=view.findViewById(R.id.tvTimer);
        notation=view.findViewById(R.id.notation);
        ivPauseResume=view.findViewById(R.id.ivPauseResume);
        stopbtn=view.findViewById(R.id.stopbtn);

        progressBar = view.findViewById(R.id.progressBar);
        steps = view.findViewById(R.id.steps);
        steps.setText(String.valueOf(0));


        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopClick(v);
            }
        });

        // Set a listener to update progress bar when goal is entered
        goalEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateProgressBar();
                    hideKeyboardAndClearFocus(); // Hide keyboard and clear focus
                    return true;
                }
                return false;
            }
        });

        ivPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseResumeClick(v);
            }
        });

        resetSteps();
        loadData();

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        // Set previewsTotalSteps to the initial value


        return view;
    }

    // Method to hide keyboard and clear focus
    private void hideKeyboardAndClearFocus() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(goalEditText.getWindowToken(), 0);
        goalEditText.clearFocus();
    }



    // Method to update progress bar based on user-entered goal
    private void updateProgressBar() {
        String goalText = goalEditText.getText().toString();
        if (!TextUtils.isEmpty(goalText)) {
            int goalValue = Integer.parseInt(goalText);
            progressBar.setMax(goalValue);
            progressBar.setProgress(0);
        }
    }


    public   void onResume()
    {
        super.onResume();
        if (isSensorRegistered) {
            registerSensor();
        }

    }

    public void onPause()
    {
        super.onPause();
        unregisterSensor();
    }


    public void onPauseResumeClick(View view) {

        if (isSensorRegistered) {
            unregisterSensor();
        } else {
            registerSensor();
        }

        if (isRunning) {
            // Pause
            isRunning = false;
            handler.removeCallbacks(updateTimerTask);
            ivPauseResume.setImageResource(R.drawable.resume);
        } else {
            // Resume
            isRunning = true;
            startTime = SystemClock.elapsedRealtime() - elapsedTime;  // Adjust for paused time
            handler.post(updateTimerTask);
            ivPauseResume.setImageResource(R.drawable.pause);
        }
    }

    public void onStopClick(View view) {
        // Reset timer and step count
        isRunning = false;
        handler.removeCallbacks(updateTimerTask);
        startTime = 0L;
        elapsedTime = 0L;
        tvTimer.setText("0:00");
        distance.setText(String.valueOf(0));
        calories.setText(String.valueOf(0));

        resetSteps();
        unregisterSensor();

        ivPauseResume.setImageResource(R.drawable.resume);  // Set image back to resume icon
    }


    private void registerSensor() {
        if (stepSensor == null) {
            Toast.makeText(getContext(), "Step counter sensor not available", Toast.LENGTH_SHORT).show();
        } else {
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            //Toast.makeText(getContext(), "Step counter sensor Registered", Toast.LENGTH_SHORT).show();
            isSensorRegistered = true;

        }
    }

    // Method to unregister the sensor
    private void unregisterSensor() {
        mSensorManager.unregisterListener(this);
        //Toast.makeText(getContext(), "Step counter sensor Unregistered", Toast.LENGTH_SHORT).show();
        isSensorRegistered = false;


    }

    //private boolean isInitialCountSet = false;

    private int stepLengthInInches = 30; // Assume a default step length in inches

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
           // Toast.makeText(getContext(), "Sensor changed", Toast.LENGTH_SHORT).show();

            totalSteps = (int) event.values[0];
            int currentSteps = totalSteps - previewsTotalSteps;

            // Update step count
            steps.setText(String.valueOf(currentSteps));
            progressBar.setProgress(currentSteps);

            // Calculate and update distance (assuming average stride length)
            double distanceInMiles = calculateDistance(currentSteps, stepLengthInInches);
            distance.setText(String.format(Locale.getDefault(), "%.2f", distanceInMiles));

            // Calculate and update calories burned (assuming a rough estimate)
            double caloriesBurned = calculateCalories(currentSteps);
            calories.setText(String.format(Locale.getDefault(), "%.2f", caloriesBurned));

            if (progressBar.getProgress() >= progressBar.getMax()) {
                // Create a custom Toast with longer duration
                Toast customToast = Toast.makeText(getContext(), "Goal reached!", Toast.LENGTH_LONG);

                // Set a custom view to the Toast (you can design your own layout)
                View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_toast_layout, null);
                customToast.setView(view);

                // Show the custom Toast
                customToast.show();
            }
        }
    }

    private double calculateDistance(int steps, int stepLengthInInches) {
        // Convert steps to miles (assuming average stride length)
        return steps * stepLengthInInches / 63360.0; // 63360 inches in a mile
    }

    private double calculateCalories(int steps) {
        // Convert steps to calories burned (rough estimate)
        // This is a simple conversion, and the actual formula may vary based on factors like weight, speed, etc.
        return steps * 0.04; // Assuming 0.04 calories burned per step
    }




    public void resetSteps()
    {
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Long Press to reset steps", Toast.LENGTH_SHORT).show();


            }
        });

        steps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previewsTotalSteps = totalSteps;
                steps.setText(String.valueOf(0));
                progressBar.setProgress(0);
                saveData();
                return true;
            }
        });
    }


    private Runnable updateTimerTask = new Runnable() {
        @Override
        public void run() {
            elapsedTime = SystemClock.elapsedRealtime() - startTime;
            int seconds = (int) (elapsedTime / 1000);
            int minutes = seconds / 60;
            seconds %= 60;
            tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            handler.postDelayed(this, 1000);
        }
    };


    private void saveData() {
        SharedPreferences sharedPref = requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("key1", String.valueOf(previewsTotalSteps));
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPref = requireContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        // Retrieve the saved string value
        String savedString = sharedPref.getString("key1", null);

        if (savedString != null) {
            try {
                // Try to parse the string as an integer
                previewsTotalSteps = Integer.parseInt(savedString);
            } catch (NumberFormatException e) {
                // Handle the case where the string cannot be parsed as an integer
                previewsTotalSteps = 0;
                Log.e("WalkFragment", "Error parsing saved steps: " + savedString);
            }
        } else {
            // If the saved value does not exist, set steps to 0
            previewsTotalSteps = 0;
        }

        Log.d("WalkFragment", "Loaded steps from SharedPreferences: " + previewsTotalSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
