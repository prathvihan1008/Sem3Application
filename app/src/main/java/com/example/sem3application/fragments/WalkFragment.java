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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sem3application.R;

public class WalkFragment extends Fragment implements SensorEventListener {
    private SensorManager mSensorManager=null;
    private Sensor stepSensor;
    private int totalSteps=0;
    private int previewsTotalSteps=0;
    private ProgressBar progressBar;
    private TextView steps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        steps = view.findViewById(R.id.steps);

        resetSteps();
        loadData();

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        // Set previewsTotalSteps to the initial value


        return view;
    }

    public   void onResume()
    {
        super.onResume();
        if (stepSensor == null) {
            Toast.makeText(getContext(), "Step counter sensor not available", Toast.LENGTH_SHORT).show();
        }
        else {
            mSensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    public void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    //private boolean isInitialCountSet = false;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_STEP_COUNTER)
        {totalSteps=(int)event.values[0];
            int currentSteps=totalSteps-previewsTotalSteps;
            steps.setText(String.valueOf(currentSteps));
            progressBar.setProgress(currentSteps);
        }

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
