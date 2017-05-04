package edu.uw.sensordemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MotionActivity extends AppCompatActivity {

    private static final String TAG = "Motion";

    private TextView txtX, txtY, txtZ;

    private boolean sensorOn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion);

        //views for easy access
        txtX = (TextView)findViewById(R.id.txt_x);
        txtY = (TextView)findViewById(R.id.txt_y);
        txtZ = (TextView)findViewById(R.id.txt_z);

    }


    private void startSensor() {
        sensorOn = true;
    }

    private void stopSensor() {
        sensorOn = false;
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
                    stopSensor();
                }
                else {
                    item.setTitle(getString(R.string.stop_menu));
                    startSensor();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
