package com.example.sem3application.fragments;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.Manifest;



import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.sem3application.R;
import java.util.Locale;

public class WalkFragment extends Fragment implements SensorEventListener {
    private boolean shouldStartTimerAutomatically = true;
    private boolean isUiInitialized = false;
    private static final int REQUEST_ACTIVITY_RECOGNITION_PERMISSION = 100;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private ProgressBar progressBar;
    private TextView tvSteps, tvDistance, tvCalories, tvTimer;
    private EditText editTextStepGoal;
    private ImageView ivPauseResume, stopbtn;
    private boolean isRunning = false;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private Handler handler = new Handler();
    private int stepCount = 0;
    private float distance = 0f; // In meters
    private float calories = 0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk, container, false);

        // Initialize sensor manager and step counter sensor
        //startTime=System.currentTimeMillis();
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        int stepCountTarget=50000;
        // Initialize UI elements
        tvSteps = view.findViewById(R.id.tvSteps);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvCalories = view.findViewById(R.id.tvCalories);
        tvTimer = view.findViewById(R.id.tvTimer);
        editTextStepGoal = view.findViewById(R.id.editTextStepGoal);
        ivPauseResume = view.findViewById(R.id.ivPauseResume);
        stopbtn = view.findViewById(R.id.stopbtn);
        progressBar=view.findViewById(R.id.progressBarGoal);
        progressBar.setMax(stepCountTarget);
        editTextStepGoal.setText(String.valueOf(stepCountTarget));


        if (stepCounterSensor == null) {
            Toast.makeText(getActivity(), "Step counter sensor not available", Toast.LENGTH_SHORT).show();
        }
        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopClick(v);
            }
        });

        ivPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseResumeClick(v);
            }
        });
        requestActivityRecognitionPermission();


        return view;
    }

    private void requestActivityRecognitionPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    REQUEST_ACTIVITY_RECOGNITION_PERMISSION);
        } else {
            // Permission already granted, proceed with sensor registration
            registerSensorListener();
            Log.d("WalkFragment", "Sensor registered successfully");
        }
    }

    private void registerSensorListener() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounterSensor == null) {
            Toast.makeText(getActivity(), "Step counter sensor not available", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
                == PackageManager.PERMISSION_GRANTED) {
            if (stepCounterSensor != null) {
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            // Check if the timer should start automatically
            if (shouldStartTimerAutomatically) {
                startTime = SystemClock.elapsedRealtime() - elapsedTime;  // Adjust for paused time
                handler.post(updateTimerTask);
            }

            // Reset the flag to avoid automatic start on subsequent resumes
            shouldStartTimerAutomatically = false;
        }
    }

    public void onStop()
  {
      super.onStop();
      if(stepCounterSensor!=null)
      {
          sensorManager.unregisterListener(this);

      }
      handler.removeCallbacks(updateTimerTask);


  }


    public void onPauseResumeClick(View view) {
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
        stepCount = 0;
        updateUI();
        ivPauseResume.setImageResource(R.drawable.resume);  // Set image back to resume icon

        // Set the flag to true to start the timer automatically on the next resume
        shouldStartTimerAutomatically = true;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (isUiInitialized) {
                stepCount = (int) event.values[0];
                progressBar.setProgress(stepCount);
                updateUI();  // Call updateUI when sensor values change
            } else {
                // Skip updating UI during the initial phase
                isUiInitialized = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
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

    private void updateUI() {
        Log.d("WalkFragment", "Updating UI: Step Count - " + stepCount);
        tvSteps.setText(String.valueOf(stepCount));
        distance = (float) stepCount * 0.762f;  // Assuming average step length of 0.762 meters
        double resetDistance=0.0;
        tvDistance.setText(String.valueOf(distance));
        calories = (float) stepCount * 0.04f;  // Assuming 0.04 calories burned per step
        double calReset=0.00;
        tvCalories.setText(String.valueOf(calories));

        // Check for step goal completion (if a goal is set)
        String stepGoalString = editTextStepGoal.getText().toString();
        if (!TextUtils.isEmpty(stepGoalString)) {
            int stepGoal = Integer.parseInt(stepGoalString);
            if (stepCount >= stepGoal) {
                // Display a message or take other actions to indicate goal completion
                Toast.makeText(getActivity(), "Step goal reached!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
