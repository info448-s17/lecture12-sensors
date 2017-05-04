package edu.uw.sensordemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;

public class MotionActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Motion";

    private TextView txtX, txtY, txtZ;
    private FloatingActionButton button;

    private boolean sensorOn;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    //for the emulator!
    private Sensor mMagnetic;
    private Sensor mAccelerometer;
    private float[] gravity;
    private float[] geomagnetic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        //views for easy access
        txtX = (TextView)findViewById(R.id.txt_x);
        txtY = (TextView)findViewById(R.id.txt_y);
        txtZ = (TextView)findViewById(R.id.txt_z);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        }
        else{
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR); //otherwise use the magnetometer-based one
        }

        Log.i(TAG, "Sensors Available: ");
        for(Sensor s : mSensorManager.getSensorList(Sensor.TYPE_ALL))
            Log.i(TAG, s.toString());

        if(mSensor == null) { //we don't have a relevant sensor
            //try to get the raw (emulator) sensors
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if(mAccelerometer == null || mMagnetic == null) {
                Log.v(TAG, "No sensor");
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        //startListener(); //start by default
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopListener();
        super.onPause();
    }

    private void startListener() {
        //register sensor
        if(mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        }
        sensorOn = true;
    }

    private void stopListener() {
        //unregister sensor
        if(mSensor != null)
            mSensorManager.unregisterListener(this, mSensor);
        else {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetic);
        }
        sensorOn = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.v(TAG, "Raw: "+ Arrays.toString(event.values));

        if(mSensor != null) { // hardware
//            txtX.setText(String.format("%.3f",event.values[0]));
//            txtY.setText(String.format("%.3f",event.values[1]));
//            txtZ.setText(String.format("%.3f",event.values[2]));

            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);

            txtX.setText(String.format("%.3f", Math.toDegrees(orientation[1])) + "\u00B0");
            txtY.setText(String.format("%.3f", Math.toDegrees(orientation[2])) + "\u00B0");
            txtZ.setText(String.format("%.3f", Math.toDegrees(orientation[0])) + "\u00B0");
        }
        else { // emulator (for fun)

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                gravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                geomagnetic = event.values;
            if (gravity != null && geomagnetic != null) {
                float rotation[] = new float[9];
                float inclination[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(rotation, inclination, gravity, geomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(rotation, orientation);
                    txtX.setText(String.format("%.3f", Math.toDegrees(orientation[1])) + "\u00B0");
                    txtY.setText(String.format("%.3f", Math.toDegrees(orientation[2])) + "\u00B0");
                    txtZ.setText(String.format("%.3f", Math.toDegrees(orientation[0])) + "\u00B0");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //leave blank for now
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_toggle:
                if(sensorOn) {
                    item.setTitle(getString(R.string.start_menu));
                    stopListener();
                }
                else {
                    item.setTitle(getString(R.string.stop_menu));
                    startListener();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
